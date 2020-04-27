package it.niedermann.nextcloud.deck.persistence.sync.adapters.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.sqlite.db.SimpleSQLiteQuery;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.niedermann.nextcloud.deck.model.AccessControl;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.JoinBoardWithLabel;
import it.niedermann.nextcloud.deck.model.JoinCardWithLabel;
import it.niedermann.nextcloud.deck.model.JoinCardWithUser;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.enums.DBStatus;
import it.niedermann.nextcloud.deck.model.enums.EDueType;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.model.full.FullStack;
import it.niedermann.nextcloud.deck.model.interfaces.AbstractRemoteEntity;
import it.niedermann.nextcloud.deck.model.interfaces.IRemoteEntity;
import it.niedermann.nextcloud.deck.model.internal.FilterInformation;
import it.niedermann.nextcloud.deck.model.ocs.Activity;
import it.niedermann.nextcloud.deck.model.ocs.comment.DeckComment;
import it.niedermann.nextcloud.deck.model.ocs.comment.Mention;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.LiveDataHelper;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.WrappedLiveData;

public class DataBaseAdapter {

    private DeckDatabase db;
    @NonNull private Context context;

    public DataBaseAdapter(@NonNull Context applicationContext) {
        this.context = applicationContext;
        this.db = DeckDatabase.getInstance(applicationContext);
    }

    @NotNull
    public Context getContext() {
        return context;
    }

    private <T extends AbstractRemoteEntity> void markAsEditedIfNeeded(T entity, boolean setStatus) {
        if (!setStatus) return;
        entity.setLastModifiedLocal(new Date()); // now.
        entity.setStatusEnum(DBStatus.LOCAL_EDITED);
    }

