package it.niedermann.nextcloud.deck.persistence.sync;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;

import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.core.util.Pair;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.nextcloud.android.sso.exceptions.NextcloudHttpRequestFailedException;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
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
import it.niedermann.nextcloud.deck.model.full.FullSingleCardWidgetModel;
import it.niedermann.nextcloud.deck.model.full.FullStack;
import it.niedermann.nextcloud.deck.model.internal.FilterInformation;
import it.niedermann.nextcloud.deck.model.ocs.Capabilities;
import it.niedermann.nextcloud.deck.model.ocs.comment.DeckComment;
import it.niedermann.nextcloud.deck.model.ocs.comment.OcsComment;
import it.niedermann.nextcloud.deck.model.ocs.comment.full.FullDeckComment;
import it.niedermann.nextcloud.deck.model.ocs.user.OcsUser;
import it.niedermann.nextcloud.deck.model.ocs.user.OcsUserList;
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
import it.niedermann.nextcloud.deck.persistence.sync.helpers.providers.partial.BoardWitAclDownSyncDataProvider;
import it.niedermann.nextcloud.deck.util.DateUtil;

import static java.net.HttpURLConnection.HTTP_UNAVAILABLE;

@SuppressWarnings("WeakerAccess")
public class SyncManager {

    @NonNull
    private Context appContext;
    @NonNull
    private DataBaseAdapter dataBaseAdapter;
    @NonNull
    private ServerAdapter serverAdapter;

    @AnyThread
    public SyncManager(@NonNull Context context) {
        this(context, null);
    }

    @AnyThread
    public SyncManager(@NonNull Context context, @Nullable String ssoAccountName) {
        appContext = context.getApplicationContext();
        LastSyncUtil.init(appContext);
        this.dataBaseAdapter = new DataBaseAdapter(appContext);
        this.serverAdapter = new ServerAdapter(appContext, ssoAccountName);
    }

    @AnyThread
    private void doAsync(@NonNull Runnable r) {
        new Thread(r).start();
    }

