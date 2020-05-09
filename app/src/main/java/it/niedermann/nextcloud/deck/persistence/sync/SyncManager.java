package it.niedermann.nextcloud.deck.persistence.sync;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.nextcloud.android.sso.exceptions.NextcloudHttpRequestFailedException;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.api.GsonConfig;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.api.LastSyncUtil;
import it.niedermann.nextcloud.deck.exceptions.OfflineException;
import it.niedermann.nextcloud.deck.model.AccessControl;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.JoinCardWithUser;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.enums.DBStatus;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.model.full.FullStack;
import it.niedermann.nextcloud.deck.model.internal.FilterInformation;
import it.niedermann.nextcloud.deck.model.ocs.Capabilities;
import it.niedermann.nextcloud.deck.model.ocs.comment.DeckComment;
import it.niedermann.nextcloud.deck.model.ocs.comment.OcsComment;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.ServerAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DataBaseAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.LiveDataHelper;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.WrappedLiveData;
import it.niedermann.nextcloud.deck.persistence.sync.helpers.DataPropagationHelper;
import it.niedermann.nextcloud.deck.persistence.sync.helpers.SyncHelper;
import it.niedermann.nextcloud.deck.persistence.sync.helpers.providers.AbstractSyncDataProvider;
import it.niedermann.nextcloud.deck.persistence.sync.helpers.providers.AccessControlDataProvider;
import it.niedermann.nextcloud.deck.persistence.sync.helpers.providers.ActivityDataProvider;
import it.niedermann.nextcloud.deck.persistence.sync.helpers.providers.AttachmentDataProvider;
import it.niedermann.nextcloud.deck.persistence.sync.helpers.providers.BoardDataProvider;
import it.niedermann.nextcloud.deck.persistence.sync.helpers.providers.CardDataProvider;
import it.niedermann.nextcloud.deck.persistence.sync.helpers.providers.CardPropagationDataProvider;
import it.niedermann.nextcloud.deck.persistence.sync.helpers.providers.DeckCommentsDataProvider;
import it.niedermann.nextcloud.deck.persistence.sync.helpers.providers.LabelDataProvider;
import it.niedermann.nextcloud.deck.persistence.sync.helpers.providers.StackDataProvider;
import it.niedermann.nextcloud.deck.util.DateUtil;

import static java.net.HttpURLConnection.HTTP_UNAVAILABLE;

@SuppressWarnings("WeakerAccess")
public class SyncManager {

    @NonNull
    private DataBaseAdapter dataBaseAdapter;
    @NonNull
    private ServerAdapter serverAdapter;

    public SyncManager(@NonNull Context context) {
        this(context, null);
    }

    public SyncManager(@NonNull Context context, @Nullable String ssoAccountName) {
        final Context applicationContext = context.getApplicationContext();
        LastSyncUtil.init(applicationContext);
        this.dataBaseAdapter = new DataBaseAdapter(applicationContext);
        this.serverAdapter = new ServerAdapter(applicationContext, ssoAccountName);
    }

    private void doAsync(Runnable r) {
        new Thread(r).start();
    }

    public MutableLiveData<FullCard> synchronizeCardByRemoteId(long cardRemoteId, Account account) {
        MutableLiveData<FullCard> liveData = new MutableLiveData<>();
        doAsync(() -> {
            Long accountId = account.getId();
            Card card = dataBaseAdapter.getCardByRemoteIdDirectly(accountId, cardRemoteId);
            FullStack stack = dataBaseAdapter.getFullStackByLocalIdDirectly(card.getStackId());
            // only sync this one card.
            stack.setCards(Collections.singletonList(card));
            Board board = dataBaseAdapter.getBoardByLocalIdDirectly(stack.getStack().getBoardId());
            new SyncHelper(serverAdapter, dataBaseAdapter, new Date()).setResponseCallback(new IResponseCallback<Boolean>(account) {
                @Override
                public void onResponse(Boolean response) {
                    FullCard fullCard = dataBaseAdapter.getFullCardByLocalIdDirectly(accountId, card.getLocalId());
                    liveData.postValue(fullCard);
                }

                @Override
                public void onError(Throwable throwable) {
                    liveData.postValue(null);
                }
            }).doSyncFor(new CardDataProvider(null, board, stack));
        });
        return liveData;
    }

    // TODO if the card does not exist yet, try to synchronize it first, instead of directly returning null. If sync failed, return null.
    public LiveData<Long> getLocalBoardIdByCardRemoteIdAndAccount(long cardRemoteId, Account account) {
        return dataBaseAdapter.getLocalBoardIdByCardRemoteIdAndAccountId(cardRemoteId, account.getId());
    }

    public boolean synchronizeEverything() {
        List<Account> accounts = dataBaseAdapter.getAllAccountsDirectly();
        if (accounts.size() > 0) {
            final BooleanResultHolder success = new BooleanResultHolder();
            CountDownLatch latch = new CountDownLatch(accounts.size());
            try {
                for (Account account : accounts) {
                    new SyncManager(dataBaseAdapter.getContext(), account.getName()).synchronize(new IResponseCallback<Boolean>(account) {
                        @Override
                        public void onResponse(Boolean response) {
                            success.result = success.result && Boolean.TRUE.equals(response);
                            latch.countDown();
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            success.result = false;
                            super.onError(throwable);
                            latch.countDown();
                        }
                    });
                }
                latch.await();
                return success.result;
            } catch (InterruptedException e) {
                DeckLog.logError(e);
                return false;
            }
        }
        return true;
    }

