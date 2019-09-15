package it.niedermann.nextcloud.deck.persistence.sync;

import android.app.Activity;
import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;
import com.nextcloud.android.sso.exceptions.NoCurrentAccountSelectedException;

import java.util.Date;
import java.util.List;

import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.api.LastSyncUtil;
import it.niedermann.nextcloud.deck.model.AccessControl;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.enums.DBStatus;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.model.full.FullStack;
import it.niedermann.nextcloud.deck.model.ocs.Capabilities;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.ServerAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DataBaseAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.WrappedLiveData;
import it.niedermann.nextcloud.deck.persistence.sync.helpers.DataPropagationHelper;
import it.niedermann.nextcloud.deck.persistence.sync.helpers.SyncHelper;
import it.niedermann.nextcloud.deck.persistence.sync.helpers.providers.BoardDataProvider;
import it.niedermann.nextcloud.deck.persistence.sync.helpers.providers.CardPropagationDataProvider;
import it.niedermann.nextcloud.deck.persistence.sync.helpers.providers.LabelDataProvider;
import it.niedermann.nextcloud.deck.persistence.sync.helpers.providers.StackDataProvider;
import it.niedermann.nextcloud.deck.util.DateUtil;

public class SyncManager {


    private DataBaseAdapter dataBaseAdapter;
    private ServerAdapter serverAdapter;

    public SyncManager(Activity sourceActivity) {
        Context applicationContext = sourceActivity.getApplicationContext();
        LastSyncUtil.init(applicationContext);
        dataBaseAdapter = new DataBaseAdapter(applicationContext);
        this.serverAdapter = new ServerAdapter(applicationContext, sourceActivity);
    }

    private void doAsync(Runnable r) {
        new Thread(r).start();
    }

