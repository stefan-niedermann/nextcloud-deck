package it.niedermann.nextcloud.deck.persistence.sync.adapters.db;

import android.arch.lifecycle.LiveData;
import android.content.Context;

import java.util.List;

import it.niedermann.nextcloud.deck.model.AccessControl;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.JoinBoardWithLabel;
import it.niedermann.nextcloud.deck.model.JoinCardWithLabel;
import it.niedermann.nextcloud.deck.model.JoinCardWithUser;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.model.full.FullStack;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.LiveDataHelper;

public class DataBaseAdapter {

    private DeckDatabase db;

    public DataBaseAdapter(Context applicationContext) {
        this.db = DeckDatabase.getInstance(applicationContext);
    }

    public LiveData<Boolean> hasAccounts(){
        return LiveDataHelper.postCustomValue(db.getAccountDao().countAccounts(), data -> data != null && data > 0);
    }

    public LiveData<Board> getBoard(long accountId, long remoteId) {
        return LiveDataHelper.onlyIfChanged(db.getBoardDao().getBoardByRemoteId(accountId, remoteId));
    }
    public Board getBoardByRemoteIdDirectly(long accountId, long remoteId) {
        return db.getBoardDao().getBoardByRemoteIdDirectly(accountId, remoteId);
    }
    public FullBoard getFullBoardByRemoteIdDirectly(long accountId, long remoteId) {
        return db.getBoardDao().getFullBoardByRemoteIdDirectly(accountId, remoteId);
    }

    public LiveData<Stack> getStackByRemoteId(long accountId, long localBoardId, long remoteId) {
        return LiveDataHelper.onlyIfChanged(db.getStackDao().getStackByRemoteId(accountId, localBoardId, remoteId));
    }

    public FullStack getFullStackByRemoteIdDirectly(long accountId, long localBoardId, long remoteId) {
        return db.getStackDao().getFullStackByRemoteIdDirectly(accountId, localBoardId, remoteId);
    }

    public LiveData<Card> getCardByRemoteID(long accountId, long remoteId) {
        return LiveDataHelper.onlyIfChanged(db.getCardDao().getCardByRemoteId(accountId, remoteId));
    }

    public FullCard getFullCardByRemoteIdDirectly(long accountId, long remoteId) {
        FullCard card = db.getCardDao().getFullCardByRemoteIdDirectly(accountId, remoteId);
        readRelationsForCard(card);
        return card;
    }

    private void readRelationsForCard(FullCard card) {
        if (card != null){
            if (card.getLabelIDs() != null && !card.getLabelIDs().isEmpty()){
                card.setLabels(db.getLabelDao().getLabelsByIdDirectly(card.getLabelIDs()));
            }
            if (card.getAssignedUserIDs() != null && !card.getAssignedUserIDs().isEmpty()){
                card.setAssignedUsers(db.getUserDao().getUsersByIdDirectly(card.getAssignedUserIDs()));
            }
        }
    }

    private void readRelationsForCard(List<FullCard> card) {
        if (card == null){
            return;
        }
        for (FullCard c : card) {
            readRelationsForCard(c);
        }
    }

    public Card getCardByRemoteIdDirectly(long accountId, long remoteId) {
        return db.getCardDao().getCardByRemoteIdDirectly(accountId, remoteId);
    }

    public LiveData<List<FullCard>> getFullCardsForStack(long accountId, long localStackId) {
        return LiveDataHelper.interceptLiveData(db.getCardDao().getFullCardsForStack(accountId, localStackId), this::readRelationsForCard);
    }

    public User getUserByUidDirectly(long accountId, String uid) {
        return db.getUserDao().getUserByUidDirectly(accountId, uid);
    }

    public long createUser(long accountId, User user) {
        user.setAccountId(accountId);
        return db.getUserDao().insert(user);
    }

    public void updateUser(long accountId, User user) {
        user.setAccountId(accountId);
        db.getUserDao().update(user);
    }

    public LiveData<Label> getLabelByRemoteId(long accountId, long remoteId) {
        return LiveDataHelper.onlyIfChanged(db.getLabelDao().getLabelByRemoteId(accountId, remoteId));
    }

    public Label getLabelByRemoteIdDirectly(long accountId, long remoteId) {
        return db.getLabelDao().getLabelByRemoteIdDirectly(accountId, remoteId);
    }

    public long createLabel(long accountId, Label label) {
        label.setAccountId(accountId);
        return db.getLabelDao().insert(label);
    }

    public void createJoinCardWithLabel(long localLabelId, long localCardId) {
        JoinCardWithLabel join = new JoinCardWithLabel();
        join.setCardId(localCardId);
        join.setLabelId(localLabelId);
        db.getJoinCardWithLabelDao().insert(join);
    }

    public void deleteJoinedLabelsForCard(long localCardId) {
        db.getJoinCardWithLabelDao().deleteByCardId(localCardId);
    }