    public void synchronize(IResponseCallback<Boolean> responseCallback) {
        if (responseCallback == null ||
                responseCallback.getAccount() == null ||
                responseCallback.getAccount().getId() == null) {
            throw new IllegalArgumentException("please provide an account ID.");
        }
        doAsync(() -> {
            refreshCapabilities(new IResponseCallback<Capabilities>(responseCallback.getAccount()) {
                @Override
                public void onResponse(Capabilities response) {
                    if (!response.isMaintenanceEnabled()) {
                        long accountId = responseCallback.getAccount().getId();
                        Date lastSyncDate = LastSyncUtil.getLastSyncDate(responseCallback.getAccount().getId());
                        Date now = DateUtil.nowInGMT();

                        final SyncHelper syncHelper = new SyncHelper(serverAdapter, dataBaseAdapter, lastSyncDate);

                        IResponseCallback<Boolean> callback = new IResponseCallback<Boolean>(responseCallback.getAccount()) {
                            @Override
                            public void onResponse(Boolean response) {
                                syncHelper.setResponseCallback(new IResponseCallback<Boolean>(account) {
                                    @Override
                                    public void onResponse(Boolean response) {
                                        // TODO deactivate for dev
                                        LastSyncUtil.setLastSyncDate(accountId, now);
                                        responseCallback.onResponse(response);
                                    }

                                    @Override
                                    public void onError(Throwable throwable) {
                                        super.onError(throwable);
                                        responseCallback.onError(throwable);
                                    }
                                });
                                doAsync(() -> {
                                    try {
                                        syncHelper.doUpSyncFor(new BoardDataProvider());
                                    } catch (Throwable e) {
                                        DeckLog.logError(e);
                                        responseCallback.onError(e);
                                    }
                                });

                            }

                            @Override
                            public void onError(Throwable throwable) {
                                super.onError(throwable);
                                responseCallback.onError(throwable);
                            }
                        };

                        syncHelper.setResponseCallback(callback);

                        try {
                            syncHelper.doSyncFor(new BoardDataProvider());
                        } catch (Throwable e) {
                            DeckLog.logError(e);
                            responseCallback.onError(e);
                        }
                    }
                }
            });
        });
    }

//
//    private <T> IResponseCallback<T> wrapCallForUi(IResponseCallback<T> responseCallback) {
//        Account account = responseCallback.getAccount();
//        if (account == null || account.getId() == null) {
//            throw new IllegalArgumentException("Bro. Please just give me a damn Account!");
//        }
//        return new IResponseCallback<T>(responseCallback.getAccount()) {
//            @Override
//            public void onResponse(T response) {
//                sourceActivity.runOnUiThread(() -> {
//                    fillAccountIDs(response);
//                    responseCallback.onResponse(response);
//                });
//            }
//
//            @Override
//            public void onError(Throwable throwable) {
//                responseCallback.onError(throwable);
//            }
//        };
//    }

//    private <T extends AbstractRemoteEntity> T applyUpdatesFromRemote(T localEntity, T remoteEntity, Long accountId) {
//        if (!localEntity.getId().equals(remoteEntity.getId())
//                || !accountId.equals(localEntity.getAccountId())) {
//            throw new IllegalArgumentException("IDs of Account or Entity are not matching! WTF are you doin?!");
//        }
//        remoteEntity.setLastModifiedLocal(remoteEntity.getLastModified()); // not an error! local-modification = remote-mod
//        remoteEntity.setLocalId(localEntity.getLocalId());
//        return remoteEntity;
//    }

    public LiveData<Boolean> hasAccounts() {
        return dataBaseAdapter.hasAccounts();
    }

    public WrappedLiveData<Account> createAccount(Account accout) {
        return dataBaseAdapter.createAccount(accout);
    }

    public boolean hasInternetConnection() {
        return serverAdapter.hasInternetConnection();
    }

    public void deleteAccount(long id) {
        doAsync(() -> {
            dataBaseAdapter.deleteAccount(id);
            LastSyncUtil.resetLastSyncDate(id);
        });
    }

    public void updateAccount(Account account) {
        dataBaseAdapter.updateAccount(account);
    }

    public LiveData<Account> readAccount(long id) {
        return dataBaseAdapter.readAccount(id);
    }

    public LiveData<Account> readAccount(String name) {
        return dataBaseAdapter.readAccount(name);
    }

    public LiveData<List<Account>> readAccounts() {
        return dataBaseAdapter.readAccounts();
    }

    public void refreshCapabilities(IResponseCallback<Capabilities> callback) {
        doAsync(() -> {
            try {
                serverAdapter.getCapabilities(new IResponseCallback<Capabilities>(callback.getAccount()) {
                    @Override
                    public void onResponse(Capabilities response) {
                        Account acc = dataBaseAdapter.getAccountByIdDirectly(account.getId());
                        acc.applyCapabilities(response);
                        dataBaseAdapter.updateAccount(acc);
                        callback.onResponse(response);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        if (throwable instanceof NextcloudHttpRequestFailedException) {
                            NextcloudHttpRequestFailedException requestFailedException = (NextcloudHttpRequestFailedException) throwable;
                            if (requestFailedException.getStatusCode() == HTTP_UNAVAILABLE && requestFailedException.getCause() != null) {
                                String errorString = requestFailedException.getCause().getMessage();
                                Capabilities capabilities = GsonConfig.getGson().fromJson(errorString, Capabilities.class);
                                if (capabilities.isMaintenanceEnabled()) {
                                    doAsync(() -> {
                                        onResponse(capabilities);
                                    });
                                } else {
                                    onError(throwable);
                                }
                            }
                        } else {
                            callback.onError(throwable);
                        }
                    }
                });
            } catch (OfflineException e) {
                callback.onError(e);
            }
        });
    }

    /**
     * @param accountId ID of the account
     * @param archived  Decides whether only archived or not-archived boards for the specified account will be returned
     * @return all archived or non-archived <code>Board</code>s depending on <code>archived</code> parameter
     */
    public LiveData<List<Board>> getBoards(long accountId, boolean archived) {
        return dataBaseAdapter.getBoards(accountId, archived);
    }

    /**
     * @param accountId ID of the account
     * @param archived  Decides whether only archived or not-archived boards for the specified account will be returned
     * @return all archived or non-archived <code>FullBoard</code>s depending on <code>archived</code> parameter
     */
    public LiveData<List<FullBoard>> getFullBoards(long accountId, boolean archived) {
        return dataBaseAdapter.getFullBoards(accountId, archived);
    }

    /**
     * Get all non-archived  <code>FullBoard</code>s with edit permissions for the specified account.
     *
     * @param accountId ID of the account
     * @return all non-archived <code>Board</code>s with edit permission
     */
    public LiveData<List<Board>> getBoardsWithEditPermission(long accountId) {
        return dataBaseAdapter.getBoardsWithEditPermission(accountId);
    }

    public LiveData<Boolean> hasArchivedBoards(long accountId) {
        return dataBaseAdapter.hasArchivedBoards(accountId);
    }

    public LiveData<FullBoard> createBoard(long accountId, Board board) {
        MutableLiveData<FullBoard> liveData = new MutableLiveData<>();
        doAsync(() -> {
            Account account = dataBaseAdapter.getAccountByIdDirectly(accountId);
            User owner = dataBaseAdapter.getUserByUidDirectly(accountId, account.getUserName());
            if (owner == null) {
                DeckLog.log("owner is null - this can be the case if the Deck app has never before been opened in the webinterface");
                liveData.postValue(null);
            } else {
                FullBoard fullBoard = new FullBoard();
                board.setOwnerId(owner.getLocalId());
                fullBoard.setOwner(owner);
                fullBoard.setBoard(board);
                board.setAccountId(accountId);
                fullBoard.setAccountId(accountId);
                new DataPropagationHelper(serverAdapter, dataBaseAdapter).createEntity(new BoardDataProvider(), fullBoard, new IResponseCallback<FullBoard>(account) {
                    @Override
                    public void onResponse(FullBoard response) {
                        liveData.postValue(response);
                    }
                });
            }
        });
        return liveData;
    }

    public LiveData<List<it.niedermann.nextcloud.deck.model.ocs.Activity>> syncActivitiesForCard(Card card) {
        doAsync(() -> {
            if (serverAdapter.hasInternetConnection()) {
                if (card.getId() != null) {
                    new SyncHelper(serverAdapter, dataBaseAdapter, null)
                            .setResponseCallback(new IResponseCallback<Boolean>(dataBaseAdapter.getAccountByIdDirectly(card.getAccountId())) {
                                @Override
                                public void onResponse(Boolean response) {
                                    // do nothing
                                }
                            }).doSyncFor(new ActivityDataProvider(null, card));
                } else {
                    DeckLog.log("Can not fetch activities for card \"" + card.getTitle() + "\" because this card does not have a remote id yet.");
                }
            }
        });
        return dataBaseAdapter.getActivitiesForCard(card.getLocalId());
    }

