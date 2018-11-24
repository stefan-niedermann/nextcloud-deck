package it.niedermann.nextcloud.deck.persistence.sync.adapters;

import android.content.Context;

import java.util.List;

import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.DaoSession;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.persistence.DeckDaoSession;

public class DataBaseAdapter implements IDataBasePersistenceAdapter {

    private DaoSession db;
    private Context applicationContext;

    public DataBaseAdapter(Context applicationContext) {
        this.applicationContext = applicationContext;
        this.db = DeckDaoSession.getInstance(applicationContext).session();
    }

    @Override
    public boolean hasAccounts() {
        return db.getAccountDao().count()>0;
    }

    @Override
    public Account createAccount(String accoutName) {
        Account acc = new Account();
        acc.setName(accoutName);
        long id = db.getAccountDao().insert(acc);
        return db.getAccountDao().load(id);
    }

    @Override
    public void deleteAccount(long id) {
        db.getAccountDao().deleteByKey(id);
    }

    @Override
    public void updateAccount(Account account) {
        db.update(account);
    }

    @Override
    public Account readAccount(long id) {
        return db.getAccountDao().load(id);
    }

    @Override
    public List<Account> readAccounts() {
        return db.getAccountDao().loadAll();
    }

    @Override
    public void getBoards(long accountId, IResponseCallback<List<Board>> responseCallback) {

    }

    @Override
    public void getStacks(long accountId, long boardId, IResponseCallback<List<Stack>> responseCallback) {

    }

    @Override
    public void getStack(long accountId, long boardId, long stackId, IResponseCallback<Stack> responseCallback) {

    }

    @Override
    public void getCard(long accountId, long boardId, long stackId, long cardId, IResponseCallback<Card> responseCallback) {

    }
}
