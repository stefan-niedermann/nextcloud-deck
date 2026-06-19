package it.niedermann.nextcloud.deck.repository;

import static java.net.HttpURLConnection.HTTP_NOT_MODIFIED;
import static java.net.HttpURLConnection.HTTP_UNAVAILABLE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.util.Pair;

import androidx.annotation.AnyThread;
import androidx.annotation.ColorInt;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.nextcloud.android.sso.AccountImporter;
import com.nextcloud.android.sso.api.EmptyResponse;
import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;
import com.nextcloud.android.sso.exceptions.NextcloudHttpRequestFailedException;
import com.nextcloud.android.sso.model.SingleSignOnAccount;

import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.database.DataBaseAdapter;
import it.niedermann.nextcloud.deck.exceptions.DeckException;
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
import it.niedermann.nextcloud.deck.model.ocs.user.OcsUserList;
import it.niedermann.nextcloud.deck.model.ocs.user.UserForAssignment;
import it.niedermann.nextcloud.deck.remote.adapters.ServerAdapter;
import it.niedermann.nextcloud.deck.remote.api.GsonConfig;
import it.niedermann.nextcloud.deck.remote.api.IResponseCallback;
import it.niedermann.nextcloud.deck.remote.api.LastSyncUtil;
import it.niedermann.nextcloud.deck.remote.api.ResponseCallback;
import it.niedermann.nextcloud.deck.remote.helpers.DataPropagationHelper;
import it.niedermann.nextcloud.deck.remote.helpers.SyncHelper;
import it.niedermann.nextcloud.deck.remote.helpers.providers.AbstractSyncDataProvider;
import it.niedermann.nextcloud.deck.remote.helpers.providers.AccessControlDataProvider;
import it.niedermann.nextcloud.deck.remote.helpers.providers.ActivityDataProvider;
import it.niedermann.nextcloud.deck.remote.helpers.providers.AttachmentDataProvider;
import it.niedermann.nextcloud.deck.remote.helpers.providers.BoardDataProvider;
import it.niedermann.nextcloud.deck.remote.helpers.providers.CardDataProvider;
import it.niedermann.nextcloud.deck.remote.helpers.providers.CardPropagationDataProvider;
import it.niedermann.nextcloud.deck.remote.helpers.providers.DeckCommentsDataProvider;
import it.niedermann.nextcloud.deck.remote.helpers.providers.LabelDataProvider;
import it.niedermann.nextcloud.deck.remote.helpers.providers.StackDataProvider;
import it.niedermann.nextcloud.deck.remote.helpers.providers.partial.BoardWithAclDownSyncDataProvider;
import it.niedermann.nextcloud.deck.remote.helpers.providers.partial.BoardWithStacksAndLabelsUpSyncDataProvider;
import it.niedermann.nextcloud.deck.remote.helpers.util.ConnectivityUtil;
import okhttp3.Headers;

/**
 * Extends {@link BaseRepository} by synchronization capabilities.
 * Therefore it always requires an {@link Account} to choose the correct {@link SingleSignOnAccount} for network operations.
 */
@SuppressWarnings("WeakerAccess")
public class SyncRepository extends BaseRepository {

    @NonNull
    private final ServerAdapter serverAdapter;
    @NonNull
    private final SyncHelper.Factory syncHelperFactory;

    @AnyThread
    public SyncRepository(@NonNull Context context, @NonNull Account account) throws NextcloudFilesAppAccountNotFoundException {
        this(context, AccountImporter.getSingleSignOnAccount(context, account.getName()), new ConnectivityUtil(context));
    }

    private SyncRepository(@NonNull Context context,
                           @NonNull SingleSignOnAccount ssoAccount,
                           @NonNull ConnectivityUtil connectivityUtil) {
        this(context, new ServerAdapter(context.getApplicationContext(), ssoAccount, connectivityUtil), connectivityUtil, SyncHelper::new);
    }

    protected SyncRepository(@NonNull Context context,
                             @NonNull ServerAdapter serverAdapter,
                             @NonNull ConnectivityUtil connectivityUtil,
                             @NonNull SyncHelper.Factory syncHelperFactory) {
        super(context, connectivityUtil);
        this.serverAdapter = serverAdapter;
        this.syncHelperFactory = syncHelperFactory;
        LastSyncUtil.init(context.getApplicationContext());
    }

    @VisibleForTesting
    protected SyncRepository(@NonNull Context context,
                             @NonNull ServerAdapter serverAdapter,
                             @NonNull ConnectivityUtil connectivityUtil,
                             @NonNull SyncHelper.Factory syncHelperFactory,
                             @NonNull DataBaseAdapter databaseAdapter,
                             @NonNull ExecutorService executor) {
        super(context, connectivityUtil, databaseAdapter, executor);
        this.serverAdapter = serverAdapter;
        this.syncHelperFactory = syncHelperFactory;
        LastSyncUtil.init(context.getApplicationContext());
    }

    @AnyThread
    public void fetchBoardsFromServer(@NonNull ResponseCallback<List<FullBoard>> callback) {
        executor.submit(() -> serverAdapter.getBoards(callback));
    }