    public void addCommentToCard(long accountId, long cardId, DeckComment comment) {
        doAsync(() -> {
            Account account = dataBaseAdapter.getAccountByIdDirectly(accountId);
            Card card = dataBaseAdapter.getCardByLocalIdDirectly(accountId, cardId);
            OcsComment commentEntity = OcsComment.of(comment);
            new DataPropagationHelper(serverAdapter, dataBaseAdapter).createEntity(new DeckCommentsDataProvider(null, card), commentEntity, new IResponseCallback<OcsComment>(account) {
                @Override
                public void onResponse(OcsComment response) {
                    // nothing so far
                }
            });
        });
    }

    public void updateComment(long accountId, long localCardId, long localCommentId, String comment) {
        doAsync(() -> {
            Account account = dataBaseAdapter.getAccountByIdDirectly(accountId);
            Card card = dataBaseAdapter.getCardByLocalIdDirectly(accountId, localCardId);
            DeckComment entity = dataBaseAdapter.getCommentByLocalIdDirectly(accountId, localCommentId);
            entity.setMessage(comment);
            OcsComment commentEntity = OcsComment.of(entity);
            new DataPropagationHelper(serverAdapter, dataBaseAdapter).updateEntity(new DeckCommentsDataProvider(null, card), commentEntity, new IResponseCallback<OcsComment>(account) {
                @Override
                public void onResponse(OcsComment response) {
                    // nothing so far
                }
            });
        });
    }

    public WrappedLiveData<Void> deleteComment(long accountId, long localCardId, long localCommentId) {
        WrappedLiveData<Void> liveData = new WrappedLiveData<>();
        doAsync(() -> {
            Account account = dataBaseAdapter.getAccountByIdDirectly(accountId);
            Card card = dataBaseAdapter.getCardByLocalIdDirectly(accountId, localCardId);
            DeckComment entity = dataBaseAdapter.getCommentByLocalIdDirectly(accountId, localCommentId);
            OcsComment commentEntity = OcsComment.of(entity);
            new DataPropagationHelper(serverAdapter, dataBaseAdapter).deleteEntity(new DeckCommentsDataProvider(null, card),
                    commentEntity, getCallbackToLiveDataConverter(account, liveData));
        });
        return liveData;
    }

    public LiveData<List<DeckComment>> getCommentsForLocalCardId(long localCardId) {
        return dataBaseAdapter.getCommentsForLocalCardId(localCardId);
    }

    public WrappedLiveData<Void> deleteBoard(Board board) {
        WrappedLiveData<Void> liveData = new WrappedLiveData<>();
        doAsync(() -> {
            long accountId = board.getAccountId();
            Account account = dataBaseAdapter.getAccountByIdDirectly(accountId);
            FullBoard fullBoard = dataBaseAdapter.getFullBoardByLocalIdDirectly(accountId, board.getLocalId());
            new DataPropagationHelper(serverAdapter, dataBaseAdapter).deleteEntity(new BoardDataProvider(), fullBoard, getCallbackToLiveDataConverter(account, liveData));
        });
        return liveData;
    }

    public WrappedLiveData<FullBoard> updateBoard(FullBoard board) {
        WrappedLiveData<FullBoard> liveData = new WrappedLiveData<>();
        long accountId = board.getAccountId();
        doAsync(() -> {
            Account account = dataBaseAdapter.getAccountByIdDirectly(accountId);
            new DataPropagationHelper(serverAdapter, dataBaseAdapter).updateEntity(new BoardDataProvider(), board, new IResponseCallback<FullBoard>(account) {
                @Override
                public void onResponse(FullBoard response) {
                    liveData.postValue(response);
                }

                @Override
                public void onError(Throwable throwable) {
                    liveData.postError(throwable);
                }
            });
        });
        return liveData;
    }

    public LiveData<List<FullStack>> getStacksForBoard(long accountId, long localBoardId) {
        return dataBaseAdapter.getFullStacksForBoard(accountId, localBoardId);
    }

    public LiveData<FullStack> getStack(long accountId, long localStackId) {
        return dataBaseAdapter.getStack(accountId, localStackId);
    }

    public WrappedLiveData<AccessControl> createAccessControl(long accountId, AccessControl entity) {
        WrappedLiveData<AccessControl> liveData = new WrappedLiveData<>();
        doAsync(() -> {
            Account account = dataBaseAdapter.getAccountByIdDirectly(accountId);
            FullBoard board = dataBaseAdapter.getFullBoardByLocalIdDirectly(accountId, entity.getBoardId());
            new DataPropagationHelper(serverAdapter, dataBaseAdapter).createEntity(
                    new AccessControlDataProvider(null, board, Collections.singletonList(entity)), entity, getCallbackToLiveDataConverter(account, liveData), ((entity1, response) -> {
                        response.setBoardId(entity.getBoardId());
                        response.setUserId(entity.getUser().getLocalId());
                    }
                    )
            );
        });
        return liveData;
    }

    public AccessControl getAccessControlByRemoteIdDirectly(long accountId, Long id) {
        return dataBaseAdapter.getAccessControlByRemoteIdDirectly(accountId, id);
    }

    public LiveData<List<AccessControl>> getAccessControlByLocalBoardId(long accountId, Long id) {
        return dataBaseAdapter.getAccessControlByLocalBoardId(accountId, id);
    }

    public WrappedLiveData<AccessControl> updateAccessControl(AccessControl entity) {
        WrappedLiveData<AccessControl> liveData = new WrappedLiveData<>();
        doAsync(() -> {
            Account account = dataBaseAdapter.getAccountByIdDirectly(entity.getAccountId());
            FullBoard board = dataBaseAdapter.getFullBoardByLocalIdDirectly(entity.getAccountId(), entity.getBoardId());
            new DataPropagationHelper(serverAdapter, dataBaseAdapter).updateEntity(
                    new AccessControlDataProvider(null, board, Collections.singletonList(entity)), entity, getCallbackToLiveDataConverter(account, liveData));
        });
        return liveData;
    }

    private <T> IResponseCallback<T> getCallbackToLiveDataConverter(Account account, WrappedLiveData<T> liveData) {
        return new IResponseCallback<T>(account) {
            @Override
            public void onResponse(T response) {
                liveData.postValue(response);
            }

            @Override
            public void onError(Throwable throwable) {
                liveData.postError(throwable);
            }
        };
    }

    public WrappedLiveData<Void> deleteAccessControl(AccessControl entity) {
        WrappedLiveData<Void> liveData = new WrappedLiveData<>();
        doAsync(() -> {
            Account account = dataBaseAdapter.getAccountByIdDirectly(entity.getAccountId());
            FullBoard board = dataBaseAdapter.getFullBoardByLocalIdDirectly(entity.getAccountId(), entity.getBoardId());
            new DataPropagationHelper(serverAdapter, dataBaseAdapter).deleteEntity(
                    new AccessControlDataProvider(null, board, Collections.singletonList(entity)), entity, new IResponseCallback<Void>(account) {
                        @Override
                        public void onResponse(Void response) {
                            // revoked own board-access?
                            if (entity.getAccountId() == entity.getAccountId() && entity.getUser().getUid().equals(account.getUserName())) {
                                dataBaseAdapter.deleteBoardPhysically(board.getBoard());
                            }
                            liveData.postValue(response);
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            liveData.postError(throwable);
                        }
                    });
        });
        return liveData;
    }

