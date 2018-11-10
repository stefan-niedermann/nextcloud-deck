package it.niedermann.nextcloud.deck.persistence.sync;

import android.app.Activity;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

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

    private IDataBasePersistenceAdapter dataBaseAdapter;
    private IPersistenceAdapter serverAdapter;
    private Context applicationContext;
    private Activity sourceActivity;

    public SyncManager(Context applicationContext, Activity sourceActivity){
        this.applicationContext = applicationContext;
        this.sourceActivity = sourceActivity;
        dataBaseAdapter = new DataBaseAdapter(applicationContext);
        this.serverAdapter =  new ServerAdapter(applicationContext, sourceActivity);
    }

    public void synchronize(){

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
        // TODO: first look at DB instead of direct server request
        serverAdapter.getBoards(accountId, responseCallback);
    }

    @Override
    public void getStacks(long accountId, long boardId, IResponseCallback<List<Stack>> responseCallback) {
        // TODO: first look at DB instead of direct server request
         serverAdapter.getStacks(accountId,boardId,responseCallback);
    }

    @Override
    public void getStack(long accountId, long boardId, long stackId, IResponseCallback<Stack> responseCallback) {
        // TODO: first look at DB instead of direct server request
        serverAdapter.getStack(accountId,boardId, stackId, responseCallback);
    }

    @Override
    public void getCard(long accountId, long boardId, long stackId, long cardId, IResponseCallback<Card> responseCallback) {
        // TODO: first look at DB instead of direct server request
        serverAdapter.getCard(accountId, boardId, stackId, cardId, responseCallback);
    }
}
