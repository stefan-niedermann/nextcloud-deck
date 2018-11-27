package it.niedermann.nextcloud.deck.persistence.sync;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.DataBaseAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.IDataBasePersistenceAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.IPersistenceAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.ServerAdapter;

public class SyncManager implements IDataBasePersistenceAdapter{

    private static final String LAST_SYNC_KEY = "lastSync";

    private IDataBasePersistenceAdapter dataBaseAdapter;
    private IPersistenceAdapter serverAdapter;
    private Context applicationContext;
    private Activity sourceActivity;

    public SyncManager(Context applicationContext, Activity sourceActivity){
        this.applicationContext = applicationContext.getApplicationContext();
        this.sourceActivity = sourceActivity;
        dataBaseAdapter = new DataBaseAdapter(this.applicationContext);
        this.serverAdapter =  new ServerAdapter(this.applicationContext, sourceActivity);
    }

    private void doAsync(Runnable r){
        new Thread(r).start();
    }

    public void synchronize(IResponseCallback<Boolean> responseCallback){
        doAsync(() -> {
                SharedPreferences lastSyncPref = applicationContext.getSharedPreferences(
                        applicationContext.getString(R.string.shared_preference_last_sync), Context.MODE_PRIVATE);
                long lastSync = lastSyncPref.getLong(LAST_SYNC_KEY, 0L);
                Date lastSyncDate = new Date(lastSync);
                Date now = new Date();

                //TODO do the magic!

                lastSyncPref.edit().putLong(LAST_SYNC_KEY, now.getTime()).apply();
        });
    }

    public boolean hasAccounts() {
        return dataBaseAdapter.hasAccounts();
    }

    @Override
    public Account createAccount(String accoutName) {
        return dataBaseAdapter.createAccount(accoutName);
    }

    @Override
    public void deleteAccount(long id) {
        dataBaseAdapter.deleteAccount(id);
    }

    @Override
    public void updateAccount(Account account) {
        dataBaseAdapter.updateAccount(account);
    }

    @Override
    public Account readAccount(long id) {
        return dataBaseAdapter.readAccount(id);
    }

    @Override
    public List<Account> readAccounts() {
        return dataBaseAdapter.readAccounts();
    }

    @Override
    public void getBoards(long accountId, IResponseCallback<List<Board>> responseCallback) {
        dataBaseAdapter.getBoards(accountId, responseCallback);
    }

    @Override
    public void createBoard(long accountId, Board board) {
        doAsync(() -> {
            dataBaseAdapter.createBoard(accountId, board);
            serverAdapter.createBoard(accountId, board);
        });
    }

    @Override
    public void deleteBoard(Board board) {

    }

    @Override
    public void updateBoard(Board board) {

    }

    @Override
    public void getStacks(long accountId, long boardId, IResponseCallback<List<Stack>> responseCallback) {
        dataBaseAdapter.getStacks(accountId,boardId,responseCallback);
    }

    @Override
    public void getStack(long accountId, long boardId, long stackId, IResponseCallback<Stack> responseCallback) {
        dataBaseAdapter.getStack(accountId,boardId, stackId, responseCallback);
    }

    @Override
    public void createStack(long accountId, Stack stack) {
        dataBaseAdapter.createStack(accountId, stack);
        //TODO implement
    }

    @Override
    public void deleteStack(Stack stack) {

    }

    @Override
    public void updateStack(Stack stack) {

    }

    @Override
    public void getCard(long accountId, long boardId, long stackId, long cardId, IResponseCallback<Card> responseCallback) {
        dataBaseAdapter.getCard(accountId, boardId, stackId, cardId, responseCallback);
    }

    @Override
    public void createCard(long accountId, long boardId, long stackId, Card card) {

    }

    @Override
    public void deleteCard(Card card) {

    }

    @Override
    public void updateCard(Card card) {

    }
}
