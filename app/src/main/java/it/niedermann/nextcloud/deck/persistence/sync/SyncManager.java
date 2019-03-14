package it.niedermann.nextcloud.deck.persistence.sync;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;
import com.nextcloud.android.sso.exceptions.NoCurrentAccountSelectedException;

import java.util.Date;
import java.util.List;

import androidx.lifecycle.LiveData;
import it.niedermann.nextcloud.deck.DeckConsts;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
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
import it.niedermann.nextcloud.deck.persistence.sync.adapters.ServerAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DataBaseAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.WrappedLiveData;
import it.niedermann.nextcloud.deck.persistence.sync.helpers.DataPropagationHelper;
import it.niedermann.nextcloud.deck.persistence.sync.helpers.SyncHelper;
import it.niedermann.nextcloud.deck.persistence.sync.helpers.providers.BoardDataProvider;
import it.niedermann.nextcloud.deck.persistence.sync.util.DateUtil;

public class SyncManager {


    private DataBaseAdapter dataBaseAdapter;
    private ServerAdapter serverAdapter;
    private Context applicationContext;
    private Activity sourceActivity;

    public SyncManager(Context applicationContext, Activity sourceActivity) {
        this.applicationContext = applicationContext.getApplicationContext();
        this.sourceActivity = sourceActivity;
        dataBaseAdapter = new DataBaseAdapter(this.applicationContext);
        this.serverAdapter = new ServerAdapter(this.applicationContext, sourceActivity);
    }

    private void doAsync(Runnable r) {
        new Thread(r).start();
    }