    public void synchronize(IResponseCallback<Boolean> responseCallback) {
        if (    responseCallback == null ||
                responseCallback.getAccount() == null ||
                responseCallback.getAccount().getId() == null){
            throw new IllegalArgumentException("please provide an account ID.");
        }
        doAsync(() -> {
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
                    doAsync(() -> syncHelper.doUpSyncFor(new BoardDataProvider()));

                }

                @Override
                public void onError(Throwable throwable) {
                    super.onError(throwable);
                    responseCallback.onError(throwable);
                }
            };

            syncHelper.setResponseCallback(callback);

            syncHelper.doSyncFor(new BoardDataProvider());
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

    public LiveData<List<Account>> readAccounts() {
        return dataBaseAdapter.readAccounts();
    }

    public void getServerVersion(IResponseCallback<Capabilities> callback){
        serverAdapter.getCapabilities(callback);
    }

    public LiveData<List<Board>> getBoards(long accountId) {
        return dataBaseAdapter.getBoards(accountId);
    }

    public LiveData<FullBoard> createBoard(long accountId, Board board) {
            MutableLiveData<FullBoard> liveData = new MutableLiveData<>();
            doAsync(() -> {
                Account account = dataBaseAdapter.getAccountByIdDirectly(accountId);
                User owner = dataBaseAdapter.getUserByUidDirectly(accountId, account.getUserName());
                FullBoard fullBoard = new FullBoard();
                board.setOwnerId(owner.getLocalId());
                fullBoard.setOwner(owner);
                fullBoard.setBoard(board);
                board.setAccountId(accountId);
                fullBoard.setAccountId(accountId);
                new DataPropagationHelper(serverAdapter, dataBaseAdapter).createEntity(new BoardDataProvider() ,fullBoard, new IResponseCallback<FullBoard>(account) {
                    @Override
                    public void onResponse(FullBoard response) {
                        liveData.postValue(response);
                    }
                });
            });
        return liveData;

    }

    public void deleteBoard(Board board) {
        long accountId = board.getAccountId();
        doAsync(() -> {
            Account account = dataBaseAdapter.getAccountByIdDirectly(accountId);
            FullBoard fullBoard = dataBaseAdapter.getFullBoardByLocalIdDirectly(accountId, board.getLocalId());
            new DataPropagationHelper(serverAdapter, dataBaseAdapter).deleteEntity(new BoardDataProvider() ,fullBoard, new IResponseCallback<FullBoard>(account) {
                @Override
                public void onResponse(FullBoard response) {
                    // doNothing
                }
            });
        });
    }

    public void updateBoard(FullBoard board) {
        MutableLiveData<FullBoard> liveData = new MutableLiveData<>();
        long accountId = board.getAccountId();
        doAsync(() -> {
            Account account = dataBaseAdapter.getAccountByIdDirectly(accountId);
            User owner = dataBaseAdapter.getUserByUidDirectly(accountId, account.getUserName());
            new DataPropagationHelper(serverAdapter, dataBaseAdapter).updateEntity(new BoardDataProvider() ,board, new IResponseCallback<FullBoard>(account) {
                @Override
                public void onResponse(FullBoard response) {
                    liveData.postValue(response);
                }
            });
        });
    }

    public LiveData<List<FullStack>> getStacksForBoard(long accountId, long localBoardId) {
        return dataBaseAdapter.getStacks(accountId, localBoardId);
    }

    public LiveData<FullStack> getStack(long accountId, long localStackId) {
        return dataBaseAdapter.getStack(accountId, localStackId);
    }

    public void createAccessControl(long accountId, AccessControl entity) {
        dataBaseAdapter.createAccessControl(accountId, entity);
    }

    public AccessControl getAccessControlByRemoteIdDirectly(long accountId, Long id) {
        return dataBaseAdapter.getAccessControlByRemoteIdDirectly(accountId, id);
    }

    public void updateAccessControl(AccessControl entity) {
        dataBaseAdapter.updateAccessControl(entity, true);
    }

    public LiveData<FullBoard> getFullBoardById(Long accountId, Long localId) {
        return dataBaseAdapter.getFullBoardById(accountId, localId);
    }


    public LiveData<FullStack> createStack(long accountId, Stack stack) {
        MutableLiveData<FullStack> liveData = new MutableLiveData<>();
        doAsync(() -> {
            Account account = dataBaseAdapter.getAccountByIdDirectly(accountId);
            FullBoard board = dataBaseAdapter.getFullBoardByLocalIdDirectly(accountId, stack.getBoardId());
            FullStack fullStack = new FullStack();
            stack.setAccountId(accountId);
            stack.setBoardId(board.getLocalId());
            fullStack.setStack(stack);
            fullStack.setAccountId(accountId);
            new DataPropagationHelper(serverAdapter, dataBaseAdapter).createEntity(new StackDataProvider(null, board) ,fullStack, new IResponseCallback<FullStack>(account) {
                @Override
                public void onResponse(FullStack response) {
                    liveData.postValue(response);
                }
            });
        });
        return liveData;
    }

    public LiveData<FullStack> deleteStack(Stack stack) {
        MutableLiveData<FullStack> liveData = new MutableLiveData<>();
        doAsync(() -> {
            Account account = dataBaseAdapter.getAccountByIdDirectly(stack.getAccountId());
            FullStack fullStack = dataBaseAdapter.getFullStackByLocalIdDirectly(stack.getLocalId());
            FullBoard board = dataBaseAdapter.getFullBoardByLocalIdDirectly(stack.getAccountId(), stack.getBoardId());
            new DataPropagationHelper(serverAdapter, dataBaseAdapter).deleteEntity(new StackDataProvider(null, board) ,fullStack, new IResponseCallback<FullStack>(account) {
                @Override
                public void onResponse(FullStack response) {
                    liveData.postValue(response);
                }
            });
        });
        return liveData;
    }

    public void updateStack(Stack stack) {
        //TODO: Tell the server
        dataBaseAdapter.updateStack(stack, true);

    }

    public LiveData<FullCard> getCardByLocalId(long accountId, long cardLocalId) {
        return dataBaseAdapter.getCardByLocalId(accountId, cardLocalId);
    }

    public LiveData<List<FullCard>> getFullCardsForStack(long accountId, long localStackId) {
        return dataBaseAdapter.getFullCardsForStack(accountId, localStackId);
    }

    public LiveData<FullCard> createCard(long accountId, long localBoardId, long localStackId, Card card) {

        MutableLiveData<FullCard> liveData = new MutableLiveData<>();
        doAsync(() -> {
            Account account = dataBaseAdapter.getAccountByIdDirectly(accountId);
            User owner = dataBaseAdapter.getUserByUidDirectly(accountId, account.getUserName());
            FullStack stack = dataBaseAdapter.getFullStackByLocalIdDirectly(localStackId);
            Board board = dataBaseAdapter.getBoardByLocalIdDirectly(localBoardId);
            card.setStackId(stack.getLocalId());
            FullCard fullCard = new FullCard();
            fullCard.setCard(card);
            fullCard.setOwner(owner);
            fullCard.setAccountId(accountId);
            new DataPropagationHelper(serverAdapter, dataBaseAdapter).createEntity(new CardPropagationDataProvider(null, board, stack) ,fullCard, new IResponseCallback<FullCard>(account) {
                @Override
                public void onResponse(FullCard response) {
                    liveData.postValue(response);
                }
            }, (FullCard entity, FullCard response) -> {
                response.getCard().setUserId(entity.getCard().getUserId());
                response.getCard().setStackId(stack.getLocalId());
            });
        });
        return liveData;
    }

    public MutableLiveData<FullCard> deleteCard(Card card) {
        MutableLiveData<FullCard> liveData = new MutableLiveData<>();
        doAsync(() -> {
            FullCard fullCard = dataBaseAdapter.getFullCardByLocalIdDirectly(card.getAccountId(), card.getLocalId());
            if (fullCard== null) {
                throw new IllegalArgumentException("card to delete does not exist.");
            }
            Account account = dataBaseAdapter.getAccountByIdDirectly(card.getAccountId());
            FullStack stack = dataBaseAdapter.getFullStackByLocalIdDirectly(card.getStackId());
            Board board = dataBaseAdapter.getBoardByLocalIdDirectly(stack.getStack().getBoardId());
            new DataPropagationHelper(serverAdapter, dataBaseAdapter).deleteEntity(new CardPropagationDataProvider(null, board, stack) ,fullCard, new IResponseCallback<FullCard>(account) {
                @Override
                public void onResponse(FullCard response) {
                    liveData.postValue(response);
                }
            });
        });
        return liveData;
    }
    public MutableLiveData<FullCard> archiveCard(Card card) {
        card.setArchived(true);
        return updateCard(card);
    }

    public MutableLiveData<FullCard> updateCard(Card card) {
        MutableLiveData<FullCard> liveData = new MutableLiveData<>();
        doAsync(() -> {
            FullCard fullCard = dataBaseAdapter.getFullCardByLocalIdDirectly(card.getAccountId(), card.getLocalId());
            if (fullCard== null) {
                throw new IllegalArgumentException("card to update does not exist.");
            }
            Account account = dataBaseAdapter.getAccountByIdDirectly(card.getAccountId());
            FullStack stack = dataBaseAdapter.getFullStackByLocalIdDirectly(card.getStackId());
            Board board = dataBaseAdapter.getBoardByLocalIdDirectly(stack.getStack().getBoardId());
            fullCard.setCard(card);
            new DataPropagationHelper(serverAdapter, dataBaseAdapter).updateEntity(new CardPropagationDataProvider(null, board, stack) ,fullCard, new IResponseCallback<FullCard>(account) {
                @Override
                public void onResponse(FullCard response) {
                    liveData.postValue(response);
                }
            });
        });
        return liveData;


        //TODO: Tell the server
//        doAsync(()->{
//            Card existingCard = dataBaseAdapter.getCardByRemoteIdDirectly(card.getAccountId(), card.getId());
//            if (!existingCard.equals(card)){
//                dataBaseAdapter.updateCard(card, true);
//            }
//        });
    }

    public long createLabel(long accountId, Label label) {
        //TODO: Tell the server
        return dataBaseAdapter.createLabel(accountId, label);
    }

    public MutableLiveData<Label> createAndAssignLabelToCard(long accountId, Label label, long localCardId) {
        MutableLiveData<Label> liveData = new MutableLiveData<>();
        doAsync(() -> {
            Account account = dataBaseAdapter.getAccountByIdDirectly(accountId);
            Board board = dataBaseAdapter.getBoardByLocalCardIdDirectly(localCardId);
            label.setAccountId(accountId);
            new DataPropagationHelper(serverAdapter, dataBaseAdapter).createEntity(new LabelDataProvider(null, board, null) ,label, new IResponseCallback<Label>(account) {
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

    public void deleteLabel(Label label) {
        //TODO: Tell the server
        dataBaseAdapter.deleteLabel(label, true);
    }

    public void updateLabel(Label label) {
        //TODO: Tell the server
        dataBaseAdapter.updateLabel(label, true);
    }

    public void assignLabelToBoard(long localLabelId, long localBoardId) {
        //TODO: remove this method? also entities and daos!
        dataBaseAdapter.createJoinBoardWithLabel(localBoardId, localLabelId);
    }

    public void assignUserToCard(User user, Card card) {
        doAsync(() -> {
            final long localUserId = user.getLocalId();
            final long localCardId = card.getLocalId();
            dataBaseAdapter.createJoinCardWithUser(localUserId, localCardId, DBStatus.LOCAL_EDITED);
            Stack stack = dataBaseAdapter.getStackByLocalIdDirectly(card.getStackId());
            Board board = dataBaseAdapter.getBoardByLocalIdDirectly(stack.getBoardId());
            Account account = dataBaseAdapter.getAccountByIdDirectly(card.getAccountId());
            serverAdapter.assignUserToCard(board.getId(), stack.getId(), card.getId(), user.getUid(), new IResponseCallback<Void>(account){

                @Override
                public void onResponse(Void response) {
                    dataBaseAdapter.setStatusForJoinCardWithUser(localCardId, localUserId, DBStatus.UP_TO_DATE.getId());
                }
            });
        });
    }

    public void assignLabelToCard(Label label, Card card) {
        doAsync(() -> {
            final long localLabelId = label.getLocalId();
            final long localCardId = card.getLocalId();
            dataBaseAdapter.createJoinCardWithLabel(localLabelId, localCardId, DBStatus.LOCAL_EDITED);
            Stack stack = dataBaseAdapter.getStackByLocalIdDirectly(card.getStackId());
            Board board = dataBaseAdapter.getBoardByLocalIdDirectly(stack.getBoardId());
            Account account = dataBaseAdapter.getAccountByIdDirectly(card.getAccountId());
            serverAdapter.assignLabelToCard(board.getId(), stack.getId(), card.getId(), label.getId(), new IResponseCallback<Void>(account){

                @Override
                public void onResponse(Void response) {
                    dataBaseAdapter.setStatusForJoinCardWithLabel(localCardId, localLabelId, DBStatus.UP_TO_DATE.getId());
                }
            });
        });
    }

    public void unassignLabelFromCard(Label label, Card card) {
        doAsync(() -> {
            dataBaseAdapter.deleteJoinedLabelForCard(card.getLocalId(), label.getLocalId());
            Stack stack = dataBaseAdapter.getStackByLocalIdDirectly(card.getStackId());
            Board board = dataBaseAdapter.getBoardByLocalIdDirectly(stack.getBoardId());
            Account account = dataBaseAdapter.getAccountByIdDirectly(card.getAccountId());
            serverAdapter.unassignLabelFromCard(board.getId(), stack.getId(), card.getId(), label.getId(), new IResponseCallback<Void>(account){

                @Override
                public void onResponse(Void response) {
                    dataBaseAdapter.deleteJoinedLabelForCardPhysically(card.getLocalId(), label.getLocalId());
                }
            });
        });
    }

    public void unassignUserFromCard(User user, Card card) {
        doAsync(() -> {
            dataBaseAdapter.deleteJoinedUserForCard(card.getLocalId(), user.getLocalId());
            Stack stack = dataBaseAdapter.getStackByLocalIdDirectly(card.getStackId());
            Board board = dataBaseAdapter.getBoardByLocalIdDirectly(stack.getBoardId());
            Account account = dataBaseAdapter.getAccountByIdDirectly(card.getAccountId());
            serverAdapter.unassignUserFromCard(board.getId(), stack.getId(), card.getId(), user.getUid(), new IResponseCallback<Void>(account){

                @Override
                public void onResponse(Void response) {
                    dataBaseAdapter.deleteJoinedUserForCardPhysically(card.getLocalId(), user.getLocalId());
                }
            });
        });
    }

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

    public LiveData<List<User>> searchUserByUidOrDisplayName(final long accountId, final String searchTerm){
        return dataBaseAdapter.searchUserByUidOrDisplayName(accountId, searchTerm);
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

    /**
     * deprecated! should be removed, as soon as the board-ID can be set by the frontend.
     *  see searchLabelByTitle with board id.
     * @param accountId
     * @param boardId
     * @param searchTerm
     * @return
     */
    public LiveData<List<Label>> searchLabelByTitle(final long accountId, final long boardId, String searchTerm){
        return dataBaseAdapter.searchLabelByTitle(accountId, boardId, searchTerm);
    }

    public String getServerUrl() throws NextcloudFilesAppAccountNotFoundException, NoCurrentAccountSelectedException {
        return serverAdapter.getServerUrl();
    }

    public String getApiPath() {
        return serverAdapter.getApiPath();
    }

    public String getApiUrl() throws NextcloudFilesAppAccountNotFoundException, NoCurrentAccountSelectedException {
        return serverAdapter.getApiUrl();
    }

    public void reorder(Long accountId, FullCard movedCard, long newStackId, int newPosition) {
        // FIXME implement!
        if (serverAdapter.hasInternetConnection()){
            // call reorder
        } else {
            // reorder locally
        }
    }
}
