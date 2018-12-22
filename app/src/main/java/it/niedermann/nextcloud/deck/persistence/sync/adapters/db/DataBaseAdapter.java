package it.niedermann.nextcloud.deck.persistence.sync.adapters.db;

import android.arch.lifecycle.LiveData;
import android.content.Context;

import java.util.List;

import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.JoinCardWithLabel;
import it.niedermann.nextcloud.deck.model.JoinCardWithUser;
import it.niedermann.nextcloud.deck.model.JoinStackWithCard;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.full.FullCard;

public class DataBaseAdapter implements IDatabaseOnlyAdapter {


    private interface DataAccessor<T> {
        T getData();
    }

    private DeckDatabase db;

    private Context applicationContext;

    public DataBaseAdapter(Context applicationContext) {
        this.applicationContext = applicationContext;
        this.db = DeckDatabase.getInstance(applicationContext);
    }

    private <T> void respond(IResponseCallback<T> responseCallback, DataAccessor<T> r) {
        new Thread(() -> responseCallback.onResponse(r.getData())).start();
    }

    @Override
    public boolean hasAccounts() {
        return db.getAccountDao().countAccounts() > 0;
    }

    @Override
    public LiveData<Board> getBoard(long accountId, long remoteId) {
        return db.getBoardDao().getBoardByRemoteId(accountId, remoteId);
    }

    @Override
    public LiveData<Stack> getStack(long accountId, long localBoardId, long remoteId) {
        return db.getStackDao().getStackByRemoteId(accountId, localBoardId, remoteId);
    }

    @Override
    public LiveData<Card> getCard(long accountId, long remoteId) {
        return db.getCardDao().getCardByRemoteId(accountId, remoteId);
    }

    @Override
    public LiveData<User> getUser(long accountId, long remoteId) {
        return db.getUserDao().getUsersByRemoteId(accountId, remoteId);
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
        return db.getLabelDao().getLabelByRemoteId(accountId, remoteId);
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
        db.getJoinCardWithLabelDao().deleteByCardId(localCardId);
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
        db.getJoinCardWithUserDao().deleteByCardId(localCardId);
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
        db.getJoinStackWithCardDao().deleteByStackId(localStackId);
    }

    @Override
    public void updateLabel(long accountId, Label label) {
        label.setAccountId(accountId);
        db.getLabelDao().update(label);
    }

    @Override
    public LiveData<Account> createAccount(String accoutName) {
        Account acc = new Account();
        acc.setName(accoutName);
        long id = db.getAccountDao().insert(acc);
        return readAccount(id);
    }

    @Override
    public void deleteAccount(long id) {
        db.getAccountDao().deleteById(id);
    }

    @Override
    public void updateAccount(Account account) {
        db.getAccountDao().update(account);
    }

    @Override
    public LiveData<Account> readAccount(long id) {
        return db.getAccountDao().selectById(id);
    }

    @Override
    public LiveData<List<Account>> readAccounts() {
        return db.getAccountDao().selectAll();
    }

    @Override
    public void getBoards(long accountId, IResponseCallback<LiveData<List<Board>>> responseCallback) {
        respond(responseCallback, () -> db.getBoardDao().getBoardsForAccount(accountId));
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
    public void getStacks(long accountId, long localBoardId, IResponseCallback<LiveData<List<Stack>>> responseCallback) {
        respond(responseCallback, () -> db.getStackDao().getStacksForBoard(accountId, localBoardId));
    }

    @Override
    public void getStack(long accountId, long localBoardId, long stackId, IResponseCallback<LiveData<Stack>> responseCallback) {
//        QueryBuilder<Stack> qb = db.getStackDao().queryBuilder();
//        respond(responseCallback, () -> {
//            Stack stack = qb.where(
//                    StackDao.Properties.AccountId.eq(accountId),
//                    StackDao.Properties.BoardId.eq(localBoardId),
//                    StackDao.Properties.LocalId.eq(stackId)
//            ).unique();
//            // eager preload
//            for (Card c : stack.getCards()) {
//                DeckLog.log("labels for card " + c.getTitle() + ": " + c.getLabels().size());
//                c.getAssignedUsers();
//                c.getLabels();
//            }
//            return stack;
//        });
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
    public void getCard(long accountId, long boardId, long stackId, long cardId, IResponseCallback<LiveData<FullCard>> responseCallback) {
//        QueryBuilder<Card> qb = db.getCardDao().queryBuilder();
//        respond(responseCallback, () -> {
//                    Card card = qb.where(
//                            CardDao.Properties.AccountId.eq(accountId),
//                            CardDao.Properties.StackId.eq(stackId),
//                            CardDao.Properties.LocalId.eq(cardId)
//                    ).unique();
//
//                    //preload eager
//                    card.getLabels();
//                    card.getAssignedUsers();
//                    DeckLog.log(card.getLabels().size() + "");
//                    return card;
//                }
//        );
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