    public LiveData<FullBoard> getFullBoardById(Long accountId, Long localId) {
        return dataBaseAdapter.getFullBoardById(accountId, localId);
    }


    public WrappedLiveData<FullStack> createStack(long accountId, Stack stack) {
        WrappedLiveData<FullStack> liveData = new WrappedLiveData<>();
        doAsync(() -> {
            Account account = dataBaseAdapter.getAccountByIdDirectly(accountId);
            FullBoard board = dataBaseAdapter.getFullBoardByLocalIdDirectly(accountId, stack.getBoardId());
            FullStack fullStack = new FullStack();
            // TODO set stack order to (highest stack-order from board) + 1 and remove logic from caller
            stack.setAccountId(accountId);
            stack.setBoardId(board.getLocalId());
            fullStack.setStack(stack);
            fullStack.setAccountId(accountId);
            new DataPropagationHelper(serverAdapter, dataBaseAdapter).createEntity(new StackDataProvider(null, board), fullStack, getCallbackToLiveDataConverter(account, liveData));
        });
        return liveData;
    }

    public WrappedLiveData<Void> deleteStack(Stack stack) {
        WrappedLiveData<Void> liveData = new WrappedLiveData<>();
        doAsync(() -> {
            Account account = dataBaseAdapter.getAccountByIdDirectly(stack.getAccountId());
            FullStack fullStack = dataBaseAdapter.getFullStackByLocalIdDirectly(stack.getLocalId());
            FullBoard board = dataBaseAdapter.getFullBoardByLocalIdDirectly(stack.getAccountId(), stack.getBoardId());
            new DataPropagationHelper(serverAdapter, dataBaseAdapter).deleteEntity(new StackDataProvider(null, board), fullStack, getCallbackToLiveDataConverter(account, liveData));
        });
        return liveData;
    }

    public WrappedLiveData<FullStack> updateStack(FullStack stack) {
        WrappedLiveData<FullStack> liveData = new WrappedLiveData<>();
        doAsync(() -> {
            Account account = dataBaseAdapter.getAccountByIdDirectly(stack.getAccountId());
            FullBoard board = dataBaseAdapter.getFullBoardByLocalIdDirectly(stack.getAccountId(), stack.getStack().getBoardId());
            updateStack(account, board, stack, liveData);
        });
        return liveData;

    }

    private void updateStack(@NonNull Account account, @NonNull FullBoard board, @NonNull FullStack stack, @Nullable WrappedLiveData<FullStack> liveData) {
        doAsync(() -> {
            new DataPropagationHelper(serverAdapter, dataBaseAdapter).updateEntity(new StackDataProvider(null, board), stack, new IResponseCallback<FullStack>(account) {
                @Override
                public void onResponse(FullStack response) {
                    if (liveData != null) {
                        liveData.postValue(response);
                    }
                }

                @Override
                public void onError(Throwable throwable) {
                    if (liveData != null) {
                        liveData.postError(throwable);
                    }
                }
            });
        });
    }

    /**
     * Swaps around the order of the given stackLocalIds
     *
     * @param stackLocalIds The first item of the pair will be updated first
     */
    public void swapStackOrder(long accountId, long boardLocalId, @NonNull Pair<Long, Long> stackLocalIds) {
        if (stackLocalIds.first == null || stackLocalIds.second == null) {
            throw new IllegalArgumentException("Given stackLocalIds must not be null");
        }
        doAsync(() -> {
            Account account = dataBaseAdapter.getAccountByIdDirectly(accountId);
            FullBoard fullBoard = dataBaseAdapter.getFullBoardByLocalIdDirectly(accountId, boardLocalId);
            Pair<FullStack, FullStack> stacks = new Pair<>(
                    dataBaseAdapter.getFullStackByLocalIdDirectly(stackLocalIds.first),
                    dataBaseAdapter.getFullStackByLocalIdDirectly(stackLocalIds.second)
            );
            assert stacks.first != null;
            assert stacks.second != null;
            int orderFirst = stacks.first.getStack().getOrder();
            stacks.first.getStack().setOrder(stacks.second.getStack().getOrder());
            stacks.second.getStack().setOrder(orderFirst);
            updateStack(account, fullBoard, stacks.first, null);
            updateStack(account, fullBoard, stacks.second, null);
        });
    }

    public LiveData<FullCard> getCardByLocalId(long accountId, long cardLocalId) {
        return dataBaseAdapter.getCardByLocalId(accountId, cardLocalId);
    }

    public LiveData<List<FullCard>> getFullCardsForStack(long accountId, long localStackId, @Nullable FilterInformation filter) {
        return dataBaseAdapter.getFullCardsForStack(accountId, localStackId, filter);
    }

    public LiveData<Integer> countCardsInStack(long accountId, long localStackId) {
        return dataBaseAdapter.countCardsInStack(accountId, localStackId);
    }

    public LiveData<Integer> countCardsWithLabel(long localLabelId) {
        return dataBaseAdapter.countCardsWithLabel(localLabelId);
    }

    // TODO implement, see https://github.com/stefan-niedermann/nextcloud-deck/issues/395
    public LiveData<List<FullCard>> getArchivedFullCardsForBoard(long accountId, long localBoardId) {
        MutableLiveData<List<FullCard>> dummyData = new MutableLiveData<>();
        dummyData.postValue(new ArrayList<>());
        return dummyData;
    }

//    public LiveData<FullCard> createCard(long accountId, long localBoardId, long localStackId, Card card) {
//
//        MutableLiveData<FullCard> liveData = new MutableLiveData<>();
//        doAsync(() -> {
//            Account account = dataBaseAdapter.getAccountByIdDirectly(accountId);
//            User owner = dataBaseAdapter.getUserByUidDirectly(accountId, account.getUserName());
//            FullStack stack = dataBaseAdapter.getFullStackByLocalIdDirectly(localStackId);
//            Board board = dataBaseAdapter.getBoardByLocalIdDirectly(localBoardId);
//            card.setStackId(stack.getLocalId());
//            FullCard fullCard = new FullCard();
//            fullCard.setCard(card);
//            fullCard.setOwner(owner);
//            fullCard.setAccountId(accountId);
//            new DataPropagationHelper(serverAdapter, dataBaseAdapter).createEntity(new CardPropagationDataProvider(null, board, stack), fullCard, new IResponseCallback<FullCard>(account) {
//                @Override
//                public void onResponse(FullCard response) {
//                    liveData.postValue(response);
//                }
//            }, (FullCard entity, FullCard response) -> {
//                response.getCard().setUserId(entity.getCard().getUserId());
//                response.getCard().setStackId(stack.getLocalId());
//            });
//        });
//        return liveData;
//    }