    @AnyThread
    public LiveData<Pair<Integer, Integer>> synchronize(@NonNull ResponseCallback<Boolean> responseCallback) {
        final var progress$ = new MutableLiveData<Pair<Integer, Integer>>();
        final var callbackAccount = responseCallback.getAccount();
        final long callbackAccountId = callbackAccount.getId();

        executor.submit(() -> {
            refreshCapabilities(new ResponseCallback<>(responseCallback.getAccount()) {
                @Override
                public void onResponse(Capabilities response, Headers headers) {
                    if (response != null && !response.isMaintenanceEnabled()) {
                        if (response.getDeckVersion().isSupported()) {
                            final var lastSyncDate = LastSyncUtil.getLastSyncDate(callbackAccountId);
                            final var syncHelper = syncHelperFactory.create(serverAdapter, dataBaseAdapter, lastSyncDate);

                            final var callback = new ResponseCallback<Boolean>(callbackAccount) {
                                @Override
                                public void onResponse(Boolean response, Headers headers) {
                                    syncHelper.setResponseCallback(new ResponseCallback<>(account) {
                                        @Override
                                        public void onResponse(Boolean response, Headers headers) {
                                            LastSyncUtil.setLastSyncDate(callbackAccountId, Instant.now());
                                            responseCallback.onResponse(response, headers);
                                        }

                                        @Override
                                        public void onError(Throwable throwable) {
                                            super.onError(throwable);
                                            responseCallback.onResponse(response, headers);
                                        }
                                    });
                                    executor.submit(() -> {
                                        try {
                                            syncHelper.doUpSyncFor(new BoardDataProvider(progress$));
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
                                syncHelper.doSyncFor(new BoardDataProvider(progress$, false), false);
                            } catch (Throwable e) {
                                DeckLog.logError(e);
                                responseCallback.onError(e);
                            }
                        } else {
                            DeckLog.warn("No sync. Server version not supported:", response.getDeckVersion().getOriginalVersion());
                            responseCallback.onResponse(Boolean.FALSE, headers);
                        }
                    } else {
                        responseCallback.onResponse(Boolean.FALSE, headers);
                        if (response != null) {
                            DeckLog.warn("No sync. Status maintenance mode:", response.isMaintenanceEnabled());
                        }
                    }
                }

                @SuppressLint("MissingSuperCall")
                @Override
                public void onError(Throwable throwable) {
                    responseCallback.onError(throwable);
                }
            });
        });
        return progress$;
    }

    @AnyThread
    public void synchronizeBoard(long localBoardId, @NonNull ResponseCallback<Boolean> responseCallback) {
        executor.submit(() -> {
            FullBoard board = dataBaseAdapter.getFullBoardByLocalIdDirectly(responseCallback.getAccount().getId(), localBoardId);
            try {
                syncHelperFactory.create(serverAdapter, dataBaseAdapter, null)
                        .setResponseCallback(responseCallback)
                        .doSyncFor(new StackDataProvider(null, board));
            } catch (OfflineException e) {
                responseCallback.onError(e);
            }
        });
    }

    @AnyThread
    public void synchronizeCard(@NonNull ResponseCallback<Boolean> responseCallback, @NonNull Card card) {
        executor.submit(() -> {
            FullStack stack = dataBaseAdapter.getFullStackByLocalIdDirectly(card.getStackId());
            Board board = dataBaseAdapter.getBoardByLocalIdDirectly(stack.getStack().getBoardId());
            try {
                syncHelperFactory.create(serverAdapter, dataBaseAdapter, null)
                        .setResponseCallback(responseCallback)
                        .doSyncFor(new CardDataProvider(null, board, stack));
            } catch (OfflineException e) {
                responseCallback.onError(e);
            }
        });
    }

//    private <T extends AbstractRemoteEntity> T applyUpdatesFromRemote(T localEntity, T remoteEntity, Long accountId) {
//        if (!localEntity.getId().equals(remoteEntity.getId())
//                || !accountId.equals(localEntity.getAccountId())) {
//            throw new IllegalArgumentException("IDs of Account or Entity are not matching! WTF are you doin?!");
//        }
//        remoteEntity.setLastModifiedLocal(remoteEntity.getLastModified()); // not an error! local-modification = remote-mod
//        remoteEntity.setLocalId(localEntity.getLocalId());
//        return remoteEntity;
//    }

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
    @MainThread
    public LiveData<List<Account>> readAccountsForHostWithReadAccessToBoard(String host, long boardRemoteId) {
        MediatorLiveData<List<Account>> liveData = new MediatorLiveData<>();
        liveData.addSource(dataBaseAdapter.readAccountsForHostWithReadAccessToBoard(host, boardRemoteId), accounts -> {
            liveData.postValue(accounts);
            executor.submit(() -> {
                for (Account account : accounts) {
                    syncHelperFactory.create(serverAdapter, dataBaseAdapter, null)
                            .setResponseCallback(new ResponseCallback<>(account) {
                                @Override
                                public void onResponse(Boolean response, Headers headers) {
                                    liveData.postValue(dataBaseAdapter.readAccountsForHostWithReadAccessToBoardDirectly(host, boardRemoteId));
                                }
                            }).doSyncFor(new BoardWithAclDownSyncDataProvider());
                }
            });
        });

        return liveData;
    }

    @AnyThread
    public Future<?> refreshCapabilities(@NonNull ResponseCallback<Capabilities> callback) {
        return executor.submit(() -> {
            try {
                Account accountForEtag = dataBaseAdapter.getAccountByIdDirectly(callback.getAccount().getId());
                serverAdapter.getCapabilities(accountForEtag.getEtag(), new ResponseCallback<>(callback.getAccount()) {
                    @Override
                    public void onResponse(Capabilities response, Headers headers) {
                        Account acc = dataBaseAdapter.getAccountByIdDirectly(account.getId());
                        acc.applyCapabilities(response, headers.get("ETag"));
                        dataBaseAdapter.updateAccount(acc);
                        callback.getAccount().setServerDeckVersion(acc.getServerDeckVersion());
                        callback.onResponse(response, headers);
                    }

                    @SuppressLint("MissingSuperCall")
                    @Override
                    public void onError(Throwable throwable) {
                        if (throwable instanceof NextcloudHttpRequestFailedException requestFailedException) {
                            DeckLog.verbose("HTTP Status " + requestFailedException.getStatusCode());
                            if (requestFailedException.getStatusCode() == HTTP_UNAVAILABLE && requestFailedException.getCause() != null) {
                                final String errorString = requestFailedException.getCause().getMessage();
                                final Capabilities capabilities = GsonConfig.getGson().fromJson(errorString, Capabilities.class);
                                if (capabilities.isMaintenanceEnabled()) {
                                    DeckLog.verbose("Yes, it is in maintenance mode according to the capabilities");
                                    executor.submit(() -> onResponse(capabilities, IResponseCallback.EMPTY_HEADERS));
                                } else {
                                    DeckLog.error("No, it is not in maintenance mode according to the capabilities.");
                                    callback.onError(throwable);
                                }
                            } else if (requestFailedException.getStatusCode() == HTTP_NOT_MODIFIED) {
                                DeckLog.verbose("HTTP Status", HTTP_NOT_MODIFIED + ": There haven't been any changes on the server side for this request.");
                                // could be after maintenance. so we have to at least revert the maintenance flag
                                executor.submit(() -> {
                                    final Account acc = dataBaseAdapter.getAccountByIdDirectly(account.getId());
                                    if (acc.isMaintenanceEnabled()) {
                                        acc.setMaintenanceEnabled(false);
                                        dataBaseAdapter.updateAccount(acc);
                                    }
                                    final Capabilities capabilities = new Capabilities();
                                    capabilities.setMaintenanceEnabled(false);
                                    capabilities.setDeckVersion(acc.getServerDeckVersionAsObject());
                                    capabilities.setTextColor(acc.getTextColor());
                                    capabilities.setColor(acc.getColor());
                                    callback.onResponse(capabilities, IResponseCallback.EMPTY_HEADERS);
                                });
                            } else {
                                callback.onError(throwable);
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

    @AnyThread
    public void createBoard(@NonNull Account account, @NonNull Board board, @NonNull IResponseCallback<FullBoard> callback) {
        executor.submit(() -> {
            final User owner = dataBaseAdapter.getUserByUidDirectly(account.getId(), account.getUserName());
            if (owner == null) {
                StringBuilder sb = buildOwnerNullMessage(account);
                callback.onError(new IllegalStateException(sb.toString()));
            } else {
                final FullBoard fullBoard = new FullBoard();
                board.setOwnerId(owner.getLocalId());
                fullBoard.setOwner(owner);
                fullBoard.setBoard(board);
                board.setAccountId(account.getId());
                fullBoard.setAccountId(account.getId());
                new DataPropagationHelper(serverAdapter, dataBaseAdapter, executor).createEntity(new BoardDataProvider(), fullBoard, ResponseCallback.from(account, callback));
            }
        });
    }

    @NonNull
    private StringBuilder buildOwnerNullMessage(@NonNull Account account) {
        StringBuilder sb = new StringBuilder("Owner is null. This can be the case if the Deck app has never before been opened in the webinterface. More:");
        sb.append("\naccount_id:");
        sb.append(account.getId());
        sb.append("\nusername:");
        sb.append(account.getUserName());

        sb.append("\nList of available Users:");
        sb.append(account.getUserName());
        List<User> allUsers = dataBaseAdapter.getAllUsersDirectly();
        if (allUsers != null) {
            for (User u : allUsers) {
                sb.append("\nuid:");
                sb.append(u.getUid());
                sb.append(" | account_id:");
                sb.append(u.getAccountId());
            }
        } else {
            sb.append("[none]");
        }
        return sb;
    }

    /**
     * Creates a new {@link Board} and adds the same {@link Label} and {@link Stack} as in the origin {@link Board}.
     * Owner of the target {@link Board} will be the {@link User} with the {@link Account} of {@param targetAccountId}.
     *
     * @param cloneCards determines whether or not the cards in this {@link Board} shall be cloned or not
     *                   Does <strong>not</strong> clone any {@link Card} or {@link AccessControl} from the origin {@link Board}.
     */
    @AnyThread
    public void cloneBoard(long originAccountId, long originBoardLocalId, long targetAccountId, @ColorInt int targetBoardColor, boolean cloneCards, @NonNull IResponseCallback<FullBoard> callback) {
        executor.submit(() -> {
            Account originAccount = dataBaseAdapter.getAccountByIdDirectly(originAccountId);
            User newOwner = dataBaseAdapter.getUserByUidDirectly(originAccountId, originAccount.getUserName());
            if (newOwner == null) {
                callback.onError(new DeckException(DeckException.Hint.UNKNOWN_ACCOUNT_USER_ID, "User with Account-UID \"" + originAccount.getUserName() + "\" not found."));
                return;
            }
            FullBoard originalBoard = dataBaseAdapter.getFullBoardByLocalIdDirectly(originAccountId, originBoardLocalId);
            String newBoardTitleBaseName = originalBoard.getBoard().getTitle().trim();
            int newBoardTitleCopyIndex = 0;
            //already a copy?
            String regex = " \\(copy [0-9]+\\)$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(originalBoard.getBoard().getTitle());
            if (matcher.find()) {
                String found = matcher.group();
                newBoardTitleBaseName = newBoardTitleBaseName.substring(0, newBoardTitleBaseName.length() - found.length());
                Matcher indexMatcher = Pattern.compile("[0-9]+").matcher(found);
                //noinspection ResultOfMethodCallIgnored
                indexMatcher.find();
                String oldIndexString = indexMatcher.group();
                newBoardTitleCopyIndex = Integer.parseInt(oldIndexString);
            }

            String newBoardTitle;
            do {
                newBoardTitleCopyIndex++;
                newBoardTitle = newBoardTitleBaseName + " (copy " + newBoardTitleCopyIndex + ")";

            } while (dataBaseAdapter.getBoardForAccountByNameDirectly(targetAccountId, newBoardTitle) != null);


            originalBoard.setAccountId(targetAccountId);
            originalBoard.setId(null);
            originalBoard.setLocalId(null);
            originalBoard.getBoard().setTitle(newBoardTitle);
            originalBoard.getBoard().setColor(String.format("%06X", 0xFFFFFF & targetBoardColor));
            originalBoard.getBoard().setOwnerId(newOwner.getLocalId());
            originalBoard.setStatusEnum(DBStatus.LOCAL_EDITED);
            originalBoard.setOwner(newOwner);
            long newBoardId = dataBaseAdapter.createBoardDirectly(originAccountId, originalBoard.getBoard());
            originalBoard.setLocalId(newBoardId);

            boolean isSameAccount = targetAccountId == originAccountId;

            if (isSameAccount) {
                List<AccessControl> aclList = originalBoard.getParticipants();
                for (AccessControl acl : aclList) {
                    acl.setLocalId(null);
                    acl.setId(null);
                    acl.setBoardId(newBoardId);
                    dataBaseAdapter.createAccessControl(targetAccountId, acl);
                }
            }

            Map<Long, Long> oldToNewLabelIdsDictionary = new HashMap<>();

            for (Label label : originalBoard.getLabels()) {
                Long oldLocalId = label.getLocalId();
                label.setLocalId(null);
                label.setId(null);
                label.setAccountId(targetAccountId);
                label.setStatusEnum(DBStatus.LOCAL_EDITED);
                label.setBoardId(newBoardId);
                long newLocalId = dataBaseAdapter.createLabelDirectly(targetAccountId, label);
                oldToNewLabelIdsDictionary.put(oldLocalId, newLocalId);
            }

            List<Stack> oldStacks = originalBoard.getStacks();
            for (Stack stack : oldStacks) {
                Long oldStackId = stack.getLocalId();
                stack.setLocalId(null);
                stack.setId(null);
                stack.setStatusEnum(DBStatus.LOCAL_EDITED);
                stack.setAccountId(targetAccountId);
                stack.setBoardId(newBoardId);
                long createdStackId = dataBaseAdapter.createStack(targetAccountId, stack);
                if (cloneCards) {
                    List<FullCard> oldCards = dataBaseAdapter.getFullCardsForStackDirectly(originAccountId, oldStackId, null);
                    for (FullCard oldCard : oldCards) {
                        Card newCard = oldCard.getCard();
                        newCard.setId(null);
                        newCard.setUserId(newOwner.getLocalId());
                        newCard.setLocalId(null);
                        newCard.setStackId(createdStackId);
                        newCard.setAccountId(targetAccountId);
                        newCard.setStatusEnum(DBStatus.LOCAL_EDITED);
                        long createdCardId = dataBaseAdapter.createCardDirectly(targetAccountId, newCard);
                        if (oldCard.getLabels() != null) {
                            for (Label oldLabel : oldCard.getLabels()) {
                                Long newLabelId = oldToNewLabelIdsDictionary.get(oldLabel.getLocalId());
                                if (newLabelId != null) {
                                    dataBaseAdapter.createJoinCardWithLabel(newLabelId, createdCardId, DBStatus.LOCAL_EDITED);
                                } else
                                    DeckLog.error("ID of created Label is null! Skipping assignment of ", oldLabel.getTitle(), "…");
                            }
                        }
                        if (isSameAccount && oldCard.getAssignedUsers() != null) {
                            for (User assignedUser : oldCard.getAssignedUsers()) {
                                dataBaseAdapter.createJoinCardWithUser(assignedUser.getLocalId(), createdCardId, DBStatus.LOCAL_EDITED);
                            }
                        }
                    }
                }
            }
            if (connectivityUtil.hasInternetConnection()) {
                Account targetAccount = dataBaseAdapter.getAccountByIdDirectly(targetAccountId);
                final ServerAdapter serverAdapterToUse;
                if (originAccountId == targetAccountId) {
                    serverAdapterToUse = this.serverAdapter;
                } else {
                    try {
                        serverAdapterToUse = new ServerAdapter(context, AccountImporter.getSingleSignOnAccount(context, targetAccount.getName()), connectivityUtil);
                    } catch (NextcloudFilesAppAccountNotFoundException e) {
                        callback.onError(e);
                        return;
                    }
                }
                syncHelperFactory.create(serverAdapterToUse, dataBaseAdapter, null)
                        .setResponseCallback(new ResponseCallback<>(targetAccount) {
                            @Override
                            public void onResponse(Boolean response, Headers headers) {
                                callback.onResponse(dataBaseAdapter.getFullBoardByLocalIdDirectly(targetAccountId, newBoardId), headers);
                            }

                            @SuppressLint("MissingSuperCall")
                            @Override
                            public void onError(Throwable throwable) {
                                callback.onError(throwable);
                            }
                        }).doUpSyncFor(new BoardWithStacksAndLabelsUpSyncDataProvider(dataBaseAdapter.getFullBoardByLocalIdDirectly(targetAccountId, newBoardId)));
            } else {
                callback.onResponse(dataBaseAdapter.getFullBoardByLocalIdDirectly(targetAccountId, newBoardId), IResponseCallback.EMPTY_HEADERS);
            }
        });
    }

    @AnyThread
    public LiveData<List<it.niedermann.nextcloud.deck.model.ocs.Activity>> syncActivitiesForCard(@NonNull Card card) {
        executor.submit(() -> {
            if (connectivityUtil.hasInternetConnection()) {
                if (card.getId() != null) {
                    syncHelperFactory.create(serverAdapter, dataBaseAdapter, null)
                            .setResponseCallback(new ResponseCallback<>(dataBaseAdapter.getAccountByIdDirectly(card.getAccountId())) {
                                @Override
                                public void onResponse(Boolean response, Headers headers) {
                                    // do nothing
                                }
                            }).doSyncFor(new ActivityDataProvider(null, card));
                } else {
                    DeckLog.log("Can not fetch activities for card ", card.getTitle(), "because this card does not have a remote id yet.");
                }
            }
        });
        return dataBaseAdapter.getActivitiesForCard(card.getLocalId());
    }

    @AnyThread
    public void addCommentToCard(long accountId, long cardId, @NonNull DeckComment comment) {
        executor.submit(() -> {
            Account account = dataBaseAdapter.getAccountByIdDirectly(accountId);
            Card card = dataBaseAdapter.getCardByLocalIdDirectly(accountId, cardId);
            OcsComment commentEntity = OcsComment.of(comment);
            new DataPropagationHelper(serverAdapter, dataBaseAdapter, executor).createEntity(new DeckCommentsDataProvider(null, card), commentEntity, new ResponseCallback<>(account) {
                @Override
                public void onResponse(OcsComment response, Headers headers) {
                    // nothing so far
                }
            });
        });
    }

    @AnyThread
    public void updateComment(long accountId, long localCardId, long localCommentId, String comment) {
        executor.submit(() -> {
            Account account = dataBaseAdapter.getAccountByIdDirectly(accountId);
            Card card = dataBaseAdapter.getCardByLocalIdDirectly(accountId, localCardId);
            DeckComment entity = dataBaseAdapter.getCommentByLocalIdDirectly(accountId, localCommentId);
            entity.setMessage(comment);
            OcsComment commentEntity = OcsComment.of(entity);
            new DataPropagationHelper(serverAdapter, dataBaseAdapter, executor).updateEntity(new DeckCommentsDataProvider(null, card), commentEntity, new ResponseCallback<>(account) {
                @Override
                public void onResponse(OcsComment response, Headers headers) {
                    // nothing so far
                }
            });
        });
    }

    @AnyThread
    public void deleteComment(long accountId, long localCardId, long localCommentId, @NonNull IResponseCallback<EmptyResponse> callback) {
        executor.submit(() -> {
            final Account account = dataBaseAdapter.getAccountByIdDirectly(accountId);
            final Card card = dataBaseAdapter.getCardByLocalIdDirectly(accountId, localCardId);
            final DeckComment entity = dataBaseAdapter.getCommentByLocalIdDirectly(accountId, localCommentId);
            final OcsComment commentEntity = OcsComment.of(entity);
            new DataPropagationHelper(serverAdapter, dataBaseAdapter, executor).deleteEntity(new DeckCommentsDataProvider(null, card),
                    commentEntity, ResponseCallback.from(account, callback));
        });
    }

    @AnyThread
    public void deleteBoard(@NonNull Board board, @NonNull IResponseCallback<EmptyResponse> callback) {
        executor.submit(() -> {
            long accountId = board.getAccountId();
            Account account = dataBaseAdapter.getAccountByIdDirectly(accountId);
            FullBoard fullBoard = dataBaseAdapter.getFullBoardByLocalIdDirectly(accountId, board.getLocalId());
            new DataPropagationHelper(serverAdapter, dataBaseAdapter, executor).deleteEntity(new BoardDataProvider(), fullBoard, ResponseCallback.from(account, callback));
        });
    }

    @AnyThread
    public void updateBoard(@NonNull FullBoard board, @NonNull IResponseCallback<FullBoard> callback) {
        executor.submit(() -> {
            long accountId = board.getAccountId();
            Account account = dataBaseAdapter.getAccountByIdDirectly(accountId);
            new DataPropagationHelper(serverAdapter, dataBaseAdapter, executor).updateEntity(new BoardDataProvider(), board, ResponseCallback.from(account, callback));
        });
    }

    @AnyThread
    public void createAccessControl(long accountId, @NonNull AccessControl entity, @NonNull IResponseCallback<AccessControl> callback) {
        executor.submit(() -> {
            Account account = dataBaseAdapter.getAccountByIdDirectly(accountId);
            FullBoard board = dataBaseAdapter.getFullBoardByLocalIdDirectly(accountId, entity.getBoardId());
            new DataPropagationHelper(serverAdapter, dataBaseAdapter, executor).createEntity(
                    new AccessControlDataProvider(null, board, Collections.singletonList(entity)), entity, ResponseCallback.from(account, callback), ((entity1, response) -> {
                        response.setBoardId(entity.getBoardId());
                        response.setUserId(entity.getUser().getLocalId());
                    })
            );
        });
    }

    @AnyThread
    public void updateAccessControl(@NonNull AccessControl entity, @NonNull IResponseCallback<AccessControl> callback) {
        executor.submit(() -> {
            Account account = dataBaseAdapter.getAccountByIdDirectly(entity.getAccountId());
            FullBoard board = dataBaseAdapter.getFullBoardByLocalIdDirectly(entity.getAccountId(), entity.getBoardId());
            new DataPropagationHelper(serverAdapter, dataBaseAdapter, executor).updateEntity(
                    new AccessControlDataProvider(null, board, Collections.singletonList(entity)), entity, ResponseCallback.from(account, callback));
        });
    }

    @AnyThread
    public void deleteAccessControl(@NonNull AccessControl entity, @NonNull IResponseCallback<EmptyResponse> callback) {
        executor.submit(() -> {
            Account account = dataBaseAdapter.getAccountByIdDirectly(entity.getAccountId());
            FullBoard board = dataBaseAdapter.getFullBoardByLocalIdDirectly(entity.getAccountId(), entity.getBoardId());
            new DataPropagationHelper(serverAdapter, dataBaseAdapter, executor).deleteEntity(
                    new AccessControlDataProvider(null, board, Collections.singletonList(entity)), entity, new ResponseCallback<>(account) {
                        @Override
                        public void onResponse(EmptyResponse response, Headers headers) {
                            // revoked own board-access?
                            if (entity.getAccountId() == entity.getAccountId() && entity.getUser().getUid().equals(account.getUserName())) {
                                dataBaseAdapter.saveNeighbourOfBoard(board.getAccountId(), board.getLocalId());
                                dataBaseAdapter.removeCurrentStackId(board.getAccountId(), board.getLocalId());
                                dataBaseAdapter.deleteBoardPhysically(board.getBoard());
                            }
                            callback.onResponse(response, headers);
                        }

                        @SuppressLint("MissingSuperCall")
                        @Override
                        public void onError(Throwable throwable) {
                            callback.onError(throwable);
                        }
                    });
        });
    }

    @AnyThread
    public void createStack(long accountId, long boardLocalId, @NonNull String title, @NonNull IResponseCallback<FullStack> callback) {
        executor.submit(() -> {
            DeckLog.info("Create Stack in account", accountId, "on board with local ID ", boardLocalId);
            Stack stack = new Stack(title, boardLocalId);
            Account account = dataBaseAdapter.getAccountByIdDirectly(accountId);
            FullBoard board = dataBaseAdapter.getFullBoardByLocalIdDirectly(accountId, stack.getBoardId());
            FullStack fullStack = new FullStack();
            stack.setOrder(dataBaseAdapter.getHighestStackOrderInBoard(stack.getBoardId()) + 1);
            stack.setAccountId(accountId);
            stack.setBoardId(board.getLocalId());
            fullStack.setStack(stack);
            fullStack.setAccountId(accountId);
            new DataPropagationHelper(serverAdapter, dataBaseAdapter, executor).createEntity(new StackDataProvider(null, board), fullStack, ResponseCallback.from(account, callback));
        });
    }

    @AnyThread
    public void deleteStack(long accountId, long boardLocalId, long stackLocalId, @NonNull IResponseCallback<EmptyResponse> callback) {
        executor.submit(() -> {
            Account account = dataBaseAdapter.getAccountByIdDirectly(accountId);
            FullStack fullStack = dataBaseAdapter.getFullStackByLocalIdDirectly(stackLocalId);
            FullBoard board = dataBaseAdapter.getFullBoardByLocalIdDirectly(accountId, boardLocalId);
            new DataPropagationHelper(serverAdapter, dataBaseAdapter, executor).deleteEntity(new StackDataProvider(null, board), fullStack, ResponseCallback.from(account, callback));
        });
    }

    @AnyThread
    public void updateStackTitle(long localStackId, @NonNull String newTitle, @NonNull IResponseCallback<FullStack> callback) {
        executor.submit(() -> {
            FullStack stack = dataBaseAdapter.getFullStackByLocalIdDirectly(localStackId);
            FullBoard fullBoard = dataBaseAdapter.getFullBoardByLocalIdDirectly(stack.getAccountId(), stack.getStack().getBoardId());
            Account account = dataBaseAdapter.getAccountByIdDirectly(stack.getAccountId());
            stack.getStack().setTitle(newTitle);
            updateStack(account, fullBoard, stack, callback);
        });
    }

    @AnyThread
    private void updateStack(@NonNull Account account, @NonNull FullBoard board, @NonNull FullStack stack, @NonNull IResponseCallback<FullStack> callback) {
        executor.submit(() -> new DataPropagationHelper(serverAdapter, dataBaseAdapter, executor).updateEntity(new StackDataProvider(null, board), stack, ResponseCallback.from(account, callback)));
    }

    /**
     * Reorders stacks and ensures order validity
     *
     * @param accountId    The ID of the Account
     * @param boardLocalId The ID of the Board the stack is in
     * @param stackLocalId The ID of the stack to move
     * @param moveToRight  <code>true</code> to move right, <code>false</code> to move left
     */
    @AnyThread
    public void reorderStack(long accountId, long boardLocalId, long stackLocalId, boolean moveToRight) {
        executor.submit(() -> {
            final Account account = dataBaseAdapter.getAccountByIdDirectly(accountId);
            final FullBoard fullBoard = dataBaseAdapter.getFullBoardByLocalIdDirectly(accountId, boardLocalId);
            final List<FullStack> stacks = dataBaseAdapter.getFullStacksForBoardDirectly(accountId, boardLocalId);

            int lastOrderValue = -1;
            boolean moveDone = false;
            for (int i = 0; i < stacks.size(); i++) {
                FullStack s = stacks.get(i);
                boolean currentStackChanged = false;
                // ensure order validity
                if (lastOrderValue >= s.getStack().getOrder()) {
                    s.getStack().setOrder(lastOrderValue + 1);
                    currentStackChanged = true;
                }
                lastOrderValue = s.getStack().getOrder();

                if (!moveDone && i < stacks.size() - 1 && (moveToRight ? s : stacks.get(i + 1)).getLocalId() == stackLocalId) {
                    FullStack rightStack = stacks.get(i + 1);
                    // fix orders
                    rightStack.getStack().setOrder(lastOrderValue);
                    s.getStack().setOrder(lastOrderValue + 1);
                    // update the other one
                    updateStack(account, fullBoard, rightStack, IResponseCallback.empty());
                    // ensure the current one is updated as well
                    currentStackChanged = true;
                    stacks.set(i, stacks.get(i + 1));
                    stacks.set(i + 1, s);
                    moveDone = true;
                }

                if (currentStackChanged) {
                    updateStack(account, fullBoard, s, IResponseCallback.empty());
                }
            }
        });
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
//        executor.submit(() -> {
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
    public void createFullCard(long accountId, long localBoardId, long localStackId, @NonNull FullCard card, @NonNull IResponseCallback<FullCard> callback) {
        executor.submit(() -> {
            Account account = dataBaseAdapter.getAccountByIdDirectly(accountId);
            User owner = dataBaseAdapter.getUserByUidDirectly(accountId, account.getUserName());
            FullStack stack = dataBaseAdapter.getFullStackByLocalIdDirectly(localStackId);
            Board board = dataBaseAdapter.getBoardByLocalIdDirectly(localBoardId);
            card.getCard().setUserId(owner.getLocalId());
            card.getCard().setStackId(stack.getLocalId());
            card.getCard().setAccountId(accountId);
            card.getCard().setStatusEnum(DBStatus.LOCAL_EDITED);
            card.getCard().setOrder(dataBaseAdapter.getHighestCardOrderInStack(localStackId) + 1);
            long localCardId = dataBaseAdapter.createCardDirectly(accountId, card.getCard());
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

            if (connectivityUtil.hasInternetConnection()) {
                syncHelperFactory.create(serverAdapter, dataBaseAdapter, null)
                        .setResponseCallback(new ResponseCallback<>(account) {
                            @Override
                            public void onResponse(Boolean response, Headers headers) {
                                callback.onResponse(card, headers);
                            }

                            @SuppressLint("MissingSuperCall")
                            @Override
                            public void onError(Throwable throwable) {
                                if (throwable.getClass() == DeckException.class && ((DeckException) throwable).getHint().equals(DeckException.Hint.DEPENDENCY_NOT_SYNCED_YET)) {
                                    callback.onResponse(card, IResponseCallback.EMPTY_HEADERS);
                                } else {
                                    callback.onError(throwable);
                                }
                            }
                        })
                        .doUpSyncFor(new CardDataProvider(null, board, stack));
            } else {
                callback.onResponse(card, IResponseCallback.EMPTY_HEADERS);
            }
        });
    }

    @AnyThread
    public void deleteCard(@NonNull Card card, @NonNull IResponseCallback<EmptyResponse> callback) {
        executor.submit(() -> {
            FullCard fullCard = dataBaseAdapter.getFullCardByLocalIdDirectly(card.getAccountId(), card.getLocalId());
            if (fullCard == null) {
                throw new IllegalArgumentException("card with id " + card.getLocalId() + " to delete does not exist.");
            }
            Account account = dataBaseAdapter.getAccountByIdDirectly(card.getAccountId());
            FullStack stack = dataBaseAdapter.getFullStackByLocalIdDirectly(card.getStackId());
            Board board = dataBaseAdapter.getBoardByLocalIdDirectly(stack.getStack().getBoardId());
            new DataPropagationHelper(serverAdapter, dataBaseAdapter, executor).deleteEntity(new CardPropagationDataProvider(null, board, stack), fullCard, ResponseCallback.from(account, callback));
        });
    }

    @AnyThread
    public void archiveCard(@NonNull FullCard card, @NonNull IResponseCallback<FullCard> callback) {
        executor.submit(() -> {
            Account account = dataBaseAdapter.getAccountByIdDirectly(card.getAccountId());
            FullStack stack = dataBaseAdapter.getFullStackByLocalIdDirectly(card.getCard().getStackId());
            Board board = dataBaseAdapter.getBoardByLocalIdDirectly(stack.getStack().getBoardId());
            card.getCard().setArchived(true);
            updateCardForArchive(stack, board, card, ResponseCallback.from(account, callback));
        });
    }

    private void updateCardForArchive(FullStack stack, Board board, FullCard card, @NonNull ResponseCallback<FullCard> callback) {
        new DataPropagationHelper(serverAdapter, dataBaseAdapter, executor).updateEntity(new CardDataProvider(null, board, stack), card, callback);
    }

    @AnyThread
    public void dearchiveCard(@NonNull FullCard card, @NonNull IResponseCallback<FullCard> callback) {
        executor.submit(() -> {
            Account account = dataBaseAdapter.getAccountByIdDirectly(card.getAccountId());
            FullStack stack = dataBaseAdapter.getFullStackByLocalIdDirectly(card.getCard().getStackId());
            Board board = dataBaseAdapter.getBoardByLocalIdDirectly(stack.getStack().getBoardId());
            card.getCard().setArchived(false);
            updateCardForArchive(stack, board, card, ResponseCallback.from(account, callback));
        });
    }

    @AnyThread
    public void archiveCardsInStack(long accountId, long stackLocalId, @NonNull FilterInformation filterInformation, @NonNull IResponseCallback<EmptyResponse> callback) {
        executor.submit(() -> {
            Account account = dataBaseAdapter.getAccountByIdDirectly(accountId);
            FullStack stack = dataBaseAdapter.getFullStackByLocalIdDirectly(stackLocalId);
            Board board = dataBaseAdapter.getBoardByLocalIdDirectly(stack.getStack().getBoardId());
            List<FullCard> cards = dataBaseAdapter.getFullCardsForStackDirectly(accountId, stackLocalId, filterInformation);
            if (cards.size() <= 0) {
                callback.onResponse(null, IResponseCallback.EMPTY_HEADERS);
                return;
            }
            final CountDownLatch latch = new CountDownLatch(cards.size());
            for (FullCard card : cards) {
                if (card.getCard().isArchived()) {
                    latch.countDown();
                    continue;
                }
                card.getCard().setArchived(true);
                updateCardForArchive(stack, board, card, new ResponseCallback<>(account) {
                    @Override
                    public void onResponse(FullCard response, Headers headers) {
                        latch.countDown();
                    }

                    @SuppressLint("MissingSuperCall")
                    @Override
                    public void onError(Throwable throwable) {
                        latch.countDown();
                        callback.onError(throwable);
                    }
                });
            }
            try {
                latch.await();
                callback.onResponse(null, IResponseCallback.EMPTY_HEADERS);
            } catch (InterruptedException e) {
                callback.onError(e);
            }
        });
    }

    @AnyThread
    public void archiveBoard(@NonNull Board board, @NonNull IResponseCallback<FullBoard> callback) {
        executor.submit(() -> {
            try {
                final var fullBoard = dataBaseAdapter.getFullBoardByLocalIdDirectly(board.getAccountId(), board.getLocalId());
                fullBoard.getBoard().setArchived(true);
                updateBoard(fullBoard, new IResponseCallback<>() {
                    @Override
                    public void onResponse(FullBoard response, Headers headers) {
                        dataBaseAdapter.saveNeighbourOfBoard(fullBoard.getAccountId(), fullBoard.getLocalId());
                        callback.onResponse(response, headers);
                    }

                    @SuppressLint("MissingSuperCall")
                    @Override
                    public void onError(Throwable throwable) {
                        callback.onError(throwable);
                    }
                });
            } catch (Throwable e) {
                callback.onError(e);
            }
        });
    }

    @AnyThread
    public void dearchiveBoard(@NonNull Board board, @NonNull IResponseCallback<FullBoard> callback) {
        executor.submit(() -> {
            try {
                FullBoard b = dataBaseAdapter.getFullBoardByLocalIdDirectly(board.getAccountId(), board.getLocalId());
                b.getBoard().setArchived(false);
                updateBoard(b, new IResponseCallback<>() {
                    @Override
                    public void onResponse(FullBoard response, Headers headers) {
                        dataBaseAdapter.saveCurrentBoardId(b.getAccountId(), b.getLocalId());
                        callback.onResponse(response, headers);
                    }

                    @SuppressLint("MissingSuperCall")
                    @Override
                    public void onError(Throwable throwable) {
                        callback.onError(throwable);
                    }
                });
            } catch (Throwable e) {
                callback.onError(e);
            }
        });
    }

    @AnyThread
    public void updateCard(@NonNull FullCard card, @NonNull IResponseCallback<FullCard> callback) {
        executor.submit(() -> {
            final FullCard fullCardFromDB = dataBaseAdapter.getFullCardByLocalIdDirectly(card.getAccountId(), card.getLocalId());
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
            if (connectivityUtil.hasInternetConnection()) {
                Account account = dataBaseAdapter.getAccountByIdDirectly(card.getAccountId());
                syncHelperFactory.create(serverAdapter, dataBaseAdapter, null)
                        .setResponseCallback(new ResponseCallback<>(account) {
                            @Override
                            public void onResponse(Boolean response, Headers headers) {
                                callback.onResponse(dataBaseAdapter.getFullCardByLocalIdDirectly(card.getAccountId(), card.getLocalId()), headers);
                            }

                            @SuppressLint("MissingSuperCall")
                            @Override
                            public void onError(Throwable throwable) {
                                callback.onError(throwable);
                            }
                        }).doUpSyncFor(new CardPropagationDataProvider(null, board, stack));
            } else {
                callback.onResponse(card, IResponseCallback.EMPTY_HEADERS);
            }
        });
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
    public void moveCard(long originAccountId, long originCardLocalId, long targetAccountId, long targetBoardLocalId, long targetStackLocalId, @NonNull IResponseCallback<EmptyResponse> callback) {
        executor.submit(() -> {
            final FullCard originalCard = dataBaseAdapter.getFullCardByLocalIdDirectly(originAccountId, originCardLocalId);
            final int newIndex = dataBaseAdapter.getHighestCardOrderInStack(targetStackLocalId) + 1;
            final FullBoard originalBoard = dataBaseAdapter.getFullBoardByLocalCardIdDirectly(originCardLocalId);
            // ### maybe shortcut possible? (just moved to another stack)
            if (targetBoardLocalId == originalBoard.getLocalId()) {
                reorder(originAccountId, originalCard, targetStackLocalId, newIndex);
                callback.onResponse(null, IResponseCallback.EMPTY_HEADERS);
                return;
            }
            // ### get rid of original card where it is now.
            final Card originalInnerCard = originalCard.getCard();
            deleteCard(new Card(originalInnerCard), IResponseCallback.empty());
            // ### clone card itself
            // TODO Why not use copy constructor? Attention, something might missing, e. g. accountId
            originalInnerCard.setAccountId(targetAccountId);
            originalInnerCard.setId(null);
            originalInnerCard.setLocalId(null);
            originalInnerCard.setStatusEnum(DBStatus.LOCAL_EDITED);
            originalInnerCard.setStackId(targetStackLocalId);
            originalInnerCard.setOrder(newIndex);
            originalInnerCard.setArchived(false);
            originalInnerCard.setAttachmentCount(0);
            originalInnerCard.setCommentsUnread(0);
            final FullCard fullCardForServerPropagation = new FullCard();
            fullCardForServerPropagation.setCard(originalInnerCard);

            final Account targetAccount = dataBaseAdapter.getAccountByIdDirectly(targetAccountId);
            final FullBoard targetBoard = dataBaseAdapter.getFullBoardByLocalIdDirectly(targetAccountId, targetBoardLocalId);
            final FullStack targetFullStack = dataBaseAdapter.getFullStackByLocalIdDirectly(targetStackLocalId);
            final User userOfTargetAccount = dataBaseAdapter.getUserByUidDirectly(targetAccountId, targetAccount.getUserName());
            final CountDownLatch latch = new CountDownLatch(1);

            ServerAdapter serverToUse = serverAdapter;
            if (originAccountId != targetAccountId) {
                try {
                    serverToUse = new ServerAdapter(context, AccountImporter.getSingleSignOnAccount(context, targetAccount.getName()), connectivityUtil);
                } catch (NextcloudFilesAppAccountNotFoundException e) {
                    callback.onError(e);
                    throw new RuntimeException(e);
                }
            }
            new DataPropagationHelper(serverToUse, dataBaseAdapter, executor).createEntity(new CardPropagationDataProvider(null, targetBoard.getBoard(), targetFullStack), fullCardForServerPropagation, new ResponseCallback<>(targetAccount) {
                @Override
                public void onResponse(FullCard response, Headers headers) {
                    originalInnerCard.setId(response.getId());
                    originalInnerCard.setLocalId(response.getLocalId());
                    latch.countDown();
                }

                @Override
                @SuppressLint("MissingSuperCall")
                public void onError(Throwable throwable) {
                    callback.onError(new RuntimeException("unable to create card in moveCard target", throwable));
                }
            }, (FullCard entity, FullCard response) -> {
                response.getCard().setUserId(userOfTargetAccount.getLocalId());
                response.getCard().setStackId(targetFullStack.getLocalId());
                entity.getCard().setUserId(userOfTargetAccount.getLocalId());
                entity.getCard().setStackId(targetFullStack.getLocalId());
            });

            try {
                latch.await();
            } catch (InterruptedException e) {
                callback.onError(new RuntimeException("error fulfilling countDownLatch", e));
            }

            final long newCardId = originalInnerCard.getLocalId();

            // ### clone labels, assign them
            // prepare
            // has user of targetaccount manage permissions?
            boolean hasManagePermission = targetBoard.getBoard().getOwnerId() == userOfTargetAccount.getLocalId();
            List<AccessControl> aclOfTargetBoard = dataBaseAdapter.getAccessControlByLocalBoardIdDirectly(targetAccountId, targetBoard.getLocalId());
            if (!hasManagePermission) {
                for (AccessControl accessControl : aclOfTargetBoard) {
                    if (accessControl.getUserId().equals(userOfTargetAccount.getLocalId()) && accessControl.isPermissionManage()) {
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
                        createAndAssignLabelToCard(targetBoard.getAccountId(), originalLabel, newCardId, serverToUse);
                    }
                } else {
                    assignLabelToCard(existingMatch, originalInnerCard, serverToUse);
                }
            }

            // ### Clone assigned users
            final Account originalAccount = dataBaseAdapter.getAccountByIdDirectly(originAccountId);
            // same instance? otherwise doesn't make sense
            if (originalAccount.getUrl().equalsIgnoreCase(targetAccount.getUrl())) {
                for (User assignedUser : originalCard.getAssignedUsers()) {
                    // has assignedUser at least view permissions?
                    boolean hasViewPermission = targetBoard.getBoard().getOwnerId() == assignedUser.getLocalId();
                    if (!hasViewPermission) {
                        for (AccessControl accessControl : aclOfTargetBoard) {
                            if (accessControl.getUserId().equals(userOfTargetAccount.getLocalId())) {
                                // ACL exists, so viewing is granted
                                hasViewPermission = true;
                                break;
                            }
                        }
                    }
                    if (hasViewPermission) {
                        assignUserToCard(assignedUser, originalInnerCard);
                    }
                }
            }
            callback.onResponse(null, IResponseCallback.EMPTY_HEADERS);
        });
    }

    @AnyThread
    public void createLabel(long accountId, Label label, long localBoardId, @NonNull IResponseCallback<Label> callback) {
        executor.submit(() -> {
            Label existing = dataBaseAdapter.getLabelByBoardIdAndTitleDirectly(label.getBoardId(), label.getTitle());
            if (existing != null) {
                callback.onError(new SQLiteConstraintException("label \"" + label.getTitle() + "\" already exists for this board!"));
                return;
            }
            Account account = dataBaseAdapter.getAccountByIdDirectly(accountId);
            Board board = dataBaseAdapter.getBoardByLocalIdDirectly(localBoardId);
            label.setAccountId(accountId);
            new DataPropagationHelper(serverAdapter, dataBaseAdapter, executor).createEntity(new LabelDataProvider(null, board, null), label, ResponseCallback.from(account, callback), (entity, response) -> response.setBoardId(board.getLocalId()));
        });
    }

    @AnyThread
    private MutableLiveData<Label> createAndAssignLabelToCard(long accountId, @NonNull Label label, long localCardId, ServerAdapter serverAdapterToUse) {
        MutableLiveData<Label> liveData = new MutableLiveData<>();
        executor.submit(() -> {
            Account account = dataBaseAdapter.getAccountByIdDirectly(accountId);
            Board board = dataBaseAdapter.getBoardByLocalCardIdDirectly(localCardId);
            label.setAccountId(accountId);
            new DataPropagationHelper(serverAdapterToUse, dataBaseAdapter, executor).createEntity(new LabelDataProvider(null, board, null), label, new ResponseCallback<>(account) {
                @Override
                public void onResponse(Label response, Headers headers) {
                    assignLabelToCard(response, dataBaseAdapter.getCardByLocalIdDirectly(accountId, localCardId));
                    liveData.postValue(response);
                }

                @Override
                public void onError(Throwable throwable) {
                    super.onError(throwable);
                    assignLabelToCard(label, dataBaseAdapter.getCardByLocalIdDirectly(accountId, localCardId));
                }
            }, (entity, response) -> response.setBoardId(board.getLocalId()));
        });
        return liveData;
    }

    @AnyThread
    public void deleteLabel(@NonNull Label label, @NonNull IResponseCallback<EmptyResponse> callback) {
        executor.submit(() -> {
            Account account = dataBaseAdapter.getAccountByIdDirectly(label.getAccountId());
            Board board = dataBaseAdapter.getBoardByLocalIdDirectly(label.getBoardId());
            new DataPropagationHelper(serverAdapter, dataBaseAdapter, executor)
                    .deleteEntity(new LabelDataProvider(null, board, Collections.emptyList()), label, ResponseCallback.from(account, callback));
        });
    }

    @AnyThread
    public void updateLabel(@NonNull Label label, @NonNull IResponseCallback<Label> callback) {
        executor.submit(() -> {
            Account account = dataBaseAdapter.getAccountByIdDirectly(label.getAccountId());
            Board board = dataBaseAdapter.getBoardByLocalIdDirectly(label.getBoardId());
            new DataPropagationHelper(serverAdapter, dataBaseAdapter, executor)
                    .updateEntity(new LabelDataProvider(null, board, Collections.emptyList()), label, ResponseCallback.from(account, callback));
        });
    }

    @AnyThread
    public void assignUserToCard(@NonNull User user, @NonNull Card card) {
        executor.submit(() -> {
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
            if (connectivityUtil.hasInternetConnection()) {
                UserForAssignment userForAssignment = dataBaseAdapter.getUserForAssignmentDirectly(user.getLocalId());

                serverAdapter.assignUserToCard(board.getId(), stack.getId(), card.getId(), userForAssignment, new ResponseCallback<>(account) {

                    @Override
                    public void onResponse(EmptyResponse response, Headers headers) {
                        dataBaseAdapter.setStatusForJoinCardWithUser(localCardId, localUserId, DBStatus.UP_TO_DATE.getId());
                    }
                });
            }
        });
    }

    @AnyThread
    public void assignLabelToCard(@NonNull Label label, @NonNull Card card) {
        assignLabelToCard(label, card, serverAdapter);
    }

    @AnyThread
    public void assignLabelToCard(@NonNull Label label, @NonNull Card card, ServerAdapter serverAdapterToUse) {
        executor.submit(() -> {
            final long localLabelId = label.getLocalId();
            final long localCardId = card.getLocalId();
            dataBaseAdapter.createJoinCardWithLabel(localLabelId, localCardId, DBStatus.LOCAL_EDITED);
            if (label.getId() == null || card.getId() == null) {
                return;
            }
            Stack stack = dataBaseAdapter.getStackByLocalIdDirectly(card.getStackId());
            Board board = dataBaseAdapter.getBoardByLocalIdDirectly(stack.getBoardId());
            Account account = dataBaseAdapter.getAccountByIdDirectly(card.getAccountId());
            if (connectivityUtil.hasInternetConnection()) {
                serverAdapterToUse.assignLabelToCard(board.getId(), stack.getId(), card.getId(), label.getId(), new ResponseCallback<>(account) {

                    @Override
                    public void onResponse(EmptyResponse response, Headers headers) {
                        dataBaseAdapter.setStatusForJoinCardWithLabel(localCardId, localLabelId, DBStatus.UP_TO_DATE.getId());
                    }
                });
            }
        });
    }

    @AnyThread
    public void unassignLabelFromCard(@NonNull Label label, @NonNull Card card) {
        executor.submit(() -> {
            dataBaseAdapter.deleteJoinedLabelForCard(card.getLocalId(), label.getLocalId());
            Stack stack = dataBaseAdapter.getStackByLocalIdDirectly(card.getStackId());
            Board board = dataBaseAdapter.getBoardByLocalIdDirectly(stack.getBoardId());
            Account account = dataBaseAdapter.getAccountByIdDirectly(card.getAccountId());
            if (connectivityUtil.hasInternetConnection()) {
                serverAdapter.unassignLabelFromCard(board.getId(), stack.getId(), card.getId(), label.getId(), new ResponseCallback<>(account) {
                    @Override
                    public void onResponse(EmptyResponse response, Headers headers) {
                        dataBaseAdapter.deleteJoinedLabelForCardPhysically(card.getLocalId(), label.getLocalId());
                    }
                });
            }
        });
    }

    @AnyThread
    public void unassignUserFromCard(@NonNull User user, @NonNull Card card) {
        executor.submit(() -> {
            dataBaseAdapter.deleteJoinedUserForCard(card.getLocalId(), user.getLocalId());
            if (connectivityUtil.hasInternetConnection()) {
                Stack stack = dataBaseAdapter.getStackByLocalIdDirectly(card.getStackId());
                Board board = dataBaseAdapter.getBoardByLocalIdDirectly(stack.getBoardId());
                Account account = dataBaseAdapter.getAccountByIdDirectly(card.getAccountId());
                UserForAssignment userForAssignment = dataBaseAdapter.getUserForAssignmentDirectly(user.getLocalId());

                serverAdapter.assignUserToCard(board.getId(), stack.getId(), card.getId(), userForAssignment, new ResponseCallback<>(account) {
                    @Override
                    public void onResponse(EmptyResponse response, Headers headers) {
                        dataBaseAdapter.deleteJoinedUserForCardPhysically(card.getLocalId(), user.getLocalId());
                    }
                });
            }
        });
    }

    public void triggerUserSearch(@NonNull Account account, @NonNull String constraint) {
        executor.submit(() -> serverAdapter.searchUser(constraint, new ResponseCallback<>(account) {
            @Override
            public void onResponse(OcsUserList response, Headers headers) {
                if (response == null || response.getUsers().isEmpty()) {
                    return;
                }
                for (var user : response.getUsers()) {
                    final var existingUser = dataBaseAdapter.getUserByUidDirectly(account.getId(), user.getId());
                    if (existingUser == null) {
                        User newUser = new User();
                        newUser.setStatus(DBStatus.UP_TO_DATE.getId());
                        newUser.setPrimaryKey(user.getId());
                        newUser.setUid(user.getId());
                        newUser.setType(User.TYPE_USER);
                        newUser.setDisplayname(user.getDisplayName());
                        dataBaseAdapter.createUser(account.getId(), newUser);
                    }
                }
                for (var group : response.getGroups()) {
                    final var existingGroup = dataBaseAdapter.getUserByUidDirectly(account.getId(), group.getId());
                    if (existingGroup == null) {
                        User newGroup = new User();
                        newGroup.setStatus(DBStatus.UP_TO_DATE.getId());
                        newGroup.setPrimaryKey(group.getId());
                        newGroup.setUid(group.getId());
                        newGroup.setType(User.TYPE_GROUP);
                        newGroup.setDisplayname(group.getDisplayName());
                        dataBaseAdapter.createUser(account.getId(), newGroup);
                    }
                }
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
            }
        }));
    }

    /**
     * @see <a href="https://github.com/stefan-niedermann/nextcloud-deck/issues/360">reenable reorder</a>
     */
    @AnyThread
    public void reorder(long accountId, @NonNull FullCard movedCard, long newStackId, int newIndex) {
        executor.submit(() -> {
            // read cards of new stack
            List<FullCard> cardsOfNewStack = dataBaseAdapter.getFullCardsForStackDirectly(accountId, newStackId, null);
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

//            if (connectivityUtil.hasInternetConnection()){
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
            if (connectivityUtil.hasInternetConnection()) {
                Stack stack = dataBaseAdapter.getStackByLocalIdDirectly(movedCard.getCard().getStackId());
                FullBoard board = dataBaseAdapter.getFullBoardByLocalIdDirectly(accountId, stack.getBoardId());
                Account account = dataBaseAdapter.getAccountByIdDirectly(movedCard.getCard().getAccountId());
                syncHelperFactory.create(serverAdapter, dataBaseAdapter, Instant.now()).setResponseCallback(new ResponseCallback<>(account) {
                    @Override
                    public void onResponse(Boolean response, Headers headers) {
                        // doNothing();
                    }
                }).doUpSyncFor(new StackDataProvider(null, board));
            }
//        }
        });
    }


    /**
     * FIXME clean up on error
     * When uploading the exact same attachment 2 times to the same card, the server starts burning and gets mad and returns status 500
     * The problem is, that the attachment is still in our local database and everytime one tries to sync, the log is spammed with 500 errors
     * Also this leads to the attachment being present in the card forever with a DBStatus.LOCAL_EDITED
     */
    @AnyThread
    public void addAttachmentToCard(long accountId, long localCardId, @NonNull String mimeType, @NonNull File file, @NonNull IResponseCallback<Attachment> callback) {
        executor.submit(() -> {
            Attachment attachment = populateAttachmentEntityForFile(new Attachment(), localCardId, mimeType, file);
            final Instant now = Instant.now();
            attachment.setLastModifiedLocal(now);
            attachment.setCreatedAt(now);
            FullCard card = dataBaseAdapter.getFullCardByLocalIdDirectly(accountId, localCardId);
            Stack stack = dataBaseAdapter.getStackByLocalIdDirectly(card.getCard().getStackId());
            Board board = dataBaseAdapter.getBoardByLocalIdDirectly(stack.getBoardId());
            Account account = dataBaseAdapter.getAccountByIdDirectly(card.getAccountId());
            new DataPropagationHelper(serverAdapter, dataBaseAdapter, executor).createEntity(
                    new AttachmentDataProvider(null, board, stack, card, Collections.singletonList(attachment)),
                    attachment, ResponseCallback.from(account, callback)
            );
        });
    }

    @AnyThread
    public LiveData<Attachment> updateAttachmentForCard(long accountId, @NonNull Attachment existing, @NonNull String mimeType, @NonNull File file) {
        final var liveData = new MutableLiveData<Attachment>();
        executor.submit(() -> {
            Attachment attachment = populateAttachmentEntityForFile(existing, existing.getCardId(), mimeType, file);
            attachment.setLastModifiedLocal(Instant.now());
            if (connectivityUtil.hasInternetConnection()) {
                FullCard card = dataBaseAdapter.getFullCardByLocalIdDirectly(accountId, existing.getCardId());
                Stack stack = dataBaseAdapter.getStackByLocalIdDirectly(card.getCard().getStackId());
                Board board = dataBaseAdapter.getBoardByLocalIdDirectly(stack.getBoardId());
                Account account = dataBaseAdapter.getAccountByIdDirectly(card.getAccountId());
                new DataPropagationHelper(serverAdapter, dataBaseAdapter, executor)
                        .updateEntity(new AttachmentDataProvider(null, board, stack, card, Collections.singletonList(attachment)), attachment, new ResponseCallback<>(account) {
                            @Override
                            public void onResponse(Attachment response, Headers headers) {
                                liveData.postValue(response);
                            }

                            @SuppressLint("MissingSuperCall")
                            @Override
                            public void onError(Throwable throwable) {
                                DeckLog.error(throwable);
//                                liveData.postError(throwable);
                            }
                        });
            }
        });
        return liveData;
    }

    @AnyThread
    private static Attachment populateAttachmentEntityForFile(@NonNull Attachment target, long localCardId, @NonNull String mimeType, @NonNull File file) {
        target.setCardId(localCardId);
        target.setMimetype(mimeType);
        target.setData(file.getName());
        target.setFilename(file.getName());
        target.setBasename(file.getName());
        target.setLocalPath(file.getAbsolutePath());
        target.setFilesize(file.length());
        return target;
    }

    @AnyThread
    public void deleteAttachmentOfCard(long accountId, long localCardId, long localAttachmentId, @NonNull IResponseCallback<EmptyResponse> callback) {
        executor.submit(() -> {
            if (connectivityUtil.hasInternetConnection()) {
                FullCard card = dataBaseAdapter.getFullCardByLocalIdDirectly(accountId, localCardId);
                Stack stack = dataBaseAdapter.getStackByLocalIdDirectly(card.getCard().getStackId());
                Board board = dataBaseAdapter.getBoardByLocalIdDirectly(stack.getBoardId());
                Attachment attachment = dataBaseAdapter.getAttachmentByLocalIdDirectly(accountId, localAttachmentId);
                Account account = dataBaseAdapter.getAccountByIdDirectly(card.getAccountId());

                new DataPropagationHelper(serverAdapter, dataBaseAdapter, executor)
                        .deleteEntity(new AttachmentDataProvider(null, board, stack, card, Collections.singletonList(attachment)), attachment, ResponseCallback.from(account, callback));
            }
        });
    }

    /**
     * FIXME <a href="https://github.com/stefan-niedermann/nextcloud-deck/issues/640">GitHub Issue #640</a>
     */
    public static boolean isNoOnVoidError(Throwable t) {
        return !(t instanceof NullPointerException) || !"Attempt to invoke interface method 'void io.reactivex.disposables.Disposable.dispose()' on a null object reference".equals(t.getMessage());
    }
}