    public void createJoinCardWithUser(long localUserId, long localCardId) {
        JoinCardWithUser join = new JoinCardWithUser();
        join.setCardId(localCardId);
        join.setUserId(localUserId);
        db.getJoinCardWithUserDao().insert(join);
    }

    public void deleteJoinedUsersForCard(long localCardId) {
        db.getJoinCardWithUserDao().deleteByCardId(localCardId);
    }

    public void createJoinBoardWithLabel(long localBoardId, long localLabelId) {
        JoinBoardWithLabel join = new JoinBoardWithLabel();
        join.setBoardId(localBoardId);
        join.setLabelId(localLabelId);
        db.getJoinBoardWithLabelDao().insert(join);
    }

    public void deleteJoinedLabelsForBoard(Long localBoardId) {
        db.getJoinBoardWithLabelDao().deleteByBoardId(localBoardId);
    }
    
    public void updateLabel(Label label) {
        db.getLabelDao().update(label);
    }

    public void deleteLabel(Label label) {
        db.getLabelDao().delete(label);
    }

    public LiveData<Account> createAccount(String accoutName) {
        return LiveDataHelper.wrapInLiveData(() -> {
            Account acc = new Account();
            acc.setName(accoutName);
            long id = db.getAccountDao().insert(acc);
            return readAccountDirectly(id);

        });
    }

    public void deleteAccount(long id) {
        db.getAccountDao().deleteById(id);
    }

    public void updateAccount(Account account) {
        db.getAccountDao().update(account);
    }

    public LiveData<Account> readAccount(long id) {
        return LiveDataHelper.onlyIfChanged(db.getAccountDao().selectById(id));
    }

    public Account readAccountDirectly(long id) {
        return db.getAccountDao().selectByIdDirectly(id);
    }

    public LiveData<List<Account>> readAccounts() {
        return LiveDataHelper.onlyIfChanged(db.getAccountDao().selectAll());
    }

    public LiveData<List<Board>> getBoards(long accountId) {
        return LiveDataHelper.onlyIfChanged(db.getBoardDao().getBoardsForAccount(accountId));
    }

    public LiveData<Board> createBoard(long accountId, Board board) {
        return LiveDataHelper.wrapInLiveData(() -> {
            board.setAccountId(accountId);
            long id = db.getBoardDao().insert(board);
            return db.getBoardDao().getBoardByIdDirectly(accountId, id);

        });
    }

    public void createBoardDirectly(long accountId, Board board) {
            board.setAccountId(accountId);
            db.getBoardDao().insert(board);
    }

    public void deleteBoard(Board board) {
        db.getBoardDao().delete(board);
    }

    public void updateBoard(Board board) {
        db.getBoardDao().update(board);
    }

    public LiveData<List<FullStack>> getStacks(long accountId, long localBoardId) {
        return LiveDataHelper.onlyIfChanged(db.getStackDao().getFullStacksForBoard(accountId, localBoardId));
    }

    public LiveData<FullStack> getStack(long accountId, long localStackId) {
        return LiveDataHelper.onlyIfChanged(db.getStackDao().getFullStack(accountId, localStackId));
    }
    
    public long createStack(long accountId, Stack stack) {
        stack.setAccountId(accountId);
        return db.getStackDao().insert(stack);
    }
    
    public void deleteStack(Stack stack) {
        db.getStackDao().delete(stack);
    }
    
    public void updateStack(Stack stack) {
        db.getStackDao().update(stack);
    }
    
    public LiveData<FullCard>  getCardByLocalId(long accountId, long localCardId) {
        return LiveDataHelper.interceptLiveData(db.getCardDao().getFullCardByLocalId(accountId, localCardId), this::readRelationsForCard);
    }
    
    public long createCard(long accountId, Card card) {
        card.setAccountId(accountId);
        return db.getCardDao().insert(card);
    }
    
    public void deleteCard(Card card) {
        db.getCardDao().delete(card);
    }
    
    public void updateCard(Card card) {
        db.getCardDao().update(card);
    }

    public void createAccessControl(long accountId, AccessControl entity) {
        entity.setAccountId(accountId);
        db.getAccessControlDao().insert(entity);
    }

    public AccessControl getAccessControlByRemoteIdDirectly(long accountId, Long id) {
        return db.getAccessControlDao().getAccessControlByRemoteIdDirectly(accountId, id);
    }

    public void updateAccessControl(AccessControl entity) {
        db.getAccessControlDao().update(entity);
    }

    public LiveData<FullBoard> getFullBoardById(Long accountId, Long localId) {
        return db.getBoardDao().getFullBoardById(accountId, localId);
    }

    public LiveData<User> getUserByUid(long accountId, long localId){
        return db.getUserDao().getUserByLocalId(accountId, localId);
    }

    public LiveData<User> getUserByUid(long accountId, String uid){
        return db.getUserDao().getUserByUid(accountId, uid);
    }
}