    public LiveData<FullCard> createFullCard(long accountId, long localBoardId, long localStackId, FullCard card) {

        MutableLiveData<FullCard> liveData = new MutableLiveData<>();
        doAsync(() -> {
            Account account = dataBaseAdapter.getAccountByIdDirectly(accountId);
            User owner = dataBaseAdapter.getUserByUidDirectly(accountId, account.getUserName());
            FullStack stack = dataBaseAdapter.getFullStackByLocalIdDirectly(localStackId);
            Board board = dataBaseAdapter.getBoardByLocalIdDirectly(localBoardId);
            card.getCard().setUserId(owner.getLocalId());
            card.getCard().setStackId(stack.getLocalId());
            card.getCard().setAccountId(accountId);
            card.getCard().setStatusEnum(DBStatus.LOCAL_EDITED);
            long localCardId = dataBaseAdapter.createCard(accountId, card.getCard());
            card.getCard().setLocalId(localCardId);

            List<User> assignedUsers = card.getAssignedUsers();
            if (assignedUsers != null) {
                for (User assignedUser : assignedUsers) {
                    dataBaseAdapter.createJoinCardWithUser(assignedUser.getLocalId(), localCardId, DBStatus.LOCAL_EDITED);
                }
            }

            List<Label> labels = card.getLabels();
            if (labels != null) {
                for (Label label : labels) {
                    dataBaseAdapter.createJoinCardWithLabel(label.getLocalId(), localCardId, DBStatus.LOCAL_EDITED);
                }
            }

            if (card.getAttachments() != null) {
                for (Attachment attachment : card.getAttachments()) {
                    if (attachment.getLocalId() == null) {
                        attachment.setCardId(localCardId);
                        dataBaseAdapter.createAttachment(accountId, attachment);
                    }
                }
            }

            liveData.postValue(card);
            if (serverAdapter.hasInternetConnection()) {
                new SyncHelper(serverAdapter, dataBaseAdapter, null)
                        .setResponseCallback(IResponseCallback.getDefaultResponseCallback(account))
                        .doUpSyncFor(new CardDataProvider(null, board, stack));
            }
        });
        return liveData;
    }

    public WrappedLiveData<Void> deleteCard(Card card) {
        WrappedLiveData<Void> liveData = new WrappedLiveData<>();
        doAsync(() -> {
            FullCard fullCard = dataBaseAdapter.getFullCardByLocalIdDirectly(card.getAccountId(), card.getLocalId());
            if (fullCard == null) {
                throw new IllegalArgumentException("card to delete does not exist.");
            }
            Account account = dataBaseAdapter.getAccountByIdDirectly(card.getAccountId());
            FullStack stack = dataBaseAdapter.getFullStackByLocalIdDirectly(card.getStackId());
            Board board = dataBaseAdapter.getBoardByLocalIdDirectly(stack.getStack().getBoardId());
            new DataPropagationHelper(serverAdapter, dataBaseAdapter).deleteEntity(new CardPropagationDataProvider(null, board, stack), fullCard, getCallbackToLiveDataConverter(account, liveData));
        });
        return liveData;
    }

    public WrappedLiveData<FullCard> archiveCard(FullCard card) {
        card.getCard().setArchived(true);
        return updateCard(card);
    }

    public WrappedLiveData<FullCard> dearchiveCard(FullCard card) {
        card.getCard().setArchived(false);
        return updateCard(card);
    }

    public void archiveBoard(Board board) {
        doAsync(() -> {
            FullBoard b = dataBaseAdapter.getFullBoardByLocalIdDirectly(board.getAccountId(), board.getLocalId());
            b.getBoard().setArchived(true);
            updateBoard(b);
        });
    }

    public void dearchiveBoard(Board board) {
        doAsync(() -> {
            FullBoard b = dataBaseAdapter.getFullBoardByLocalIdDirectly(board.getAccountId(), board.getLocalId());
            b.getBoard().setArchived(false);
            updateBoard(b);
        });
    }

    public WrappedLiveData<FullCard> updateCard(FullCard card) {
        WrappedLiveData<FullCard> liveData = new WrappedLiveData<>();
        doAsync(() -> {
            FullCard fullCardFromDB = dataBaseAdapter.getFullCardByLocalIdDirectly(card.getAccountId(), card.getLocalId());
            if (fullCardFromDB == null) {
                throw new IllegalArgumentException("card to update does not exist.");
            }

            dataBaseAdapter.filterRelationsForCard(fullCardFromDB);
            List<User> deletedUsers = AbstractSyncDataProvider.findDelta(card.getAssignedUsers(), fullCardFromDB.getAssignedUsers());
            List<User> addedUsers = AbstractSyncDataProvider.findDelta(fullCardFromDB.getAssignedUsers(), card.getAssignedUsers());
            for (User addedUser : addedUsers) {
                dataBaseAdapter.createJoinCardWithUser(addedUser.getLocalId(), card.getLocalId(), DBStatus.LOCAL_EDITED);
            }
            for (User deletedUser : deletedUsers) {
                dataBaseAdapter.deleteJoinedUserForCard(card.getLocalId(), deletedUser.getLocalId());
            }

            List<Label> deletedLabels = AbstractSyncDataProvider.findDelta(card.getLabels(), fullCardFromDB.getLabels());
            List<Label> addedLabels = AbstractSyncDataProvider.findDelta(fullCardFromDB.getLabels(), card.getLabels());
            for (Label addedLabel : addedLabels) {
                dataBaseAdapter.createJoinCardWithLabel(addedLabel.getLocalId(), card.getLocalId(), DBStatus.LOCAL_EDITED);
            }
            for (Label deletedLabel : deletedLabels) {
                dataBaseAdapter.deleteJoinedLabelForCard(card.getLocalId(), deletedLabel.getLocalId());
            }

            FullStack stack = dataBaseAdapter.getFullStackByLocalIdDirectly(card.getCard().getStackId());
            Board board = dataBaseAdapter.getBoardByLocalIdDirectly(stack.getStack().getBoardId());
            fullCardFromDB.setCard(card.getCard());
            card.getCard().setStatus(DBStatus.LOCAL_EDITED.getId());
            dataBaseAdapter.updateCard(card.getCard(), false);
            if (serverAdapter.hasInternetConnection()) {
                Account account = dataBaseAdapter.getAccountByIdDirectly(card.getAccountId());
                new SyncHelper(serverAdapter, dataBaseAdapter, null)
                        .setResponseCallback(new IResponseCallback<Boolean>(account) {
                            @Override
                            public void onResponse(Boolean response) {
                                liveData.postValue(dataBaseAdapter.getFullCardByLocalIdDirectly(card.getAccountId(), card.getLocalId()));
                            }

                            @Override
                            public void onError(Throwable throwable) {
                                liveData.postError(throwable);
                            }
                        }).doUpSyncFor(new CardPropagationDataProvider(null, board, stack));
            } else {
                liveData.postValue(card);
            }
        });
        return liveData;
    }