    private <T extends AbstractRemoteEntity> void markAsDeletedIfNeeded(T entity, boolean setStatus) {
        if (!setStatus) return;
        entity.setStatusEnum(DBStatus.LOCAL_DELETED);
        entity.setLastModifiedLocal(new Date()); // now.
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

    public FullBoard getFullBoardByLocalIdDirectly(long accountId, long localId) {
        return db.getBoardDao().getFullBoardByLocalIdDirectly(accountId, localId);
    }

    public LiveData<Stack> getStackByRemoteId(long accountId, long localBoardId, long remoteId) {
        return LiveDataHelper.onlyIfChanged(db.getStackDao().getStackByRemoteId(accountId, localBoardId, remoteId));
    }

    public Stack getStackByLocalIdDirectly(final long localStackId) {
        return db.getStackDao().getStackByLocalIdDirectly(localStackId);
    }

    public FullStack getFullStackByLocalIdDirectly(final long localStackId) {
        return db.getStackDao().getFullStackByLocalIdDirectly(localStackId);
    }


    public FullStack getFullStackByRemoteIdDirectly(long accountId, long localBoardId, long remoteId) {
        return db.getStackDao().getFullStackByRemoteIdDirectly(accountId, localBoardId, remoteId);
    }

    public LiveData<Card> getCardByRemoteID(long accountId, long remoteId) {
        return LiveDataHelper.onlyIfChanged(db.getCardDao().getCardByRemoteId(accountId, remoteId));
    }

    public FullCard getFullCardByRemoteIdDirectly(long accountId, long remoteId) {
        FullCard card = db.getCardDao().getFullCardByRemoteIdDirectly(accountId, remoteId);
        filterRelationsForCard(card);
        return card;
    }
    public FullCard getFullCardByLocalIdDirectly(long accountId, long localId) {
        return db.getCardDao().getFullCardByLocalIdDirectly(accountId, localId);
    }

    public void filterRelationsForCard(FullCard card) {
        if (card != null){
            if (card.getLabels() != null && !card.getLabels().isEmpty()){
                List<Long> filteredIDs = db.getJoinCardWithLabelDao().filterDeleted(card.getLocalId(), getLocalIDs(card.getLabels()));
                card.setLabels(db.getLabelDao().getLabelsByIdsDirectly(filteredIDs));
            }
            if (card.getAssignedUsers() != null && !card.getAssignedUsers().isEmpty()){
                List<Long> filteredIDs = db.getJoinCardWithUserDao().filterDeleted(card.getLocalId(), getLocalIDs(card.getAssignedUsers()));
                card.setAssignedUsers(db.getUserDao().getUsersByIdsDirectly(filteredIDs));
            }
        }
    }

    private <T> List<Long> getLocalIDs(List<? extends AbstractRemoteEntity> remoteEntityList){
        ArrayList<Long> ids = new ArrayList<>(remoteEntityList.size());
        for (AbstractRemoteEntity entity : remoteEntityList) {
            ids.add(entity.getLocalId());
        }
        return ids;
    }

    public void readRelationsForACL(List<AccessControl> acl) {
        if (acl != null){
            for (AccessControl accessControl : acl) {
                readRelationsForACL(accessControl);
            }
        }
    }
    public void readRelationsForACL(AccessControl acl) {
        if (acl != null){
            if (acl.getUserId() != null){
                acl.setUser(db.getUserDao().getUserByLocalIdDirectly(acl.getUserId()));
            }
        }
    }

    private void filterRelationsForCard(List<FullCard> card) {
        if (card == null){
            return;
        }
        for (FullCard c : card) {
            filterRelationsForCard(c);
        }
    }

    public Card getCardByRemoteIdDirectly(long accountId, long remoteId) {
        return db.getCardDao().getCardByRemoteIdDirectly(accountId, remoteId);
    }

    public LiveData<List<FullCard>> getFullCardsForStack(long accountId, long localStackId, FilterInformation filter) {
        if (filter == null){
            return LiveDataHelper.interceptLiveData(db.getCardDao().getFullCardsForStack(accountId, localStackId), this::filterRelationsForCard);
        }

        List<Object> args = new ArrayList();
        StringBuilder query = new StringBuilder("SELECT * FROM card c " +
                "WHERE accountId = ? AND stackId = ? ");
        args.add(accountId);
        args.add(localStackId);

        if (!filter.getLabels().isEmpty()){
            query.append("and exists(select 1 from joincardwithlabel j where c.localId = cardId and labelId in (");
            fillSqlWithListValues(query, args, filter.getLabels());
            query.append(") and j.status<>3) ");
        }

        if (!filter.getUsers().isEmpty()){
            query.append("and exists(select 1 from joincardwithuser j where c.localId = cardId and userId in (");
            fillSqlWithListValues(query, args, filter.getUsers());
            query.append(") and j.status<>3) ");
        }
        if (filter.getDueType() != EDueType.NO_FILTER){
            switch (filter.getDueType()) {
                case NO_DUE:
                    query.append("and c.dueDate is null");
                    break;
                case OVERDUE:
                    query.append("and datetime(c.duedate/1000, 'unixepoch', 'localtime') <= datetime('now', 'localtime')");
                    break;
                case TODAY:
                    query.append("and datetime(c.duedate/1000, 'unixepoch', 'localtime') between datetime('now', 'localtime') and datetime('now', '+24 hour', 'localtime')");
                    break;
                case WEEK:
                    query.append("and datetime(c.duedate/1000, 'unixepoch', 'localtime') between datetime('now', 'localtime') and datetime('now', '+7 day', 'localtime')");
                    break;
                case MONTH:
                    query.append("and datetime(c.duedate/1000, 'unixepoch', 'localtime') between datetime('now', 'localtime') and datetime('now', '+30 day', 'localtime')");
                    break;
                default:
                    throw new IllegalArgumentException("Xou need to add your new EDueType value\"" + filter.getDueType() + "\" here!");
            }
        }
        query.append(" and status<>3 order by `order`, createdAt asc;");
        return LiveDataHelper.interceptLiveData(db.getCardDao().getFilteredFullCardsForStack(new SimpleSQLiteQuery(query.toString(), args.toArray())), this::filterRelationsForCard);

    }

    private void fillSqlWithListValues(StringBuilder query, List<Object> args, List<? extends IRemoteEntity> entities) {
        for (int i = 0; i < entities.size(); i++) {
             if (i > 0) {
                 query.append(", ");
             }
            query.append("?");
            args.add(entities.get(i).getLocalId());
        }
    }

    public List<FullCard> getFullCardsForStackDirectly(long accountId, long localStackId) {
        return db.getCardDao().getFullCardsForStackDirectly(accountId, localStackId);
    }

    public User getUserByUidDirectly(long accountId, String uid) {
        return db.getUserDao().getUserByUidDirectly(accountId, uid);
    }

    public long createUser(long accountId, User user) {
        user.setAccountId(accountId);
        return db.getUserDao().insert(user);
    }

    public void updateUser(long accountId, User user, boolean setStatus) {
        markAsEditedIfNeeded(user, setStatus);
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
        createJoinCardWithLabel(localLabelId, localCardId, DBStatus.UP_TO_DATE);
    }

    public void createJoinCardWithLabel(long localLabelId, long localCardId, DBStatus status) {
        JoinCardWithLabel existing = db.getJoinCardWithLabelDao().getJoin(localLabelId, localCardId);
        if (existing != null && existing.getStatusEnum() == DBStatus.LOCAL_DELETED){
            // readded!
            existing.setStatusEnum(DBStatus.LOCAL_EDITED);
            db.getJoinCardWithLabelDao().update(existing);
        } else {
            JoinCardWithLabel join = new JoinCardWithLabel();
            join.setCardId(localCardId);
            join.setLabelId(localLabelId);
            join.setStatus(status.getId());
            db.getJoinCardWithLabelDao().insert(join);
        }


    }

    public void deleteJoinedLabelsForCard(long localCardId) {
        db.getJoinCardWithLabelDao().deleteByCardId(localCardId);
    }
    public void deleteJoinedLabelForCard(long localCardId, long localLabelId) {
        db.getJoinCardWithLabelDao().setDbStatus(localCardId, localLabelId, DBStatus.LOCAL_DELETED.getId());
    }

    public void deleteJoinedUserForCard(long localCardId, long localUserId) {
        db.getJoinCardWithUserDao().setDbStatus(localCardId, localUserId, DBStatus.LOCAL_DELETED.getId());
    }

    public void deleteJoinedLabelForCardPhysically(long localCardId, long localLabelId) {
        db.getJoinCardWithLabelDao().deleteByCardIdAndLabelId(localCardId, localLabelId);
    }
    public void deleteJoinedUserForCardPhysically(long localCardId, long localUserId) {
        db.getJoinCardWithUserDao().deleteByCardIdAndUserIdPhysically(localCardId, localUserId);
    }

    public void createJoinCardWithUser(long localUserId, long localCardId) {
        createJoinCardWithUser(localUserId, localCardId, DBStatus.UP_TO_DATE);
    }

    public void createJoinCardWithUser(long localUserId, long localCardId, DBStatus status) {
        JoinCardWithUser existing = db.getJoinCardWithUserDao().getJoin(localUserId, localCardId);
        if (existing != null && existing.getStatusEnum() == DBStatus.LOCAL_DELETED){
            // readded!
            existing.setStatusEnum(DBStatus.LOCAL_EDITED);
            db.getJoinCardWithUserDao().update(existing);
        } else {
            JoinCardWithUser join = new JoinCardWithUser();
            join.setCardId(localCardId);
            join.setUserId(localUserId);
            join.setStatus(status.getId());
            db.getJoinCardWithUserDao().insert(join);
        }
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
    
    public void updateLabel(Label label, boolean setStatus) {
        markAsEditedIfNeeded(label, setStatus);
        db.getLabelDao().update(label);
    }

    public void deleteLabel(Label label, boolean setStatus) {
        markAsDeletedIfNeeded(label, setStatus);
        db.getLabelDao().update(label);
    }
    public void deleteLabelPhysically(Label label) {
        db.getLabelDao().delete(label);
    }

    public WrappedLiveData<Account> createAccount(Account account) {
        return LiveDataHelper.wrapInLiveData(() -> {
            long id = db.getAccountDao().insert(account);
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
        return LiveDataHelper.onlyIfChanged(db.getAccountDao().getAccountById(id));
    }

    public LiveData<Account> readAccount(String name) {
        return LiveDataHelper.wrapInLiveData(()->db.getAccountDao().getAccountByNameDirectly(name));
//        return LiveDataHelper.onlyIfChanged(db.getAccountDao().getAccountByName(name));
    }

    public Account readAccountDirectly(long id) {
        return db.getAccountDao().getAccountByIdDirectly(id);
    }

    public LiveData<List<Account>> readAccounts() {
        return LiveDataHelper.onlyIfChanged(db.getAccountDao().getAllAccounts());
    }

    public LiveData<List<Board>> getBoards(long accountId) {
        return LiveDataHelper.onlyIfChanged(db.getBoardDao().getBoardsForAccount(accountId));
    }

    public LiveData<List<Board>> getBoardsWithEditPermission(long accountId) {
        return LiveDataHelper.onlyIfChanged(db.getBoardDao().getBoardsWithEditPermissionsForAccount(accountId));
    }

    public WrappedLiveData<Board> createBoard(long accountId, Board board) {
        return LiveDataHelper.wrapInLiveData(() -> {
            board.setAccountId(accountId);
            long id = db.getBoardDao().insert(board);
            return db.getBoardDao().getBoardByIdDirectly(id);

        });
    }

    public long createBoardDirectly(long accountId, Board board) {
            board.setAccountId(accountId);
            return db.getBoardDao().insert(board);
    }

    public void deleteBoard(Board board, boolean setStatus) {
        markAsDeletedIfNeeded(board, setStatus);
        db.getBoardDao().update(board);
    }

    public void deleteBoardPhysically(Board board) {
        db.getBoardDao().delete(board);
    }

    public void updateBoard(Board board, boolean setStatus) {
        markAsEditedIfNeeded(board, setStatus);
        db.getBoardDao().update(board);
    }

    public LiveData<List<FullStack>> getFullStacksForBoard(long accountId, long localBoardId) {
        return LiveDataHelper.onlyIfChanged(db.getStackDao().getFullStacksForBoard(accountId, localBoardId));
    }

    public List<FullStack> getFullStacksForBoardDirectly(long accountId, long localBoardId) {
        return db.getStackDao().getFullStacksForBoardDirectly(accountId, localBoardId);
    }

    public LiveData<FullStack> getStack(long accountId, long localStackId) {
        return LiveDataHelper.onlyIfChanged(db.getStackDao().getFullStack(accountId, localStackId));
    }
    
    public long createStack(long accountId, Stack stack) {
        stack.setAccountId(accountId);
        return db.getStackDao().insert(stack);
    }
    
    public void deleteStack(Stack stack, boolean setStatus) {
        markAsDeletedIfNeeded(stack, setStatus);
        db.getStackDao().update(stack);
    }

    public void deleteStackPhysically(Stack stack) {
        db.getStackDao().delete(stack);
    }

    public void updateStack(Stack stack, boolean setStatus) {
        markAsEditedIfNeeded(stack, setStatus);
        db.getStackDao().update(stack);
    }
    
    public Card  getCardByLocalIdDirectly(long accountId, long localCardId) {
        return db.getCardDao().getCardByLocalIdDirectly(accountId, localCardId);
    }

    public LiveData<FullCard>  getCardByLocalId(long accountId, long localCardId) {
        return LiveDataHelper.interceptLiveData(db.getCardDao().getFullCardByLocalId(accountId, localCardId), this::filterRelationsForCard);
    }
    public List<FullCard> getLocallyChangedCardsDirectly(long accountId) {
        return db.getCardDao().getLocallyChangedCardsDirectly(accountId);
    }
    public List<FullCard> getLocallyChangedCardsByLocalStackIdDirectly(long accountId, long localStackId) {
        return db.getCardDao().getLocallyChangedCardsByLocalStackIdDirectly(accountId, localStackId);
    }

    public long createCard(long accountId, Card card) {
        card.setAccountId(accountId);
        return db.getCardDao().insert(card);
    }

    public void deleteCard(Card card, boolean setStatus) {
        markAsDeletedIfNeeded(card, setStatus);
        if (setStatus){
            db.getCardDao().update(card);
        } else {
            deleteCardPhysically(card);
        }
    }

    public void deleteCardPhysically(Card card) {
        db.getCardDao().delete(card);
    }

    public void updateCard(Card card, boolean setStatus) {
        markAsEditedIfNeeded(card, setStatus);
        db.getCardDao().update(card);
    }

    public long createAccessControl(long accountId, AccessControl entity) {
        entity.setAccountId(accountId);
        return db.getAccessControlDao().insert(entity);
    }

    public AccessControl getAccessControlByRemoteIdDirectly(long accountId, Long id) {
        return db.getAccessControlDao().getAccessControlByRemoteIdDirectly(accountId, id);
    }

    public LiveData<List<AccessControl>> getAccessControlByLocalBoardId(long accountId, Long id) {
        return LiveDataHelper.interceptLiveData(db.getAccessControlDao().getAccessControlByLocalBoardId(accountId, id), (acl) -> {
            readRelationsForACL(acl);
        });
    }

    public void updateAccessControl(AccessControl entity, boolean setStatus) {
        markAsEditedIfNeeded(entity, setStatus);
        db.getAccessControlDao().update(entity);
    }

    public void deleteAccessControl(AccessControl entity, boolean setStatus) {
        markAsDeletedIfNeeded(entity, setStatus);
        if (setStatus){
            db.getAccessControlDao().update(entity);
        } else {
            db.getAccessControlDao().delete(entity);
        }
    }

    public LiveData<FullBoard> getFullBoardById(Long accountId, Long localId) {
        return db.getBoardDao().getFullBoardById(accountId, localId);
    }
    public Board getBoardByLocalIdDirectly(long localId) {
        return db.getBoardDao().getBoardByIdDirectly(localId);
    }

    public LiveData<User> getUserByLocalId(long accountId, long localId){
        return db.getUserDao().getUserByLocalId(accountId, localId);
    }

    public LiveData<User> getUserByUid(long accountId, String uid){
        return db.getUserDao().getUserByUid(accountId, uid);
    }

    public LiveData<List<User>> getUsersForAccount(final long accountId){
        return db.getUserDao().getUsersForAccount(accountId);
    }

    public LiveData<List<User>> searchUserByUidOrDisplayName(final long accountId, final long notYetAssignedToLocalCardId, final String searchTerm){
        validateSearchTerm(searchTerm);
        return db.getUserDao().searchUserByUidOrDisplayName(accountId, notYetAssignedToLocalCardId, "%"+searchTerm.trim()+"%");
    }

    public LiveData<List<User>> searchUserByUidOrDisplayNameForACL(final long accountId, final long notYetAssignedToACL, final String searchTerm){
        validateSearchTerm(searchTerm);
        return db.getUserDao().searchUserByUidOrDisplayNameForACL(accountId, notYetAssignedToACL, "%"+searchTerm.trim()+"%");
    }

    public LiveData<List<Label>> searchNotYetAssignedLabelsByTitle(final long accountId, final long boardId, final long notYetAssignedToLocalCardId, String searchTerm){
        validateSearchTerm(searchTerm);
        return db.getLabelDao().searchNotYetAssignedLabelsByTitle(accountId, boardId, notYetAssignedToLocalCardId,"%"+searchTerm.trim()+"%");
    }

    public LiveData<List<User>> findProposalsForUsersToAssign(final long accountId, long boardId, long notAssignedToLocalCardId, final int topX){
        return db.getUserDao().findProposalsForUsersToAssign(accountId, boardId, notAssignedToLocalCardId, topX);
    }

    public LiveData<List<User>> findProposalsForUsersToAssignForACL(final long accountId, long boardId, final int topX){
        return db.getUserDao().findProposalsForUsersToAssignForACL(accountId, boardId, topX);
    }

    public LiveData<List<Label>> findProposalsForLabelsToAssign(final long accountId, final long boardId, long notAssignedToLocalCardId){
        return db.getLabelDao().findProposalsForLabelsToAssign(accountId, boardId, notAssignedToLocalCardId);
    }


    public Attachment getAttachmentByRemoteIdDirectly(long accountId, Long id) {
        return db.getAttachmentDao().getAttachmentByRemoteIdDirectly(accountId, id);
    }

    public Attachment getAttachmentByLocalIdDirectly(long accountId, Long id) {
        return db.getAttachmentDao().getAttachmentByLocalIdDirectly(accountId, id);
    }

    public List<Attachment> getAttachmentsForLocalCardIdDirectly(long accountId, Long localCardId) {
        return db.getAttachmentDao().getAttachmentsForLocalCardIdDirectly(accountId, localCardId);
    }

    public List<Attachment> getLocallyChangedAttachmentsByLocalCardIdDirectly(long accountId, Long localCardId) {
        return db.getAttachmentDao().getLocallyChangedAttachmentsByLocalCardIdDirectly(accountId, localCardId);
    }

    public List<Attachment> getLocallyChangedAttachmentsDirectly(long accountId) {
        return db.getAttachmentDao().getLocallyChangedAttachmentsDirectly(accountId);
    }

    public long createAttachment(long accountId, Attachment attachment) {
        attachment.setAccountId(accountId);
        attachment.setCreatedAt(new Date());
        return db.getAttachmentDao().insert(attachment);
    }

    public void updateAttachment(long accountId, Attachment attachment, boolean setStatus) {
        markAsEditedIfNeeded(attachment, setStatus);
        attachment.setAccountId(accountId);
        db.getAttachmentDao().update(attachment);
    }

    public void deleteAttachment(long accountId, Attachment attachment, boolean setStatus) {
        attachment.setAccountId(accountId);
        if (setStatus) {
            markAsDeletedIfNeeded(attachment, setStatus);
            db.getAttachmentDao().update(attachment);
        } else {
            db.getAttachmentDao().delete(attachment);
        }
    }

    private void validateSearchTerm(String searchTerm){
        if (searchTerm == null || searchTerm.trim().length()<1) {
            throw new IllegalArgumentException("please provide a proper search term! \""+searchTerm+"\" doesn't seem right...");
        }
    }

    public Account getAccountByIdDirectly(long accountId) {
        return db.getAccountDao().getAccountByIdDirectly(accountId);
    }

    public List<Account> getAllAccountsDirectly() {
        return db.getAccountDao().getAllAccountsDirectly();
    }

    public User getUserByLocalIdDirectly(long localUserId) {
        return db.getUserDao().getUserByLocalIdDirectly(localUserId);
    }

    public void setStatusForJoinCardWithUser(long localCardId, long localUserId, int status) {
        db.getJoinCardWithUserDao().setDbStatus(localCardId, localUserId, status);
    }

    public void setStatusForJoinCardWithLabel(long localCardId, long localLabelId, int status) {
        db.getJoinCardWithLabelDao().setDbStatus(localCardId, localLabelId, status);
    }

    public Label getLabelByLocalIdDirectly(long localLabelId) {
        return db.getLabelDao().getLabelsByIdDirectly(localLabelId);
    }
    public LiveData<Label> getLabelByLocalId(long localLabelId) {
        return db.getLabelDao().getLabelByLocalId(localLabelId);
    }

    public List<FullBoard> getLocallyChangedBoards(long accountId) {
        return db.getBoardDao().getLocallyChangedBoardsDirectly(accountId);
    }
    public List<FullBoard> getAllFullBoards(long accountId) {
        return db.getBoardDao().getAllFullBoards(accountId);
    }

    public List<FullStack> getLocallyChangedStacksForBoard(long accountId, long localBoardId) {
        return db.getStackDao().getLocallyChangedStacksForBoardDirectly(accountId, localBoardId);
    }
    public List<FullStack> getLocallyChangedStacks(long accountId) {
        return db.getStackDao().getLocallyChangedStacksDirectly(accountId);
    }
    public List<Label> getLocallyChangedLabels(long accountId) {
        return db.getLabelDao().getLocallyChangedLabelsDirectly(accountId);
    }

    public Board getBoardByLocalCardIdDirectly(long localCardId) {
        return db.getBoardDao().getBoardByLocalCardIdDirectly(localCardId);
    }

    public JoinCardWithLabel getJoinCardWithLabel(Long localLabelId, Long localCardId) {
        return db.getJoinCardWithLabelDao().getJoin(localLabelId, localCardId);
    }

    public JoinCardWithUser getJoinCardWithUser(Long localUserId, Long localCardId) {
        return db.getJoinCardWithUserDao().getJoin(localUserId, localCardId);
    }

    public List<JoinCardWithLabel> getAllDeletedJoinsWithRemoteIDs() {
        return db.getJoinCardWithLabelDao().getAllDeletedJoinsWithRemoteIDs();
    }

    public List<JoinCardWithLabel> getAllDeletedJoins() {
        return db.getJoinCardWithLabelDao().getAllDeletedJoins();
    }
    public JoinCardWithLabel getRemoteIdsForJoin(Long localCardId, Long localLabelId) {
        return db.getJoinCardWithLabelDao().getRemoteIdsForJoin(localCardId, localLabelId);
    }

    public List<JoinCardWithUser> getAllDeletedUserJoinsWithRemoteIDs() {
        return db.getJoinCardWithUserDao().getDeletedJoinsWithRemoteIDs();
    }

    public void deleteJoinedLabelForCardPhysicallyByRemoteIDs(Long accountId, Long remoteCardId, Long remoteLabelId) {
        db.getJoinCardWithLabelDao().deleteJoinedLabelForCardPhysicallyByRemoteIDs(accountId, remoteCardId, remoteLabelId);
    }

    public void deleteJoinedUserForCardPhysicallyByRemoteIDs(Long accountId, Long remoteCardId, String userUid) {
        db.getJoinCardWithUserDao().deleteJoinedUserForCardPhysicallyByRemoteIDs(accountId, remoteCardId, userUid);
    }

    public LiveData<List<Activity>> getActivitiesForCard(Long localCardId) {
        return db.getActivityDao().getActivitiesForCard(localCardId);
    }

    public long createActivity(long accountId, Activity activity) {
        activity.setAccountId(accountId);
        return db.getActivityDao().insert(activity);
    }
    public Activity getActivityByRemoteIdDirectly(long accountId, long remoteActivityId) {
        return db.getActivityDao().getActivityByRemoteIdDirectly(accountId, remoteActivityId);
    }

    public void updateActivity(Activity activity, boolean setStatus) {
        markAsEditedIfNeeded(activity, setStatus);
        db.getActivityDao().update(activity);
    }

    public void deleteActivity(Activity activity) {
        db.getActivityDao().delete(activity);
    }

    public List<AccessControl> getLocallyChangedAccessControl(long accountId, long boardId) {
        return db.getAccessControlDao().getLocallyChangedAccessControl(accountId, boardId);
    }
    public List<Long> getBoardIDsOfLocallyChangedAccessControl(long accountId) {
        return db.getAccessControlDao().getBoardIDsOfLocallyChangedAccessControl(accountId);
    }

    public LiveData<List<DeckComment>> getCommentsForLocalCardId(long localCardId) {
        return LiveDataHelper.interceptLiveData(db.getCommentDao().getCommentByLocalCardId(localCardId), (list) -> {
            for (DeckComment deckComment : list) {
                deckComment.setMentions(db.getMentionDao().getMentionsForCommentIdDirectly(deckComment.getLocalId()));
            }
        });
    }

    public DeckComment getCommentByRemoteIdDirectly(long accountId, Long remoteCommentId) {
        return db.getCommentDao().getCommentByRemoteIdDirectly(accountId, remoteCommentId);
    }

    public DeckComment getCommentByLocalIdDirectly(long accountId, Long localCommentId) {
        return db.getCommentDao().getCommentByLocalIdDirectly(accountId, localCommentId);
    }

    public long createComment(long accountId, DeckComment comment) {
        comment.setAccountId(accountId);
        return db.getCommentDao().insert(comment);
    }

    public void updateComment(DeckComment comment, boolean setStatus) {
        markAsEditedIfNeeded(comment, setStatus);
        db.getCommentDao().update(comment);
    }

    public void deleteComment(DeckComment comment, boolean setStatus) {
        markAsDeletedIfNeeded(comment, setStatus);
        if (setStatus) {
            db.getCommentDao().update(comment);
        } else {
            db.getCommentDao().delete(comment);
        }
    }

    public List<DeckComment> getLocallyChangedCommentsByLocalCardIdDirectly(long accountId, long localCardId) {
        return db.getCommentDao().getLocallyChangedCommentsByLocalCardIdDirectly(accountId, localCardId);
    }

    public void clearMentionsForCommentId(long commentID) {
        db.getMentionDao().clearMentionsForCommentId(commentID);
    }

    public long createMention(Mention mention) {
        return db.getMentionDao().insert(mention);
    }

    public List<DeckComment> getCommentByLocalCardIdDirectly(Long localCardId) {
        return db.getCommentDao().getCommentByLocalCardIdDirectly(localCardId);
    }

    public List<Card> getCardsWithLocallyChangedCommentsDirectly(Long accountId) {
        return db.getCardDao().getCardsWithLocallyChangedCommentsDirectly(accountId);
    }

    public Long getLocalStackIdByRemoteStackIdDirectly(long accountId, Long stackId) {
        return db.getStackDao().getLocalStackIdByRemoteStackIdDirectly(accountId, stackId);
    }

    public LiveData<Long> getLocalBoardIdByCardRemoteIdAndAccountId(long cardRemoteId, long accountId) {
        return db.getBoardDao().getLocalBoardIdByCardRemoteIdAndAccountId(cardRemoteId, accountId);
    }

    public LiveData<Integer> countCardsInStack(long accountId, long localStackId) {
        return db.getCardDao().countCardsInStack(accountId, localStackId);
    }
}
