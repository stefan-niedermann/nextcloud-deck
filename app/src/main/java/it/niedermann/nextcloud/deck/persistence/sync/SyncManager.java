package it.niedermann.nextcloud.deck.persistence.sync;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;
import java.util.List;

import it.niedermann.nextcloud.deck.DeckConsts;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.model.full.FullStack;
import it.niedermann.nextcloud.deck.model.interfaces.AbstractRemoteEntity;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.ServerAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DataBaseAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.helpers.SyncHelper;
import it.niedermann.nextcloud.deck.persistence.sync.helpers.providers.BoardDataProvider;

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
        final long accountId = responseCallback.getAccount().getId();
        doAsync(() -> {
            SharedPreferences lastSyncPref = applicationContext.getSharedPreferences(
                    applicationContext.getString(R.string.shared_preference_last_sync), Context.MODE_PRIVATE);
            long lastSync = lastSyncPref.getLong(DeckConsts.LAST_SYNC_KEY, 0L);
            Date lastSyncDate = new Date(lastSync);
            Date now = new Date();

            new SyncHelper(serverAdapter, dataBaseAdapter, new IResponseCallback<Boolean>(responseCallback.getAccount()) {
                @Override
                public void onResponse(Boolean response) {
                    //TODO activate when done dev
//                lastSyncPref.edit().putLong(LAST_SYNC_KEY, now.getTime()).apply();
                    responseCallback.onResponse(response);
                }

                @Override
                public void onError(Throwable throwable) {
                    responseCallback.onError(throwable);
                }
            }).doSyncFor(new BoardDataProvider());
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

    private <T extends AbstractRemoteEntity> T applyUpdatesFromRemote(T localEntity, T remoteEntity, Long accountId) {
        if (!localEntity.getId().equals(remoteEntity.getId())
                || !accountId.equals(localEntity.getAccountId())) {
            throw new IllegalArgumentException("IDs of Account or Entity are not matching! WTF are you doin?!");
        }
        remoteEntity.setLastModifiedLocal(remoteEntity.getLastModified()); // not an error! local-modification = remote-mod
        remoteEntity.setLocalId(localEntity.getLocalId());
        return remoteEntity;
    }

    public LiveData<Boolean> hasAccounts() {
        return dataBaseAdapter.hasAccounts();
    }

    public LiveData<Account> createAccount(String accoutName) {
        return dataBaseAdapter.createAccount(accoutName);
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

    public LiveData<Board> createBoard(long accountId, Board board) {
        //TODO how to tell server?
        doAsync(() -> {
            serverAdapter.createBoard(board);
        });
        return dataBaseAdapter.createBoard(accountId, board);
    }

    public void deleteBoard(Board board) {
        //TODO: Tell the server
        dataBaseAdapter.deleteBoard(board);
    }

    public void updateBoard(Board board) {
        //TODO: Tell the server
        dataBaseAdapter.updateBoard(board);
    }

    public LiveData<List<FullStack>> getStacksForBoard(long accountId, long localBoardId) {
        return dataBaseAdapter.getStacks(accountId, localBoardId);
    }

    public LiveData<FullStack> getStack(long accountId, long localStackId) {
        return dataBaseAdapter.getStack(accountId, localStackId);
    }

    public long createStack(long accountId, Stack stack) {
        //TODO: Tell the server
        return dataBaseAdapter.createStack(accountId, stack);
    }

    public void deleteStack(Stack stack) {
        //TODO: Tell the server
        dataBaseAdapter.deleteStack(stack);
    }

    public void updateStack(Stack stack) {
        //TODO: Tell the server
        dataBaseAdapter.updateStack(stack);

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
        dataBaseAdapter.deleteCard(card);
    }

    public void updateCard(Card card) {
        //TODO: Tell the server
        dataBaseAdapter.updateCard(card);
    }

    public long createLabel(long accountId, Label label) {
        //TODO: Tell the server
        return dataBaseAdapter.createLabel(accountId, label);
    }

    public void deleteLabel(Label label) {
        //TODO: Tell the server
        dataBaseAdapter.deleteLabel(label);
    }

    public void updateLabel(Label label) {
        //TODO: Tell the server
        dataBaseAdapter.updateLabel(label);
    }

    public void assignLabelToBoard(long localLabelId, long localBoardId) {
        //TODO: Tell the server
        dataBaseAdapter.createJoinBoardWithLabel(localBoardId, localLabelId);
    }

    public void assignLabelToCard(long localLabelId, long localCardId) {
        //TODO: Tell the server
        dataBaseAdapter.createJoinCardWithLabel(localLabelId, localCardId);
    }
}