    public WrappedLiveData<Label> createLabel(long accountId, Label label, long localBoardId) {
        WrappedLiveData<Label> liveData = new WrappedLiveData<>();
        doAsync(() -> {
            Label existing = dataBaseAdapter.getLabelByBoardIdAndTitleDirectly(label.getBoardId(), label.getTitle());
            if (existing != null) {
                liveData.postError(new SQLiteConstraintException("label \"" + label.getTitle() + "\" already exists for this board!"));
                return;
            }
            Account account = dataBaseAdapter.getAccountByIdDirectly(accountId);
            Board board = dataBaseAdapter.getBoardByLocalIdDirectly(localBoardId);
            label.setAccountId(accountId);
            new DataPropagationHelper(serverAdapter, dataBaseAdapter).createEntity(new LabelDataProvider(null, board, null), label, new IResponseCallback<Label>(account) {
                @Override
                public void onResponse(Label response) {
                    liveData.postValue(response);
                }

                @Override
                public void onError(Throwable throwable) {
                    liveData.postError(throwable);
                }
            }, (entity, response) -> {
                response.setBoardId(board.getLocalId());
            });
        });
        return liveData;
    }

    public MutableLiveData<Label> createAndAssignLabelToCard(long accountId, Label label, long localCardId) {
        MutableLiveData<Label> liveData = new MutableLiveData<>();
        doAsync(() -> {
            Account account = dataBaseAdapter.getAccountByIdDirectly(accountId);
            Board board = dataBaseAdapter.getBoardByLocalCardIdDirectly(localCardId);
            label.setAccountId(accountId);
            new DataPropagationHelper(serverAdapter, dataBaseAdapter).createEntity(new LabelDataProvider(null, board, null), label, new IResponseCallback<Label>(account) {
                @Override
                public void onResponse(Label response) {
                    assignLabelToCard(response, dataBaseAdapter.getCardByLocalIdDirectly(accountId, localCardId));
                    liveData.postValue(response);
                }

                @Override
                public void onError(Throwable throwable) {
                    super.onError(throwable);
                    assignLabelToCard(label, dataBaseAdapter.getCardByLocalIdDirectly(accountId, localCardId));
                }
            }, (entity, response) -> {
                response.setBoardId(board.getLocalId());
            });
        });
        return liveData;
    }

    public WrappedLiveData<Void> deleteLabel(Label label) {
        WrappedLiveData<Void> liveData = new WrappedLiveData<>();
        doAsync(() -> {
            Account account = dataBaseAdapter.getAccountByIdDirectly(label.getAccountId());
            Board board = dataBaseAdapter.getBoardByLocalIdDirectly(label.getBoardId());
            new DataPropagationHelper(serverAdapter, dataBaseAdapter)
                    .deleteEntity(new LabelDataProvider(null, board, Collections.emptyList()), label, getCallbackToLiveDataConverter(account, liveData));
        });
        return liveData;
    }

    public WrappedLiveData<Label> updateLabel(Label label) {
        WrappedLiveData<Label> liveData = new WrappedLiveData<>();
        doAsync(() -> {
            Account account = dataBaseAdapter.getAccountByIdDirectly(label.getAccountId());
            Board board = dataBaseAdapter.getBoardByLocalIdDirectly(label.getBoardId());
            new DataPropagationHelper(serverAdapter, dataBaseAdapter)
                    .updateEntity(new LabelDataProvider(null, board, Collections.emptyList()), label, getCallbackToLiveDataConverter(account, liveData));
        });
        return liveData;
    }

    public void assignUserToCard(User user, Card card) {
        doAsync(() -> {
            final long localUserId = user.getLocalId();
            final long localCardId = card.getLocalId();
            JoinCardWithUser joinCardWithUser = dataBaseAdapter.getJoinCardWithUser(localUserId, localCardId);
            if (joinCardWithUser != null && joinCardWithUser.getStatus() != DBStatus.LOCAL_DELETED.getId()) {
                return;
            }
            dataBaseAdapter.createJoinCardWithUser(localUserId, localCardId, DBStatus.LOCAL_EDITED);
            Stack stack = dataBaseAdapter.getStackByLocalIdDirectly(card.getStackId());
            Board board = dataBaseAdapter.getBoardByLocalIdDirectly(stack.getBoardId());
            Account account = dataBaseAdapter.getAccountByIdDirectly(card.getAccountId());
            if (serverAdapter.hasInternetConnection()) {
                serverAdapter.assignUserToCard(board.getId(), stack.getId(), card.getId(), user.getUid(), new IResponseCallback<Void>(account) {

                    @Override
                    public void onResponse(Void response) {
                        dataBaseAdapter.setStatusForJoinCardWithUser(localCardId, localUserId, DBStatus.UP_TO_DATE.getId());
                    }
                });
            }
        });
    }

    public void assignLabelToCard(Label label, Card card) {
        doAsync(() -> {
            final long localLabelId = label.getLocalId();
            final long localCardId = card.getLocalId();
            dataBaseAdapter.createJoinCardWithLabel(localLabelId, localCardId, DBStatus.LOCAL_EDITED);
            if (label.getId() == null || card.getId() == null) {
                return;
            }
            Stack stack = dataBaseAdapter.getStackByLocalIdDirectly(card.getStackId());
            Board board = dataBaseAdapter.getBoardByLocalIdDirectly(stack.getBoardId());
            Account account = dataBaseAdapter.getAccountByIdDirectly(card.getAccountId());
            if (serverAdapter.hasInternetConnection()) {
                serverAdapter.assignLabelToCard(board.getId(), stack.getId(), card.getId(), label.getId(), new IResponseCallback<Void>(account) {

                    @Override
                    public void onResponse(Void response) {
                        dataBaseAdapter.setStatusForJoinCardWithLabel(localCardId, localLabelId, DBStatus.UP_TO_DATE.getId());
                    }
                });
            }
        });
    }

    public void unassignLabelFromCard(Label label, Card card) {
        doAsync(() -> {
            dataBaseAdapter.deleteJoinedLabelForCard(card.getLocalId(), label.getLocalId());
            Stack stack = dataBaseAdapter.getStackByLocalIdDirectly(card.getStackId());
            Board board = dataBaseAdapter.getBoardByLocalIdDirectly(stack.getBoardId());
            Account account = dataBaseAdapter.getAccountByIdDirectly(card.getAccountId());
            if (serverAdapter.hasInternetConnection()) {
                serverAdapter.unassignLabelFromCard(board.getId(), stack.getId(), card.getId(), label.getId(), new IResponseCallback<Void>(account) {
                    @Override
                    public void onResponse(Void response) {
                        dataBaseAdapter.deleteJoinedLabelForCardPhysically(card.getLocalId(), label.getLocalId());
                    }
                });
            }
        });
    }

    public void unassignUserFromCard(User user, Card card) {
        doAsync(() -> {
            dataBaseAdapter.deleteJoinedUserForCard(card.getLocalId(), user.getLocalId());
            if (serverAdapter.hasInternetConnection()) {
                Stack stack = dataBaseAdapter.getStackByLocalIdDirectly(card.getStackId());
                Board board = dataBaseAdapter.getBoardByLocalIdDirectly(stack.getBoardId());
                Account account = dataBaseAdapter.getAccountByIdDirectly(card.getAccountId());
                serverAdapter.unassignUserFromCard(board.getId(), stack.getId(), card.getId(), user.getUid(), new IResponseCallback<Void>(account) {
                    @Override
                    public void onResponse(Void response) {
                        dataBaseAdapter.deleteJoinedUserForCardPhysically(card.getLocalId(), user.getLocalId());
                    }
                });
            }
        });
    }

