package it.niedermann.nextcloud.deck.persistence.sync.adapters;

import android.content.Context;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.BoardDao;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.CardDao;
import it.niedermann.nextcloud.deck.model.DaoSession;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.model.StackDao;
import it.niedermann.nextcloud.deck.persistence.DeckDaoSession;

public class DataBaseAdapter implements IDataBasePersistenceAdapter {

    private interface DataAccessor <T> {
        T getData();
    }

    private DaoSession db;
    private Context applicationContext;

    public DataBaseAdapter(Context applicationContext) {
        this.applicationContext = applicationContext;
        this.db = DeckDaoSession.getInstance(applicationContext).session();
    }

    private <T> void respond(IResponseCallback<T> responseCallback, DataAccessor<T> r){
        new Thread(() -> responseCallback.onResponse(r.getData())).start();
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
        QueryBuilder<Board> qb = db.getBoardDao().queryBuilder();
        respond(responseCallback, () -> qb.where(
                BoardDao.Properties.AccountId.eq(accountId)
        ).list());
    }

    @Override
    public void getStacks(long accountId, long boardId, IResponseCallback<List<Stack>> responseCallback) {
        QueryBuilder<Stack> qb = db.getStackDao().queryBuilder();
        respond(responseCallback, () -> qb.where(
                StackDao.Properties.AccountId.eq(accountId),
                StackDao.Properties.BoardId.eq(boardId)
        ).list());
    }

    @Override
    public void getStack(long accountId, long boardId, long stackId, IResponseCallback<Stack> responseCallback) {
        QueryBuilder<Stack> qb = db.getStackDao().queryBuilder();
        respond(responseCallback, () -> qb.where(
                StackDao.Properties.AccountId.eq(accountId),
                StackDao.Properties.BoardId.eq(boardId),
                StackDao.Properties.LocalId.eq(stackId)
        ).unique());
    }

    @Override
    public void getCard(long accountId, long boardId, long stackId, long cardId, IResponseCallback<Card> responseCallback) {
        QueryBuilder<Card> qb = db.getCardDao().queryBuilder();
        respond(responseCallback, () -> qb.where(
                CardDao.Properties.AccountId.eq(accountId),
                CardDao.Properties.StackId.eq(stackId),
                CardDao.Properties.LocalId.eq(cardId)
        ).unique());
    }


}