    public void synchronize(IResponseCallback<Boolean> responseCallback) {
        doAsync(() -> {
            SharedPreferences lastSyncPref = applicationContext.getSharedPreferences(
                    applicationContext.getString(R.string.shared_preference_last_sync), Context.MODE_PRIVATE);
            long lastSync = lastSyncPref.getLong(DeckConsts.LAST_SYNC_KEY, 0L);
            Date lastSyncDate = new Date(lastSync);
            Date now = DateUtil.nowInGMT();

            BoardDataProvider boardDataProvider = new BoardDataProvider();
            final SyncHelper syncHelper = new SyncHelper(serverAdapter, dataBaseAdapter, lastSyncDate);

            IResponseCallback<Boolean> callback = new IResponseCallback<Boolean>(responseCallback.getAccount()) {
                @Override
                public void onResponse(Boolean response) {
                    syncHelper.setResponseCallback(new IResponseCallback<Boolean>(account) {
                        @Override
                        public void onResponse(Boolean response) {
                            // TODO activate when done dev
                            lastSyncPref.edit().putLong(DeckConsts.LAST_SYNC_KEY, now.getTime()).apply();
                            responseCallback.onResponse(response);
                        }
                        @Override
                        public void onError(Throwable throwable) {
                            super.onError(throwable);
                            responseCallback.onError(throwable);
                        }
                    });
                    syncHelper.doUpSyncFor(boardDataProvider);
                }

                @Override
                public void onError(Throwable throwable) {
                    super.onError(throwable);
                    responseCallback.onError(throwable);
                }
            };

            syncHelper.setResponseCallback(callback);

            syncHelper.doSyncFor(boardDataProvider);
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
        dataBaseAdapter.deleteAccount(id);
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

    public LiveData<List<Board>> getBoards(long accountId) {
        return dataBaseAdapter.getBoards(accountId);
    }

    public void createBoard(long accountId, Board board) {
        FullBoard fullBoard = new FullBoard();
        fullBoard.setBoard(board);
        Account dummyAccount = new Account(accountId);
        doAsync(() ->
            new DataPropagationHelper(serverAdapter, dataBaseAdapter).createEntity(new BoardDataProvider() ,fullBoard, new IResponseCallback<FullBoard>(dummyAccount) {
                @Override
                public void onResponse(FullBoard response) {
                    DeckLog.log(response.toString());
                }

                @Override
                public void onError(Throwable throwable) {
                    DeckLog.logError(throwable);
                }
            })
        );
//        return dataBaseAdapter.createBoard(accountId, board);
    }

    public void deleteBoard(Board board) {
        //TODO: Tell the server
        dataBaseAdapter.deleteBoard(board, true);
    }

    public void updateBoard(Board board) {
        //TODO: Tell the server
        dataBaseAdapter.updateBoard(board, true);
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

    public long createStack(long accountId, Stack stack) {
        //TODO: Tell the server
        return dataBaseAdapter.createStack(accountId, stack);
    }

    public void deleteStack(Stack stack) {
        //TODO: Tell the server
        dataBaseAdapter.deleteStack(stack, true);
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

    public long createCard(long accountId, Card card) {
        //TODO: Tell the server
        return dataBaseAdapter.createCard(accountId, card);
    }

    public void deleteCard(Card card) {
        //TODO: Tell the server
        dataBaseAdapter.deleteCard(card, true);
    }

    public void updateCard(Card card) {

        //TODO: Tell the server
        doAsync(()->{
            dataBaseAdapter.updateCard(card, true);
        });
    }

    public long createLabel(long accountId, Label label) {
        //TODO: Tell the server
        return dataBaseAdapter.createLabel(accountId, label);
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
        //TODO: Tell the server
        dataBaseAdapter.createJoinBoardWithLabel(localBoardId, localLabelId);
    }

    public void assignUserToCard(long localUserId, Card card) {
        doAsync(() -> {
            dataBaseAdapter.createJoinCardWithUser(localUserId, card.getLocalId(), DBStatus.LOCAL_EDITED);
            //TODO: reactivate, as soon as SSO supports Retrofit @Field annotations
//            Stack stack = dataBaseAdapter.getStackByLocalIdDirectly(card.getStackId());
//            Board board = dataBaseAdapter.getBoardByLocalIdDirectly(stack.getBoardId());
//            Account account = dataBaseAdapter.getAccountByIdDirectly(card.getAccountId());
//            User user = dataBaseAdapter.getUserByLocalIdDirectly(localUserId);
//            serverAdapter.assignUserToCard(board.getId(), stack.getId(), card.getId(), user.getUid(), new IResponseCallback<FullCard>(account){
//
//                @Override
//                public void onResponse(FullCard response) {
//                    dataBaseAdapter.setStatusForJoinCardWithUser(card.getLocalId(), user.getLocalId(), DBStatus.UP_TO_DATE.getId());
//                }
//            });
        });
    }

    public void assignLabelToCard(long localLabelId, long localCardId) {
        //TODO: Tell the server
        dataBaseAdapter.createJoinCardWithLabel(localLabelId, localCardId);
    }

    public void unassignLabelToCard(Label label, Card card) {
        doAsync(() -> {
            dataBaseAdapter.deleteJoinedLabelForCard(card.getLocalId(), label.getLocalId());
            //TODO: reactivate, as soon as SSO supports Retrofit @Field annotations
            //TODO: this is copied! change it to fit the needs
//            Stack stack = dataBaseAdapter.getStackByLocalIdDirectly(card.getStackId());
//            Board board = dataBaseAdapter.getBoardByLocalIdDirectly(stack.getBoardId());
//            Account account = dataBaseAdapter.getAccountByIdDirectly(card.getAccountId());
//            User user = dataBaseAdapter.getUserByLocalIdDirectly(localUserId);
//            serverAdapter.assignUserToCard(board.getId(), stack.getId(), card.getId(), user.getUid(), new IResponseCallback<FullCard>(account){
//
//                @Override
//                public void onResponse(FullCard response) {
//                    dataBaseAdapter.setStatusForJoinCardWithUser(card.getLocalId(), user.getLocalId(), DBStatus.UP_TO_DATE.getId());
//                }
//            });
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

    public LiveData<List<FullStack>> getStacks(long accountId, long localBoardId) {
        return dataBaseAdapter.getStacks(accountId, localBoardId);
    }


    public LiveData<List<Label>> searchLabelByTitle(final long accountId, String searchTerm){
        return dataBaseAdapter.searchLabelByTitle(accountId, searchTerm);
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

}