    public LiveData<List<User>> findProposalsForUsersToAssign(final long accountId, long boardId, long notAssignedToLocalCardId, final int topX) {
        return dataBaseAdapter.findProposalsForUsersToAssign(accountId, boardId, notAssignedToLocalCardId, topX);
    }

    public LiveData<List<User>> findProposalsForUsersToAssign(final long accountId, long boardId) {
        return dataBaseAdapter.findProposalsForUsersToAssign(accountId, boardId, -1L, -1);
    }

    public LiveData<List<User>> findProposalsForUsersToAssignForACL(final long accountId, long boardId, final int topX) {
        return dataBaseAdapter.findProposalsForUsersToAssignForACL(accountId, boardId, topX);
    }

    public LiveData<List<Label>> findProposalsForLabelsToAssign(final long accountId, final long boardId, long notAssignedToLocalCardId) {
        return dataBaseAdapter.findProposalsForLabelsToAssign(accountId, boardId, notAssignedToLocalCardId);
    }

    public LiveData<List<Label>> findProposalsForLabelsToAssign(final long accountId, final long boardId) {
        return findProposalsForLabelsToAssign(accountId, boardId, -1L);
    }

    // TODO Difference to getFullBoardByid() ??? I think those methods are equal, we should drop one of them.
    public LiveData<FullBoard> getFullBoard(Long accountId, Long localId) {
        return dataBaseAdapter.getFullBoardById(accountId, localId);
    }

    public LiveData<User> getUserByLocalId(long accountId, long localId) {
        return dataBaseAdapter.getUserByLocalId(accountId, localId);
    }

    public LiveData<User> getUserByUid(long accountId, String uid) {
        return dataBaseAdapter.getUserByUid(accountId, uid);
    }

    public User getUserByUidDirectly(long accountId, String uid) {
        return dataBaseAdapter.getUserByUidDirectly(accountId, uid);
    }

    public LiveData<List<User>> searchUserByUidOrDisplayName(final long accountId, final long notYetAssignedToLocalCardId, final String searchTerm) {
        return dataBaseAdapter.searchUserByUidOrDisplayName(accountId, notYetAssignedToLocalCardId, searchTerm);
    }

    public LiveData<List<User>> searchUserByUidOrDisplayNameForACL(final long accountId, final long notYetAssignedInACL, final String searchTerm) {
        return dataBaseAdapter.searchUserByUidOrDisplayNameForACL(accountId, notYetAssignedInACL, searchTerm);
    }

    public LiveData<Board> getBoard(long accountId, long remoteId) {
        return dataBaseAdapter.getBoard(accountId, remoteId);
    }

    public LiveData<Stack> getStackByRemoteId(long accountId, long localBoardId, long remoteId) {
        return dataBaseAdapter.getStackByRemoteId(accountId, localBoardId, remoteId);
    }

    public LiveData<Card> getCardByRemoteID(long accountId, long remoteId) {
        return dataBaseAdapter.getCardByRemoteID(accountId, remoteId);
    }

    public long createUser(long accountId, User user) {
        return dataBaseAdapter.createUser(accountId, user);
    }

    public void updateUser(long accountId, User user) {
        dataBaseAdapter.updateUser(accountId, user, true);
    }

    public LiveData<List<Label>> searchNotYetAssignedLabelsByTitle(final long accountId, final long boardId, final long notYetAssignedToLocalCardId, String searchTerm) {
        return dataBaseAdapter.searchNotYetAssignedLabelsByTitle(accountId, boardId, notYetAssignedToLocalCardId, searchTerm);
    }

    public String getServerUrl() {
        return serverAdapter.getServerUrl();
    }

    /**
     * @see <a href="https://github.com/stefan-niedermann/nextcloud-deck/issues/360">reenable reorder</a>
     */
    public void reorder(long accountId, FullCard movedCard, long newStackId, int newIndex) {
        doAsync(() -> {
            // read cards of new stack
            List<FullCard> cardsOfNewStack = dataBaseAdapter.getFullCardsForStackDirectly(accountId, newStackId);
            int newOrder = newIndex;
            if (cardsOfNewStack.size() > newIndex) {
                newOrder = cardsOfNewStack.get(newIndex).getCard().getOrder();
            }
            if (newOrder == movedCard.getCard().getOrder() && newStackId == movedCard.getCard().getStackId()) {
                return;
            }
//            if (serverAdapter.hasInternetConnection()){
//                // call reorder
//                Stack stack = dataBaseAdapter.getStackByLocalIdDirectly(movedCard.getCard().getStackId());
//                Stack newStack = newStackId == stack.getLocalId() ? stack :  dataBaseAdapter.getStackByLocalIdDirectly(newStackId);
//                Board board = dataBaseAdapter.getBoardByLocalIdDirectly(stack.getBoardId());
//                Account account = dataBaseAdapter.getAccountByIdDirectly(movedCard.getCard().getAccountId());
//                movedCard.getCard().setStackId(newStackId);
//                movedCard.getCard().setOrder(newOrder);
//                movedCard.setStatusEnum(DBStatus.LOCAL_MOVED);
//                dataBaseAdapter.updateCard(movedCard.getCard(), false);
//                serverAdapter.reorder(board.getId(), stack.getId(), movedCard.getId(), newStack.getId(), newOrder+1, new IResponseCallback<List<FullCard>>(account){
//
//                    @Override
//                    public void onResponse(List<FullCard> response) {
//                        for (FullCard fullCard : response) {
//                            Card card = fullCard.getCard();
//                            card.setAccountId(accountId);
//                            card.setStackId(dataBaseAdapter.getLocalStackIdByRemoteStackIdDirectly(accountId, card.getStackId()));
//                            card.setStatusEnum(DBStatus.UP_TO_DATE);
//                            dataBaseAdapter.updateCard(card, false);
//                            DeckLog.log("move: stackid "+card.getStackId());
//                        }
//                        movedCard.setStatusEnum(DBStatus.UP_TO_DATE);
//                        dataBaseAdapter.updateCard(movedCard.getCard(), false);
//                    }
//                });
//            } else {
            reorderLocally(cardsOfNewStack, movedCard, newStackId, newOrder);
            //FIXME: remove the sync-block, when commentblock up there is activated. (waiting for deck server bugfix)
            if (hasInternetConnection()) {
                Stack stack = dataBaseAdapter.getStackByLocalIdDirectly(movedCard.getCard().getStackId());
                FullBoard board = dataBaseAdapter.getFullBoardByLocalIdDirectly(accountId, stack.getBoardId());
                Account account = dataBaseAdapter.getAccountByIdDirectly(movedCard.getCard().getAccountId());
                new SyncHelper(serverAdapter, dataBaseAdapter, new Date()).setResponseCallback(new IResponseCallback<Boolean>(account) {
                    @Override
                    public void onResponse(Boolean response) {
                        // doNothing();
                    }
                }).doUpSyncFor(new StackDataProvider(null, board));
            }
//        }
        });
    }


