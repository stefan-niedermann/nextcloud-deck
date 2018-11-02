package it.niedermann.nextcloud.deck.persistence.sync;

import android.app.Activity;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.api.RequestHelper;
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
        //TODO: implement.
        // bullshit data...
        List<Stack> stacks = new ArrayList<>();
        Stack s = new Stack();
        s.setBoardId(boardId);
        s.setOrder(1);
        s.setTitle("ToDo");
        stacks.add(s);
        s = new Stack();
        s.setBoardId(boardId);
        s.setOrder(2);
        s.setTitle("Doing");
        stacks.add(s);
        s = new Stack();
        s.setBoardId(boardId);
        s.setOrder(3);
        s.setTitle("Done");
        stacks.add(s);
        responseCallback.onResponse(stacks);
    }

    @Override
    public void getCards(long accountId, long stackId, IResponseCallback<List<Card>> responseCallback) {
        //TODO: implement.
        // bullshit data...
        List<Card> cards = new ArrayList<>();
        Card c = new Card(0, "Fix some naughty bug");
        c.setDescription("getRandom always returns four");
        cards.add(c);
        c = new Card(0, "Strange behaviour");
        c.setDescription("Some retard didn't close the streams");
        cards.add(c);
        c = new Card(0, "Push Pixels around");
        c.setDescription("It is one px too narrow!!! Get it fixed now!");
        cards.add(c);
        responseCallback.onResponse(cards);
    }
}