    @AnyThread
    public MutableLiveData<FullCard> synchronizeCardByRemoteId(long cardRemoteId, @NonNull Account account) {
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
    @AnyThread
    public LiveData<Long> getLocalBoardIdByCardRemoteIdAndAccount(long cardRemoteId, @NonNull Account account) {
        return dataBaseAdapter.getLocalBoardIdByCardRemoteIdAndAccountId(cardRemoteId, account.getId());
    }

    @AnyThread
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

    @AnyThread
    public void synchronize(@NonNull IResponseCallback<Boolean> responseCallback) {
        if(responseCallback.getAccount() == null) {
            throw new IllegalArgumentException(Account.class.getSimpleName() + " object in given " + IResponseCallback.class.getSimpleName() + " must not be null.");
        }
        if(responseCallback.getAccount().getId() == null) {
            throw new IllegalArgumentException(Account.class.getSimpleName() + " object in given " + IResponseCallback.class.getSimpleName() + " must contain a valid id, but given id was null.");
        }
        doAsync(() -> refreshCapabilities(new IResponseCallback<Capabilities>(responseCallback.getAccount()) {
            @Override
            public void onResponse(Capabilities response) {
                if (!response.isMaintenanceEnabled()) {
                    if (response.getDeckVersion().isSupported(appContext)) {
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
                    } else {
                        responseCallback.onResponse(false);
                        DeckLog.warn("No sync. Server version not supported: " + response.getDeckVersion().getOriginalVersion());
                    }
                } else {
                    responseCallback.onResponse(false);
                    DeckLog.warn("No sync. Status maintenance mode: " + response.isMaintenanceEnabled());
                }
            }
        }));
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

    @AnyThread
    public LiveData<Boolean> hasAccounts() {
        return dataBaseAdapter.hasAccounts();
    }

    @AnyThread
    public WrappedLiveData<Account> createAccount(@NonNull Account accout) {
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

    @AnyThread
    public LiveData<Account> readAccount(long id) {
        return dataBaseAdapter.readAccount(id);
    }

    @AnyThread
    public LiveData<Account> readAccount(@Nullable String name) {
        return dataBaseAdapter.readAccount(name);
    }

    @AnyThread
    public LiveData<List<Account>> readAccounts() {
        return dataBaseAdapter.readAccounts();
    }

    /**
     * <p>
     * Since the return value is a {@link LiveData}, it should immediately return the available values from the database
     * and then perform a synchronization (not full but only for the needed data) to update the return value.
     * <p>
     * See https://github.com/stefan-niedermann/nextcloud-deck/issues/498#issuecomment-631615680
     *
     * @param host e. g. "example.com:4711"
     * @return a {@link List<Account>} of {@link Account}s which are
     * - located at the given {@param host}
     * - and have the permission to read the board with the given {@param boardRemoteId} (aka the {@link Board} is shared with this {@link User}).
     */
    @AnyThread
    public LiveData<List<Account>> readAccountsForHostWithReadAccessToBoard(String host, long boardRemoteId) {
        MediatorLiveData<List<Account>> liveData = new MediatorLiveData<>();
        liveData.addSource(dataBaseAdapter.readAccountsForHostWithReadAccessToBoard(host, boardRemoteId), accounts -> {
            liveData.postValue(accounts);
            doAsync(() -> {
                for (Account account : accounts) {
                    new SyncHelper(serverAdapter, dataBaseAdapter, null)
                            .setResponseCallback(new IResponseCallback<Boolean>(account) {
                                @Override
                                public void onResponse(Boolean response) {
                                    liveData.postValue(dataBaseAdapter.readAccountsForHostWithReadAccessToBoardDirectly(host, boardRemoteId));
                                }
                            }).doSyncFor(new BoardWitAclDownSyncDataProvider());
                }
            });
        });

        return liveData;
    }

    @AnyThread
    public void refreshCapabilities(@NonNull IResponseCallback<Capabilities> callback) {
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

            try {
                serverAdapter.getAllOcsUsers(new IResponseCallback<OcsUserList>(callback.getAccount()) {
                    @Override
                    public void onResponse(OcsUserList response) {
                        Long accountId = callback.getAccount().getId();
                        for (String ocsUserName : response) {
                            User existingUser = dataBaseAdapter.getUserByUidDirectly(accountId, ocsUserName);
                            if (existingUser == null) {
                                // we don't know this user, lets get some details...
                                serverAdapter.getOcsUserDetails(ocsUserName, new IResponseCallback<OcsUser>(callback.getAccount()) {
                                    @Override
                                    public void onResponse(OcsUser response) {
                                        User newUser = new User();
                                        newUser.setStatus(DBStatus.UP_TO_DATE.getId());
                                        newUser.setPrimaryKey(ocsUserName);
                                        newUser.setUid(ocsUserName);
                                        newUser.setDisplayname(response.getDisplayName());
                                        dataBaseAdapter.createUser(accountId, newUser);
                                    }
                                });
                            }
                        }
                    }
                });
            } catch (OfflineException ignored) {
                // Nothing to do here...
            }
        });
    }

    /**
     * @param accountId ID of the account
     * @param archived  Decides whether only archived or not-archived boards for the specified account will be returned
     * @return all archived or non-archived <code>Board</code>s depending on <code>archived</code> parameter
     */
    @AnyThread
    public LiveData<List<Board>> getBoards(long accountId, boolean archived) {
        return dataBaseAdapter.getBoards(accountId, archived);
    }

    /**
     * @param accountId ID of the account
     * @param archived  Decides whether only archived or not-archived boards for the specified account will be returned
     * @return all archived or non-archived <code>FullBoard</code>s depending on <code>archived</code> parameter
     */
    @AnyThread
    public LiveData<List<FullBoard>> getFullBoards(long accountId, boolean archived) {
        return dataBaseAdapter.getFullBoards(accountId, archived);
    }

    /**
     * Get all non-archived  <code>FullBoard</code>s with edit permissions for the specified account.
     *
     * @param accountId ID of the account
     * @return all non-archived <code>Board</code>s with edit permission
     */
    @AnyThread
    public LiveData<List<Board>> getBoardsWithEditPermission(long accountId) {
        return dataBaseAdapter.getBoardsWithEditPermission(accountId);
    }

    @AnyThread
    public LiveData<Boolean> hasArchivedBoards(long accountId) {
        return dataBaseAdapter.hasArchivedBoards(accountId);
    }

    @AnyThread
    public LiveData<FullBoard> createBoard(long accountId, @NonNull Board board) {
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

    /**
     * Creates a new {@link Board} and adds the same {@link Label} and {@link Stack} as in the origin {@link Board}.
     * Owner of the target {@link Board} will be the {@link User} with the {@link Account} of {@param targetAccountId}.
     * Does <strong>not</strong> clone any {@link Card} or {@link AccessControl} from the origin {@link Board}.
     */
    @AnyThread
    public WrappedLiveData<FullBoard> cloneBoard(long originAccountId, long originBoardLocalId, long targetAccountId, String targetBoardTitle, String targetBoardColor) {
        WrappedLiveData<FullBoard> liveData = new WrappedLiveData<>();

        doAsync(() -> {
            Account originAccount = dataBaseAdapter.getAccountByIdDirectly(originAccountId);
            User newOwner = dataBaseAdapter.getUserByUidDirectly(originAccountId, originAccount.getUserName());
            FullBoard originalBoard = dataBaseAdapter.getFullBoardByLocalIdDirectly(originAccountId, originBoardLocalId);
            originalBoard.setAccountId(targetAccountId);
            originalBoard.getBoard().setTitle(targetBoardTitle);
            originalBoard.getBoard().setColor(targetBoardColor);
            originalBoard.getBoard().setOwnerId(newOwner.getId());
            originalBoard.setStatusEnum(DBStatus.LOCAL_EDITED);
            originalBoard.setOwner(newOwner);
            originalBoard.setId(null);
            originalBoard.setLocalId(null);
            long newBoardId = dataBaseAdapter.createBoardDirectly(originAccountId, originalBoard.getBoard());
            originalBoard.setLocalId(newBoardId);

            for (Stack stack : originalBoard.getStacks()) {
                stack.setLocalId(null);
                stack.setId(null);
                stack.setStatusEnum(DBStatus.LOCAL_EDITED);
                stack.setAccountId(targetAccountId);
                stack.setBoardId(newBoardId);
                dataBaseAdapter.createStack(targetAccountId, stack);
            }
            for (Label label : originalBoard.getLabels()) {
                label.setLocalId(null);
                label.setId(null);
                label.setAccountId(targetAccountId);
                label.setStatusEnum(DBStatus.LOCAL_EDITED);
                label.setBoardId(newBoardId);
                dataBaseAdapter.createLabel(targetAccountId, label);
            }
            Account targetAccount = dataBaseAdapter.getAccountByIdDirectly(targetAccountId);
            new SyncHelper(serverAdapter, dataBaseAdapter, null)
                    .setResponseCallback(new IResponseCallback<Boolean>(targetAccount) {
                        @Override
                        public void onResponse(Boolean response) {
                            liveData.postValue(dataBaseAdapter.getFullBoardByLocalIdDirectly(targetAccountId, newBoardId));
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            super.onError(throwable);
                            liveData.postError(throwable);
                        }
                    }).doSyncFor(new BoardDataProvider());

        });
        return liveData;
    }

    @AnyThread
    public LiveData<List<it.niedermann.nextcloud.deck.model.ocs.Activity>> syncActivitiesForCard(@NonNull Card card) {
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

    @AnyThread
    public void addCommentToCard(long accountId, long cardId, @NonNull DeckComment comment) {
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

    @AnyThread
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

    @AnyThread
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

    public LiveData<List<FullDeckComment>> getFullCommentsForLocalCardId(long localCardId) {
        return dataBaseAdapter.getFullCommentsForLocalCardId(localCardId);
    }

    @AnyThread
    public WrappedLiveData<Void> deleteBoard(@NonNull Board board) {
        WrappedLiveData<Void> liveData = new WrappedLiveData<>();
        doAsync(() -> {
            long accountId = board.getAccountId();
            Account account = dataBaseAdapter.getAccountByIdDirectly(accountId);
            FullBoard fullBoard = dataBaseAdapter.getFullBoardByLocalIdDirectly(accountId, board.getLocalId());
            new DataPropagationHelper(serverAdapter, dataBaseAdapter).deleteEntity(new BoardDataProvider(), fullBoard, getCallbackToLiveDataConverter(account, liveData));
        });
        return liveData;
    }

    @AnyThread
    public WrappedLiveData<FullBoard> updateBoard(@NonNull FullBoard board) {
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

    @AnyThread
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

    @WorkerThread
    public AccessControl getAccessControlByRemoteIdDirectly(long accountId, Long id) {
        return dataBaseAdapter.getAccessControlByRemoteIdDirectly(accountId, id);
    }

    public LiveData<List<AccessControl>> getAccessControlByLocalBoardId(long accountId, Long id) {
        return dataBaseAdapter.getAccessControlByLocalBoardId(accountId, id);
    }

    @AnyThread
    public WrappedLiveData<AccessControl> updateAccessControl(@NonNull AccessControl entity) {
        WrappedLiveData<AccessControl> liveData = new WrappedLiveData<>();
        doAsync(() -> {
            Account account = dataBaseAdapter.getAccountByIdDirectly(entity.getAccountId());
            FullBoard board = dataBaseAdapter.getFullBoardByLocalIdDirectly(entity.getAccountId(), entity.getBoardId());
            new DataPropagationHelper(serverAdapter, dataBaseAdapter).updateEntity(
                    new AccessControlDataProvider(null, board, Collections.singletonList(entity)), entity, getCallbackToLiveDataConverter(account, liveData));
        });
        return liveData;
    }

    @AnyThread
    private <T> IResponseCallback<T> getCallbackToLiveDataConverter(Account account, @NonNull WrappedLiveData<T> liveData) {
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

    @AnyThread
    public WrappedLiveData<Void> deleteAccessControl(@NonNull AccessControl entity) {
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

    @AnyThread
    public WrappedLiveData<FullStack> createStack(long accountId, @NonNull Stack stack) {
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

    @AnyThread
    public WrappedLiveData<Void> deleteStack(long accountId, long stackLocalId, long boardLocalId) {
        WrappedLiveData<Void> liveData = new WrappedLiveData<>();
        doAsync(() -> {
            Account account = dataBaseAdapter.getAccountByIdDirectly(accountId);
            FullStack fullStack = dataBaseAdapter.getFullStackByLocalIdDirectly(stackLocalId);
            FullBoard board = dataBaseAdapter.getFullBoardByLocalIdDirectly(accountId, boardLocalId);
            new DataPropagationHelper(serverAdapter, dataBaseAdapter).deleteEntity(new StackDataProvider(null, board), fullStack, getCallbackToLiveDataConverter(account, liveData));
        });
        return liveData;
    }

    @AnyThread
    public WrappedLiveData<FullStack> updateStack(@NonNull FullStack stack) {
        WrappedLiveData<FullStack> liveData = new WrappedLiveData<>();
        doAsync(() -> {
            Account account = dataBaseAdapter.getAccountByIdDirectly(stack.getAccountId());
            FullBoard board = dataBaseAdapter.getFullBoardByLocalIdDirectly(stack.getAccountId(), stack.getStack().getBoardId());
            updateStack(account, board, stack, liveData);
        });
        return liveData;

    }

    @AnyThread
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
    @AnyThread
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

    @AnyThread
    public LiveData<FullCard> createFullCard(long accountId, long localBoardId, long localStackId, @NonNull FullCard card) {
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
            card.getCard().setOrder(dataBaseAdapter.getHighestCardOrderInStack(localStackId) + 1);
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

    @AnyThread
    public WrappedLiveData<Void> deleteCard(@NonNull Card card) {
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

    @AnyThread
    public WrappedLiveData<FullCard> archiveCard(@NonNull FullCard card) {
        WrappedLiveData<FullCard> liveData = new WrappedLiveData<>();
        doAsync(() -> {
            Account account = dataBaseAdapter.getAccountByIdDirectly(card.getAccountId());
            FullStack stack = dataBaseAdapter.getFullStackByLocalIdDirectly(card.getCard().getStackId());
            Board board = dataBaseAdapter.getBoardByLocalIdDirectly(stack.getStack().getBoardId());
            card.getCard().setArchived(true);
            updateCardForArchive(account, stack, board, card, getCallbackToLiveDataConverter(account, liveData));
        });
        return liveData;
    }

    private void updateCardForArchive(Account account, FullStack stack, Board board, FullCard card, @NonNull IResponseCallback<FullCard> callback) {
        new DataPropagationHelper(serverAdapter, dataBaseAdapter).updateEntity(new CardDataProvider(null, board, stack), card, callback);
    }

    @AnyThread
    public WrappedLiveData<FullCard> dearchiveCard(@NonNull FullCard card) {
        WrappedLiveData<FullCard> liveData = new WrappedLiveData<>();
        doAsync(() -> {
            Account account = dataBaseAdapter.getAccountByIdDirectly(card.getAccountId());
            FullStack stack = dataBaseAdapter.getFullStackByLocalIdDirectly(card.getCard().getStackId());
            Board board = dataBaseAdapter.getBoardByLocalIdDirectly(stack.getStack().getBoardId());
            card.getCard().setArchived(false);
            updateCardForArchive(account, stack, board, card, getCallbackToLiveDataConverter(account, liveData));
        });
        return liveData;
    }

    @AnyThread
    public WrappedLiveData<Void> archiveCardsInStack(long accountId, long stackLocalId) {
        WrappedLiveData<Void> liveData = new WrappedLiveData<>();
        doAsync(() -> {
            Account account = dataBaseAdapter.getAccountByIdDirectly(accountId);
            FullStack stack = dataBaseAdapter.getFullStackByLocalIdDirectly(stackLocalId);
            Board board = dataBaseAdapter.getBoardByLocalIdDirectly(stack.getStack().getBoardId());
            List<FullCard> cards = dataBaseAdapter.getFullCardsForStackDirectly(accountId, stackLocalId);
            if (cards.size() > 0) {
                CountDownLatch latch = new CountDownLatch(cards.size());
                for (FullCard card : cards) {
                    card.getCard().setArchived(true);
                    updateCardForArchive(account, stack, board, card, new IResponseCallback<FullCard>(account) {
                        @Override
                        public void onResponse(FullCard response) {
                            latch.countDown();
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            latch.countDown();
                            liveData.postError(throwable);
                        }
                    });
                }
                try {
                    latch.await();
                    liveData.postValue(null);
                } catch (InterruptedException e) {
                    liveData.postError(e);
                }
            } else {
                liveData.postValue(null);
            }
        });
        return liveData;
    }

    @AnyThread
    public void archiveBoard(@NonNull Board board) {
        doAsync(() -> {
            FullBoard b = dataBaseAdapter.getFullBoardByLocalIdDirectly(board.getAccountId(), board.getLocalId());
            b.getBoard().setArchived(true);
            updateBoard(b);
        });
    }

    @AnyThread
    public void dearchiveBoard(@NonNull Board board) {
        doAsync(() -> {
            FullBoard b = dataBaseAdapter.getFullBoardByLocalIdDirectly(board.getAccountId(), board.getLocalId());
            b.getBoard().setArchived(false);
            updateBoard(b);
        });
    }

    @AnyThread
    public WrappedLiveData<FullCard> updateCard(@NonNull FullCard card) {
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

    /**
     * Moves the given {@param originCardLocalId} to the new target coordinates specified by {@param targetAccountId}, {@param targetBoardLocalId} and {@param targetStackLocalId}.
     * If the {@param targetBoardLocalId} changes, this will apply some logic to make sure that we migrate as much data as possible without the risk of getting an illegal state.
     * Attachments are not copied or anything.
     * <p>
     * 1) {@link FullCard#labels}
     * <p>
     * a) If a {@link Label} with the same {@link Label#title} exists (case insensitive) in the target, assign this {@link Label} instead of the origin
     * b) If no similar {@link Label} exists:
     * i) If user has {@link AccessControl#permissionManage}, create a new {@link Label} with this {@link Label#color} and {@link Label#title} and assign it
     * ii) Else remove this {@link Label} from the {@link Card}
     * <p>
     * 2) {@link FullCard#assignedUsers}
     * <p>
     * a) If the {@link User} has at least view permission at the target {@link Board}, keep it (<strong>can</strong> be the case if the target {@link Account} is the same as the origin {@link Account} <strong>or</strong> the target {@link Account} is on the same Nextcloud instance as the origin {@link Account}
     * b) Else {@link #unassignUserFromCard(User, Card)} (will always be the case if the target {@link Account} is on another Nextcloud isntance as the origin {@link Account})
     * <p>
     * <p>
     * https://github.com/stefan-niedermann/nextcloud-deck/issues/453
     */
    @SuppressWarnings("JavadocReference")
    @AnyThread
    public WrappedLiveData<Void> moveCard(long originAccountId, long originCardLocalId, long targetAccountId, long targetBoardLocalId, long targetStackLocalId) {
        return LiveDataHelper.wrapInLiveData(() -> {

            FullCard originalCard = dataBaseAdapter.getFullCardByLocalIdDirectly(originAccountId, originCardLocalId);
            int newIndex = dataBaseAdapter.getHighestCardOrderInStack(targetStackLocalId) + 1;
            FullBoard originalBoard = dataBaseAdapter.getFullBoardByLocalCardIdDirectly(originCardLocalId);
            // ### maybe shortcut possible? (just moved to another stack)
            if (targetBoardLocalId == originalBoard.getLocalId()) {
                reorder(originAccountId, originalCard, targetStackLocalId, newIndex);
                return null;
            }
            // ### get rid of original card where it is now.
            Card originalInnerCard = originalCard.getCard();
            deleteCard(originalInnerCard);
            // ### clone card itself
            Card targetCard = originalInnerCard;
            targetCard.setAccountId(targetAccountId);
            targetCard.setId(null);
            targetCard.setLocalId(null);
            targetCard.setStatusEnum(DBStatus.LOCAL_EDITED);
            targetCard.setStackId(targetStackLocalId);
            targetCard.setOrder(newIndex);
            //TODO: this needs to propagate to server as well, since anything else propagates as well (otherwise card isn't known on server)
            FullCard fullCardForServerPropagation = new FullCard();
            fullCardForServerPropagation.setCard(targetCard);

            Account targetAccount = dataBaseAdapter.getAccountByIdDirectly(targetAccountId);
            FullBoard targetBoard = dataBaseAdapter.getFullBoardByLocalIdDirectly(targetAccountId, targetBoardLocalId);
            FullStack targetFullStack = dataBaseAdapter.getFullStackByLocalIdDirectly(targetStackLocalId);
            User userOfTargetAccount = dataBaseAdapter.getUserByUidDirectly(targetAccountId, targetAccount.getUserName());
            CountDownLatch latch = new CountDownLatch(1);
            new DataPropagationHelper(serverAdapter, dataBaseAdapter).createEntity(new CardPropagationDataProvider(null, targetBoard.getBoard(), targetFullStack), fullCardForServerPropagation, new IResponseCallback<FullCard>(targetAccount) {
                @Override
                public void onResponse(FullCard response) {
                    targetCard.setId(response.getId());
                    targetCard.setLocalId(response.getLocalId());
                    latch.countDown();
                }

                @Override
                public void onError(Throwable throwable) {
                    super.onError(throwable);
                    throw new RuntimeException("unable to create card in moveCard target", throwable);
                }
            }, (FullCard entity, FullCard response) -> {
                response.getCard().setUserId(entity.getCard().getUserId());
                response.getCard().setStackId(targetFullStack.getLocalId());
            });

            try {
                latch.await();
            } catch (InterruptedException e) {
                DeckLog.logError(e);
                throw new RuntimeException("error fulfilling countDownLatch", e);
            }

            long newCardId = targetCard.getLocalId();

            // ### clone labels, assign them
            // prepare
            // has user of targetaccount manage permissions?
            boolean hasManagePermission = targetBoard.getBoard().getOwnerId() == userOfTargetAccount.getLocalId();
            List<AccessControl> aclOfTargetBoard = dataBaseAdapter.getAccessControlByLocalBoardIdDirectly(targetAccountId, targetBoard.getLocalId());
            if (!hasManagePermission) {
                for (AccessControl accessControl : aclOfTargetBoard) {
                    if (accessControl.getUserId() == userOfTargetAccount.getLocalId() && accessControl.isPermissionManage()) {
                        hasManagePermission = true;
                        break;
                    }
                }
            }

            // actual doing
            for (Label originalLabel : originalCard.getLabels()) {
                // already exists?
                Label existingMatch = null;
                for (Label targetBoardLabel : targetBoard.getLabels()) {
                    if (originalLabel.getTitle().trim().equalsIgnoreCase(targetBoardLabel.getTitle().trim())) {
                        existingMatch = targetBoardLabel;
                        break;
                    }
                }
                if (existingMatch == null) {
                    if (hasManagePermission) {
                        originalLabel.setBoardId(targetBoardLocalId);
                        originalLabel.setId(null);
                        originalLabel.setLocalId(null);
                        originalLabel.setStatusEnum(DBStatus.LOCAL_EDITED);
                        originalLabel.setAccountId(targetBoard.getAccountId());
                        createAndAssignLabelToCard(originalBoard.getAccountId(), originalLabel, newCardId);
                    }
                } else {
                    assignLabelToCard(existingMatch, targetCard);
                }
            }

            // ### Clone assigned users
            Account originalAccount = dataBaseAdapter.getAccountByIdDirectly(originAccountId);
            // same instance? otherwise doesn't make sense
            if (originalAccount.getUrl().equalsIgnoreCase(targetAccount.getUrl())) {
                for (User assignedUser : originalCard.getAssignedUsers()) {
                    // has assignedUser at least view permissions?
                    boolean hasViewPermission = targetBoard.getBoard().getOwnerId() == assignedUser.getLocalId();
                    if (!hasViewPermission) {
                        for (AccessControl accessControl : aclOfTargetBoard) {
                            if (accessControl.getUserId() == userOfTargetAccount.getLocalId()) {
                                // ACL exists, so viewing is granted
                                hasViewPermission = true;
                                break;
                            }
                        }
                    }
                    if (hasViewPermission) {
                        assignUserToCard(assignedUser, targetCard);
                    }
                }
            }
            // since this is LiveData<Void>
            return null;
        });
    }

    @AnyThread
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

    @AnyThread
    public MutableLiveData<Label> createAndAssignLabelToCard(long accountId, @NonNull Label label, long localCardId) {
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

    @AnyThread
    public WrappedLiveData<Void> deleteLabel(@NonNull Label label) {
        WrappedLiveData<Void> liveData = new WrappedLiveData<>();
        doAsync(() -> {
            Account account = dataBaseAdapter.getAccountByIdDirectly(label.getAccountId());
            Board board = dataBaseAdapter.getBoardByLocalIdDirectly(label.getBoardId());
            new DataPropagationHelper(serverAdapter, dataBaseAdapter)
                    .deleteEntity(new LabelDataProvider(null, board, Collections.emptyList()), label, getCallbackToLiveDataConverter(account, liveData));
        });
        return liveData;
    }

    @AnyThread
    public WrappedLiveData<Label> updateLabel(@NonNull Label label) {
        WrappedLiveData<Label> liveData = new WrappedLiveData<>();
        doAsync(() -> {
            Account account = dataBaseAdapter.getAccountByIdDirectly(label.getAccountId());
            Board board = dataBaseAdapter.getBoardByLocalIdDirectly(label.getBoardId());
            new DataPropagationHelper(serverAdapter, dataBaseAdapter)
                    .updateEntity(new LabelDataProvider(null, board, Collections.emptyList()), label, getCallbackToLiveDataConverter(account, liveData));
        });
        return liveData;
    }

    @AnyThread
    public void assignUserToCard(@NonNull User user, @NonNull Card card) {
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

    @AnyThread
    public void assignLabelToCard(@NonNull Label label, @NonNull Card card) {
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

    @AnyThread
    public void unassignLabelFromCard(@NonNull Label label, @NonNull Card card) {
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

    @AnyThread
    public void unassignUserFromCard(@NonNull User user, @NonNull Card card) {
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

    @WorkerThread
    public User getUserByUidDirectly(long accountId, String uid) {
        return dataBaseAdapter.getUserByUidDirectly(accountId, uid);
    }

    public LiveData<List<User>> searchUserByUidOrDisplayName(final long accountId, final long boardId, final long notYetAssignedToLocalCardId, final String searchTerm) {
        return dataBaseAdapter.searchUserByUidOrDisplayName(accountId, boardId, notYetAssignedToLocalCardId, searchTerm);
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

    public void updateUser(long accountId, @NonNull User user) {
        dataBaseAdapter.updateUser(accountId, user, true);
    }

    public LiveData<List<Label>> searchNotYetAssignedLabelsByTitle(final long accountId, final long boardId, final long notYetAssignedToLocalCardId, @NonNull String searchTerm) {
        return dataBaseAdapter.searchNotYetAssignedLabelsByTitle(accountId, boardId, notYetAssignedToLocalCardId, searchTerm);
    }

    public String getServerUrl() {
        return serverAdapter.getServerUrl();
    }

    /**
     * @see <a href="https://github.com/stefan-niedermann/nextcloud-deck/issues/360">reenable reorder</a>
     */
    @AnyThread
    public void reorder(long accountId, @NonNull FullCard movedCard, long newStackId, int newIndex) {
        doAsync(() -> {
            // read cards of new stack
            List<FullCard> cardsOfNewStack = dataBaseAdapter.getFullCardsForStackDirectly(accountId, newStackId);
            int newOrder = newIndex;
            if (cardsOfNewStack.size() > newIndex) {
                newOrder = cardsOfNewStack.get(newIndex).getCard().getOrder();
            }

            boolean orderIsCorrect = true;
            if (newOrder == movedCard.getCard().getOrder() && newStackId == movedCard.getCard().getStackId()) {
                int lastOrder = Integer.MIN_VALUE;
                for (FullCard fullCard : cardsOfNewStack) {
                    int currentOrder = fullCard.getCard().getOrder();
                    if (currentOrder > lastOrder) {
                        lastOrder = currentOrder;
                    } else {
                        // the order is messed up. this could happen for a while,
                        // because the new cards by the app had all the same order: 0
                        orderIsCorrect = false;
                        break;
                    }
                }
                if (orderIsCorrect) {
                    return;
                } else {
                    // we need to fix the order.
                    cardsOfNewStack.remove(movedCard);
                    cardsOfNewStack.add(newIndex, movedCard);
                    for (int i = 0; i < cardsOfNewStack.size(); i++) {
                        Card card = cardsOfNewStack.get(i).getCard();
                        card.setOrder(i);
                        dataBaseAdapter.updateCard(card, true);
                    }

                }
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
            if (orderIsCorrect) {
                reorderLocally(cardsOfNewStack, movedCard, newStackId, newOrder);
            }
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


    private void reorderLocally(List<FullCard> cardsOfNewStack, @NonNull FullCard movedCard, long newStackId, int newOrder) {
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

    private void reorderAscending(@NonNull Card movedCard, @NonNull List<Card> cardsToReorganize, int startingAtOrder) {
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
    @AnyThread
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

    @AnyThread
    public WrappedLiveData<Attachment> updateAttachmentForCard(long accountId, @NonNull Attachment existing, @NonNull String mimeType, @NonNull File file) {
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

    @AnyThread
    private static Attachment populateAttachmentEntityForFile(@NonNull Attachment target, long localCardId, @NonNull String mimeType, @NonNull File file) {
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

    @AnyThread
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

    // -------------------
    // Widgets
    // -------------------

    /**
     * Can be called from a configuration screen or a picker.
     * Creates a new entry in the database, if row with given widgetId does not yet exist.
     */
    @AnyThread
    public void addOrUpdateSingleCardWidget(int widgetId, long accountId, long boardId, long localCardId) {
        doAsync(() -> dataBaseAdapter.createSingleCardWidget(widgetId, accountId, boardId, localCardId));
    }

    @WorkerThread
    public FullSingleCardWidgetModel getSingleCardWidgetModelDirectly(int widgetId) throws NoSuchElementException {
        final FullSingleCardWidgetModel model = dataBaseAdapter.getFullSingleCardWidgetModel(widgetId);
        if (model == null) {
            throw new NoSuchElementException();
        }
        return model;
    }

    @AnyThread
    public void deleteSingleCardWidgetModel(int widgetId) {
        doAsync(() -> dataBaseAdapter.deleteSingleCardWidget(widgetId));
    }

    private static class BooleanResultHolder {
        public boolean result = true;
    }
}