    private void reorderLocally(List<FullCard> cardsOfNewStack, FullCard movedCard, long newStackId, int newOrder) {
        // set new stack and order
        Card movedInnerCard = movedCard.getCard();
        int oldOrder = movedInnerCard.getOrder();
        long oldStackId = movedInnerCard.getStackId();


        List<Card> changedCards = new ArrayList<>();

        int startingAtOrder = newOrder;
        if (oldStackId == newStackId) {
            // card was only reordered in the same stack
            movedInnerCard.setStatusEnum(movedInnerCard.getStatus() == DBStatus.LOCAL_MOVED.getId() ? DBStatus.LOCAL_MOVED : DBStatus.LOCAL_EDITED);
            // move direction?
            if (oldOrder > newOrder) {
                // up
                changedCards.add(movedCard.getCard());
                for (FullCard cardToUpdate : cardsOfNewStack) {
                    Card cardEntity = cardToUpdate.getCard();
                    if (cardEntity.getOrder() < newOrder) {
                        continue;
                    }
                    if (cardEntity.getOrder() >= oldOrder) {
                        break;
                    }
                    changedCards.add(cardEntity);
                }
            } else {
                // down
                startingAtOrder = oldOrder;
                for (FullCard cardToUpdate : cardsOfNewStack) {
                    Card cardEntity = cardToUpdate.getCard();
                    if (cardEntity.getOrder() <= oldOrder) {
                        continue;
                    }
                    if (cardEntity.getOrder() > newOrder) {
                        break;
                    }
                    changedCards.add(cardEntity);
                }
                changedCards.add(movedCard.getCard());
            }
        } else {
            // card was moved to an other stack
            movedInnerCard.setStackId(newStackId);
            movedInnerCard.setStatusEnum(DBStatus.LOCAL_MOVED);
            changedCards.add(movedCard.getCard());
            for (FullCard fullCard : cardsOfNewStack) {
                // skip unchanged cards
                if (fullCard.getCard().getOrder() < newOrder) {
                    continue;
                }
                changedCards.add(fullCard.getCard());
            }
        }
        reorderAscending(movedInnerCard, changedCards, startingAtOrder);
    }

    private void reorderAscending(Card movedCard, List<Card> cardsToReorganize, int startingAtOrder) {
        Date now = new Date();
        for (Card card : cardsToReorganize) {
            card.setOrder(startingAtOrder);
            if (card.getStatus() == DBStatus.UP_TO_DATE.getId()) {
                card.setStatusEnum(DBStatus.LOCAL_EDITED_SILENT);
                card.setLastModifiedLocal(now);
            }
            startingAtOrder++;
        }
        //update the moved one first, because otherwise a bunch of livedata is fired, leading the card to dispose and reappear
        cardsToReorganize.remove(movedCard);
        dataBaseAdapter.updateCard(movedCard, false);
        for (Card card : cardsToReorganize) {
            dataBaseAdapter.updateCard(card, false);
        }
    }

    /**
     * FIXME clean up on error
     * When uploading the exact same attachment 2 times to the same card, the server starts burning and gets mad and returns status 500
     * The problem is, that the attachment is still in our local database and everytime one tries to sync, the log is spammed with 500 errors
     * Also this leads to the attachment being present in the card forever with a DBStatus.LOCAL_EDITED
     */
    public WrappedLiveData<Attachment> addAttachmentToCard(long accountId, long localCardId, @NonNull String mimeType, @NonNull File file) {
        WrappedLiveData<Attachment> liveData = new WrappedLiveData<>();
        doAsync(() -> {
            Attachment attachment = populateAttachmentEntityForFile(new Attachment(), localCardId, mimeType, file);
            Date now = new Date();
            attachment.setLastModifiedLocal(now);
            attachment.setCreatedAt(now);
            FullCard card = dataBaseAdapter.getFullCardByLocalIdDirectly(accountId, localCardId);
            Stack stack = dataBaseAdapter.getStackByLocalIdDirectly(card.getCard().getStackId());
            Board board = dataBaseAdapter.getBoardByLocalIdDirectly(stack.getBoardId());
            Account account = dataBaseAdapter.getAccountByIdDirectly(card.getAccountId());
            new DataPropagationHelper(serverAdapter, dataBaseAdapter).createEntity(
                    new AttachmentDataProvider(null, board, stack, card, Collections.singletonList(attachment)),
                    attachment, getCallbackToLiveDataConverter(account, liveData)
            );
        });
        return liveData;
    }

    public WrappedLiveData<Attachment> updateAttachmentForCard(long accountId, Attachment existing, @NonNull String mimeType, @NonNull File file) {
        WrappedLiveData<Attachment> liveData = new WrappedLiveData<>();
        doAsync(() -> {
            Attachment attachment = populateAttachmentEntityForFile(existing, existing.getCardId(), mimeType, file);
            attachment.setLastModifiedLocal(new Date());
            if (serverAdapter.hasInternetConnection()) {
                FullCard card = dataBaseAdapter.getFullCardByLocalIdDirectly(accountId, existing.getCardId());
                Stack stack = dataBaseAdapter.getStackByLocalIdDirectly(card.getCard().getStackId());
                Board board = dataBaseAdapter.getBoardByLocalIdDirectly(stack.getBoardId());
                Account account = dataBaseAdapter.getAccountByIdDirectly(card.getAccountId());
                new DataPropagationHelper(serverAdapter, dataBaseAdapter)
                        .updateEntity(new AttachmentDataProvider(null, board, stack, card, Collections.singletonList(attachment)), attachment, new IResponseCallback<Attachment>(account) {
                            @Override
                            public void onResponse(Attachment response) {
                                liveData.postValue(response);
                            }

                            @Override
                            public void onError(Throwable throwable) {
                                liveData.postError(throwable);
                            }
                        });
            }
        });
        return liveData;
    }

    private Attachment populateAttachmentEntityForFile(Attachment target, long localCardId, @NonNull String mimeType, @NonNull File file) {
        Attachment attachment = target;
        attachment.setCardId(localCardId);
        attachment.setMimetype(mimeType);
        attachment.setData(file.getName());
        attachment.setFilename(file.getName());
        attachment.setBasename(file.getName());
        attachment.setLocalPath(file.getAbsolutePath());
        attachment.setFilesize(file.length());
        return attachment;
    }

    public WrappedLiveData<Void> deleteAttachmentOfCard(long accountId, long localCardId, long localAttachmentId) {
        WrappedLiveData<Void> liveData = new WrappedLiveData<>();
        doAsync(() -> {
            if (serverAdapter.hasInternetConnection()) {
                FullCard card = dataBaseAdapter.getFullCardByLocalIdDirectly(accountId, localCardId);
                Stack stack = dataBaseAdapter.getStackByLocalIdDirectly(card.getCard().getStackId());
                Board board = dataBaseAdapter.getBoardByLocalIdDirectly(stack.getBoardId());
                Attachment attachment = dataBaseAdapter.getAttachmentByLocalIdDirectly(accountId, localAttachmentId);
                Account account = dataBaseAdapter.getAccountByIdDirectly(card.getAccountId());

                new DataPropagationHelper(serverAdapter, dataBaseAdapter)
                        .deleteEntity(new AttachmentDataProvider(null, board, stack, card, Collections.singletonList(attachment)), attachment, getCallbackToLiveDataConverter(account, liveData));
            }
        });
        return liveData;
    }


    private static class BooleanResultHolder {
        public boolean result = true;
    }
}
