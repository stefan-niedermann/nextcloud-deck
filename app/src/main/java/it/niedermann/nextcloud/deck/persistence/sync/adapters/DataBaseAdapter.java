package it.niedermann.nextcloud.deck.persistence.sync.adapters;

import android.content.Context;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.BoardDao;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.CardDao;
import it.niedermann.nextcloud.deck.model.DaoSession;
import it.niedermann.nextcloud.deck.model.JoinCardWithLabel;
import it.niedermann.nextcloud.deck.model.JoinCardWithLabelDao;
import it.niedermann.nextcloud.deck.model.JoinCardWithUser;
import it.niedermann.nextcloud.deck.model.JoinCardWithUserDao;
import it.niedermann.nextcloud.deck.model.JoinStackWithCard;
import it.niedermann.nextcloud.deck.model.JoinStackWithCardDao;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.LabelDao;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.model.StackDao;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.UserDao;
import it.niedermann.nextcloud.deck.persistence.DeckDaoSession;

public class DataBaseAdapter implements IDatabaseOnlyAdapter {

    private interface DataAccessor <T> {
        T getData();
    }

    private DaoSession db;
    private Context applicationContext;

    public DataBaseAdapter(Context applicationContext) {
        this.applicationContext = applicationContext;
        this.db = DeckDaoSession.getInstance(applicationContext).session();
        QueryBuilder.LOG_SQL = true; //FIXME: remove this.
    }

    private <T> void respond(IResponseCallback<T> responseCallback, DataAccessor<T> r){
        new Thread(() -> responseCallback.onResponse(r.getData())).start();
    }

    @Override
    public boolean hasAccounts() {
        return db.getAccountDao().count()>0;
    }

    @Override
    public Board getBoard(long accountId, long remoteId) {
        QueryBuilder<Board> qb = db.getBoardDao().queryBuilder();
        return qb.where(BoardDao.Properties.AccountId.eq(accountId), BoardDao.Properties.Id.eq(remoteId)).unique();
    }

    @Override
    public Stack getStack(long accountId, long localBoardId, long remoteId) {
        QueryBuilder<Stack> qb = db.getStackDao().queryBuilder();
        return qb.where(StackDao.Properties.AccountId.eq(accountId), StackDao.Properties.BoardId.eq(localBoardId), StackDao.Properties.Id.eq(remoteId)).unique();
    }

    @Override
    public Card getCard(long accountId, long remoteId) {
        QueryBuilder<Card> qb = db.getCardDao().queryBuilder();
        return qb.where(CardDao.Properties.AccountId.eq(accountId), CardDao.Properties.Id.eq(remoteId)).unique();
    }

    @Override
    public User getUser(long accountId, long remoteId) {
        QueryBuilder<User> qb = db.getUserDao().queryBuilder();
        return qb.where(UserDao.Properties.AccountId.eq(accountId), UserDao.Properties.Id.eq(remoteId)).unique();
    }

    @Override
    public void createUser(long accountId, User user) {
        user.setAccountId(accountId);
        db.getUserDao().insert(user);
    }

    @Override
    public void updateUser(long accountId, User user) {
        user.setAccountId(accountId);
        db.getUserDao().update(user);
    }

    @Override
    public Label getLabel(long accountId, long remoteId) {
        QueryBuilder<Label> qb = db.getLabelDao().queryBuilder();
        return qb.where(LabelDao.Properties.AccountId.eq(accountId), LabelDao.Properties.Id.eq(remoteId)).unique();
    }

    @Override
    public void createLabel(long accountId, Label label) {
        label.setAccountId(accountId);
        db.getLabelDao().insert(label);
    }

    @Override
    public void createJoinCardWithLabel(long localLabelId, long localCardId) {
        JoinCardWithLabel join = new JoinCardWithLabel();
        join.setCardId(localCardId);
        join.setLabelId(localLabelId);
        db.getJoinCardWithLabelDao().insert(join);
    }

    @Override
    public void deleteJoinedLabelsForCard(long localCardId) {
        db.getJoinCardWithLabelDao().queryBuilder().where(JoinCardWithLabelDao.Properties.CardId.eq(localCardId)).buildDelete().executeDeleteWithoutDetachingEntities();
    }

    @Override
    public void createJoinCardWithUser(long localUserId, long localCardId) {
        JoinCardWithUser join = new JoinCardWithUser();
        join.setCardId(localCardId);
        join.setUserId(localUserId);
        db.getJoinCardWithUserDao().insert(join);
    }

    @Override
    public void deleteJoinedUsersForCard(long localCardId) {
        db.getJoinCardWithUserDao().queryBuilder().where(JoinCardWithUserDao.Properties.CardId.eq(localCardId)).buildDelete().executeDeleteWithoutDetachingEntities();
    }

    @Override
    public void createJoinStackWithCard(long localCardId, long localStackId) {
        JoinStackWithCard join = new JoinStackWithCard();
        join.setCardId(localCardId);
        join.setStackId(localStackId);
        db.getJoinStackWithCardDao().insert(join);
    }

    @Override
    public void deleteJoinedCardsForStack(long localStackId) {
        db.getJoinStackWithCardDao().queryBuilder().where(JoinStackWithCardDao.Properties.StackId.eq(localStackId)).buildDelete().executeDeleteWithoutDetachingEntities();
    }

    @Override
    public void updateLabel(long accountId, Label label) {
        label.setAccountId(accountId);
        db.getLabelDao().update(label);
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
    public void createBoard(long accountId, Board board) {
        board.setAccountId(accountId);
        db.getBoardDao().insert(board);
    }

    @Override
    public void deleteBoard(Board board) {
        db.getBoardDao().delete(board);

    }

    @Override
    public void updateBoard(Board board) {
        db.getBoardDao().update(board);

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
    public void getStack(long accountId, long localBoardId, long stackId, IResponseCallback<Stack> responseCallback) {
        QueryBuilder<Stack> qb = db.getStackDao().queryBuilder();
        respond(responseCallback, () -> {
            Stack stack = qb.where(
                    StackDao.Properties.AccountId.eq(accountId),
                    StackDao.Properties.BoardId.eq(localBoardId),
                    StackDao.Properties.LocalId.eq(stackId)
            ).unique();
            // eager preload
            for (Card c : stack.getCards()){
                DeckLog.log("labels for card "+c.getTitle()+": "+c.getLabels().size());
                c.getAssignedUsers();
                c.getLabels();
            }
            return stack;
        });
    }

    @Override
    public void createStack(long accountId, Stack stack) {
        stack.setAccountId(accountId);
        db.getStackDao().insert(stack);

    }

    @Override
    public void deleteStack(Stack stack) {
        db.getStackDao().delete(stack);

    }

    @Override
    public void updateStack(Stack stack) {
        db.getStackDao().update(stack);

    }

    @Override
    public void getCard(long accountId, long boardId, long stackId, long cardId, IResponseCallback<Card> responseCallback) {
        QueryBuilder<Card> qb = db.getCardDao().queryBuilder();
        respond(responseCallback, () -> {
                Card card = qb.where(
                        CardDao.Properties.AccountId.eq(accountId),
                        CardDao.Properties.StackId.eq(stackId),
                        CardDao.Properties.LocalId.eq(cardId)
                ).unique();

                //preload eager
                card.getLabels();
                card.getAssignedUsers();
                DeckLog.log(card.getLabels().size()+"");
                return card;
            }
        );
    }

    @Override
    public void createCard(long accountId, Card card) {
        card.setAccountId(accountId);
        db.getCardDao().insert(card);

    }

    @Override
    public void deleteCard(Card card) {
        db.getCardDao().delete(card);
    }

    @Override
    public void updateCard(Card card) {
        db.getCardDao().update(card);

    }


}
