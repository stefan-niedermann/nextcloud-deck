package it.niedermann.nextcloud.deck.persistence.sync.adapters.db;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.AnyThread;
import androidx.annotation.ColorInt;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.sqlite.db.SimpleSQLiteQuery;

import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
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
import it.niedermann.nextcloud.deck.model.appwidgets.StackWidgetModel;
import it.niedermann.nextcloud.deck.model.enums.DBStatus;
import it.niedermann.nextcloud.deck.model.enums.EDueType;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.model.full.FullCardWithProjects;
import it.niedermann.nextcloud.deck.model.full.FullSingleCardWidgetModel;
import it.niedermann.nextcloud.deck.model.full.FullStack;
import it.niedermann.nextcloud.deck.model.interfaces.AbstractRemoteEntity;
import it.niedermann.nextcloud.deck.model.interfaces.IRemoteEntity;
import it.niedermann.nextcloud.deck.model.internal.FilterInformation;
import it.niedermann.nextcloud.deck.model.ocs.Activity;
import it.niedermann.nextcloud.deck.model.ocs.comment.DeckComment;
import it.niedermann.nextcloud.deck.model.ocs.comment.Mention;
import it.niedermann.nextcloud.deck.model.ocs.comment.full.FullDeckComment;
import it.niedermann.nextcloud.deck.model.ocs.projects.JoinCardWithProject;
import it.niedermann.nextcloud.deck.model.ocs.projects.OcsProject;
import it.niedermann.nextcloud.deck.model.ocs.projects.OcsProjectResource;
import it.niedermann.nextcloud.deck.model.relations.UserInBoard;
import it.niedermann.nextcloud.deck.model.relations.UserInGroup;
import it.niedermann.nextcloud.deck.model.widget.filter.EWidgetType;
import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidget;
import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidgetAccount;
import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidgetBoard;
import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidgetLabel;
import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidgetProject;
import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidgetSort;
import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidgetStack;
import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidgetUser;
import it.niedermann.nextcloud.deck.model.widget.filter.dto.FilterWidgetCard;
import it.niedermann.nextcloud.deck.model.widget.singlecard.SingleCardWidgetModel;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.LiveDataHelper;
import it.niedermann.nextcloud.deck.ui.upcomingcards.UpcomingCardsAdapterItem;
import it.niedermann.nextcloud.deck.ui.widget.singlecard.SingleCardWidget;

import static androidx.lifecycle.Transformations.distinctUntilChanged;

public class DataBaseAdapter {

    @NonNull
    private final DeckDatabase db;
    @NonNull
    private final Context context;
    @NonNull
    private final ExecutorService widgetNotifierExecutor;

    public DataBaseAdapter(@NonNull Context appContext) {
        this(appContext, DeckDatabase.getInstance(appContext), Executors.newCachedThreadPool());
    }

    private DataBaseAdapter(@NonNull Context applicationContext, @NonNull DeckDatabase db, @NonNull ExecutorService widgetNotifierExecutor) {
        this.context = applicationContext;
        this.db = db;
        this.widgetNotifierExecutor = widgetNotifierExecutor;
    }

    @NonNull
    public Context getContext() {
        return context;
    }

    private <T extends AbstractRemoteEntity> void markAsEditedIfNeeded(T entity, boolean setStatus) {
        if (!setStatus) return;
        entity.setLastModifiedLocal(Instant.now());
        entity.setStatusEnum(DBStatus.LOCAL_EDITED);
    }

    private <T extends AbstractRemoteEntity> void markAsDeletedIfNeeded(T entity, boolean setStatus) {
        if (!setStatus) return;
        entity.setStatusEnum(DBStatus.LOCAL_DELETED);
        entity.setLastModifiedLocal(Instant.now());
    }

    public LiveData<Boolean> hasAccounts() {
        return LiveDataHelper.postCustomValue(db.getAccountDao().countAccounts(), data -> data != null && data > 0);
    }

    public LiveData<Board> getBoardByRemoteId(long accountId, long remoteId) {
        return distinctUntilChanged(db.getBoardDao().getBoardByRemoteId(accountId, remoteId));
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
        return distinctUntilChanged(db.getStackDao().getStackByRemoteId(accountId, localBoardId, remoteId));
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
        return distinctUntilChanged(db.getCardDao().getCardByRemoteId(accountId, remoteId));
    }

    public FullCard getFullCardByRemoteIdDirectly(long accountId, long remoteId) {
        FullCard card = db.getCardDao().getFullCardByRemoteIdDirectly(accountId, remoteId);
        filterRelationsForCard(card);
        return card;
    }

    public FullCard getFullCardByLocalIdDirectly(long accountId, long localId) {
        return db.getCardDao().getFullCardByLocalIdDirectly(accountId, localId);
    }

    public void filterRelationsForCard(@Nullable FullCard card) {
        if (card != null) {
            if (card.getLabels() != null && !card.getLabels().isEmpty()) {
                List<Long> filteredIDs = db.getJoinCardWithLabelDao().filterDeleted(card.getLocalId(), getLocalIDs(card.getLabels()));
                card.setLabels(db.getLabelDao().getLabelsByIdsDirectly(filteredIDs));
            }
            if (card.getAssignedUsers() != null && !card.getAssignedUsers().isEmpty()) {
                List<Long> filteredIDs = db.getJoinCardWithUserDao().filterDeleted(card.getLocalId(), getLocalIDs(card.getAssignedUsers()));
                card.setAssignedUsers(db.getUserDao().getUsersByIdsDirectly(filteredIDs));
            }
        }
    }

    private <T> List<Long> getLocalIDs(@NonNull List<? extends AbstractRemoteEntity> remoteEntityList) {
        ArrayList<Long> ids = new ArrayList<>(remoteEntityList.size());
        for (AbstractRemoteEntity entity : remoteEntityList) {
            ids.add(entity.getLocalId());
        }
        return ids;
    }

    public void readRelationsForACL(@Nullable List<AccessControl> acl) {
        if (acl != null) {
            for (AccessControl accessControl : acl) {
                readRelationsForACL(accessControl);
            }
        }
    }

    public void readRelationsForACL(@Nullable AccessControl acl) {
        if (acl != null) {
            if (acl.getUserId() != null) {
                acl.setUser(db.getUserDao().getUserByLocalIdDirectly(acl.getUserId()));
            }
        }
    }

    private void filterRelationsForCard(@Nullable Collection<FullCard> card) {
        if (card == null) {
            return;
        }
        for (FullCard c : card) {
            filterRelationsForCard(c);
        }
    }

    @WorkerThread
    public Card getCardByRemoteIdDirectly(long accountId, long remoteId) {
        return db.getCardDao().getCardByRemoteIdDirectly(accountId, remoteId);
    }

    public LiveData<List<FullCard>> getFullCardsForStack(long accountId, long localStackId, FilterInformation filter) {
        if (filter == null) {
            return LiveDataHelper.interceptLiveData(db.getCardDao().getFullCardsForStack(accountId, localStackId), this::filterRelationsForCard);
        }
        return LiveDataHelper.interceptLiveData(db.getCardDao().getFilteredFullCardsForStack(getQueryForFilter(filter, accountId, localStackId)), this::filterRelationsForCard);

    }

    private void fillSqlWithEntityListValues(StringBuilder query, Collection<Object> args, @NonNull List<? extends IRemoteEntity> entities) {
        List<Long> idList = entities.stream().map(IRemoteEntity::getLocalId).collect(Collectors.toList());
        fillSqlWithListValues(query, args, idList);
    }

    private void fillSqlWithListValues(StringBuilder query, Collection<Object> args, @NonNull List<?> values) {
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) {
                query.append(", ");
            }
            query.append("?");
            args.add(values.get(i));
        }
    }

    @WorkerThread
    public List<FullCard> getFullCardsForStackDirectly(long accountId, long localStackId, @Nullable FilterInformation filter) {
        return filter == null
                ? db.getCardDao().getFullCardsForStackDirectly(accountId, localStackId)
                : db.getCardDao().getFilteredFullCardsForStackDirectly(getQueryForFilter(filter, accountId, localStackId));
    }

    @AnyThread
    private SimpleSQLiteQuery getQueryForFilter(FilterInformation filter, long accountId, long localStackId) {
        return getQueryForFilter(filter, Collections.singletonList(accountId), Collections.singletonList(localStackId));
    }

    @AnyThread
    private SimpleSQLiteQuery getQueryForFilter(FilterInformation filter, List<Long> accountIds, List<Long> localStackIds) {
        final Collection<Object> args = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT * FROM card c WHERE 1=1 ");
        if (accountIds != null && !accountIds.isEmpty()) {
            query.append("and accountId in (");
            fillSqlWithListValues(query, args, accountIds);
            query.append(") ");
        }
        if (localStackIds != null && !localStackIds.isEmpty()) {
            query.append("and stackId in (");
            fillSqlWithListValues(query, args, localStackIds);
            query.append(") ");
        }

        if (!filter.getLabels().isEmpty()) {
            query.append("and (exists(select 1 from joincardwithlabel j where c.localId = cardId and labelId in (");
            fillSqlWithEntityListValues(query, args, filter.getLabels());
            query.append(") and j.status<>3) ");
            if (filter.isNoAssignedLabel()) {
                query.append("or not exists(select 1 from joincardwithlabel j where c.localId = cardId and j.status<>3)) ");
            } else {
                query.append(") ");
            }
        } else if (filter.isNoAssignedLabel()) {
            query.append("and not exists(select 1 from joincardwithlabel j where c.localId = cardId and j.status<>3) ");
        }

        if (!filter.getUsers().isEmpty()) {
            query.append("and (exists(select 1 from joincardwithuser j where c.localId = cardId and userId in (");
            fillSqlWithEntityListValues(query, args, filter.getUsers());
            query.append(") and j.status<>3) ");
            if (filter.isNoAssignedUser()) {
                query.append("or not exists(select 1 from joincardwithuser j where c.localId = cardId and j.status<>3)) ");
            } else {
                query.append(") ");
            }
        } else if (filter.isNoAssignedUser()) {
            query.append("and not exists(select 1 from joincardwithuser j where c.localId = cardId and j.status<>3) ");
        }

        if (!filter.getProjects().isEmpty()) {
            query.append("and (exists(select 1 from joincardwithproject j where c.localId = cardId and projectId in (");
            fillSqlWithEntityListValues(query, args, filter.getProjects());
            query.append(") and j.status<>3) ");
            if (filter.isNoAssignedProject()) {
                query.append("or not exists(select 1 from joincardwithproject j where c.localId = cardId and j.status<>3)) ");
            } else {
                query.append(") ");
            }
        } else if (filter.isNoAssignedProject()) {
            query.append("and not exists(select 1 from joincardwithproject j where c.localId = cardId and j.status<>3) ");
        }

        if (filter.getDueType() != EDueType.NO_FILTER) {
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
                    throw new IllegalArgumentException("You need to add your new EDueType value\"" + filter.getDueType() + "\" here!");
            }
        }
        if (filter.getFilterText() != null && !filter.getFilterText().isEmpty()) {
            query.append(" and (c.description like ? or c.title like ?) ");
            String filterText = "%" + filter.getFilterText() + "%";
            args.add(filterText);
            args.add(filterText);
        }
        if (filter.getArchiveStatus() != FilterInformation.EArchiveStatus.ALL) {
            query.append(" and c.archived = ").append(filter.getArchiveStatus() == FilterInformation.EArchiveStatus.ARCHIVED ? 1 : 0);
        }
        query.append(" and status<>3 order by accountId asc, stackId asc, `order`, createdAt asc;");
        return new SimpleSQLiteQuery(query.toString(), args.toArray());
    }

    @WorkerThread
    public User getUserByUidDirectly(long accountId, String uid) {
        return db.getUserDao().getUserByUidDirectly(accountId, uid);
    }

    @WorkerThread
    public long createUser(long accountId, User user) {
        user.setAccountId(accountId);
        final long newId = db.getUserDao().insert(user);
        final Account account = db.getAccountDao().getAccountByIdDirectly(accountId);
        if (account.getUserName().equals(user.getUid())) {
            for (FilterWidget widget : getFilterWidgetsByType(EWidgetType.UPCOMING_WIDGET)) {
                for (FilterWidgetAccount widgetAccount : widget.getAccounts()) {
                    if (widgetAccount.getAccountId() == accountId && widgetAccount.getUsers().isEmpty()) {
                        FilterWidgetUser u = new FilterWidgetUser();
                        u.setFilterAccountId(widgetAccount.getId());
                        u.setUserId(newId);
                        widgetAccount.getUsers().add(u);
                        updateFilterWidgetDirectly(widget);
                    }
                }
            }
        }
        notifyFilterWidgetsAboutChangedEntity(FilterWidget.EChangedEntityType.USER, newId);
        return newId;
    }

    @WorkerThread
    public void updateUser(long accountId, User user, boolean setStatus) {
        markAsEditedIfNeeded(user, setStatus);
        user.setAccountId(accountId);
        db.getUserDao().update(user);
        notifyFilterWidgetsAboutChangedEntity(FilterWidget.EChangedEntityType.USER, user.getLocalId());
    }

    @UiThread
    public LiveData<Label> getLabelByRemoteId(long accountId, long remoteId) {
        return distinctUntilChanged(db.getLabelDao().getLabelByRemoteId(accountId, remoteId));
    }

    @WorkerThread
    public Label getLabelByRemoteIdDirectly(long accountId, long remoteId) {
        return db.getLabelDao().getLabelByRemoteIdDirectly(accountId, remoteId);
    }

    @WorkerThread
    public long createLabelDirectly(long accountId, @NonNull Label label) {
        label.setAccountId(accountId);
        final long newId = db.getLabelDao().insert(label);
        notifyFilterWidgetsAboutChangedEntity(FilterWidget.EChangedEntityType.LABEL, newId);
        return newId;
    }

    public void createJoinCardWithLabel(long localLabelId, long localCardId) {
        createJoinCardWithLabel(localLabelId, localCardId, DBStatus.UP_TO_DATE);
    }

    public void createJoinCardWithLabel(long localLabelId, long localCardId, DBStatus status) {
        final JoinCardWithLabel existing = db.getJoinCardWithLabelDao().getJoin(localLabelId, localCardId);
        if (existing != null && existing.getStatusEnum() == DBStatus.LOCAL_DELETED) {
            // readded!
            existing.setStatusEnum(DBStatus.LOCAL_EDITED);
            db.getJoinCardWithLabelDao().update(existing);
            notifyFilterWidgetsAboutChangedEntity(FilterWidget.EChangedEntityType.LABEL, existing.getLabelId());
        } else {
            final JoinCardWithLabel join = new JoinCardWithLabel();
            join.setCardId(localCardId);
            join.setLabelId(localLabelId);
            join.setStatus(status.getId());
            db.getJoinCardWithLabelDao().insert(join);
            notifyFilterWidgetsAboutChangedEntity(FilterWidget.EChangedEntityType.LABEL, join.getLabelId());
        }
    }

    public void deleteJoinedLabelsForCard(long localCardId) {
        db.getJoinCardWithLabelDao().deleteByCardId(localCardId);
    }

    public void deleteJoinedLabelForCard(long localCardId, long localLabelId) {
        db.getJoinCardWithLabelDao().setDbStatus(localCardId, localLabelId, DBStatus.LOCAL_DELETED.getId());
        notifyFilterWidgetsAboutChangedEntity(FilterWidget.EChangedEntityType.LABEL, localLabelId);
    }

    public void deleteJoinedUserForCard(long localCardId, long localUserId) {
        db.getJoinCardWithUserDao().setDbStatus(localCardId, localUserId, DBStatus.LOCAL_DELETED.getId());
        notifyFilterWidgetsAboutChangedEntity(FilterWidget.EChangedEntityType.USER, localUserId);
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
        final JoinCardWithUser existing = db.getJoinCardWithUserDao().getJoin(localUserId, localCardId);
        if (existing != null && existing.getStatusEnum() == DBStatus.LOCAL_DELETED) {
            // readded!
            existing.setStatusEnum(DBStatus.LOCAL_EDITED);
            db.getJoinCardWithUserDao().update(existing);
            notifyFilterWidgetsAboutChangedEntity(FilterWidget.EChangedEntityType.USER, localUserId);
        } else if (existing != null) {
            return;
        } else {
            JoinCardWithUser join = new JoinCardWithUser();
            join.setCardId(localCardId);
            join.setUserId(localUserId);
            join.setStatus(status.getId());
            db.getJoinCardWithUserDao().insert(join);
            notifyFilterWidgetsAboutChangedEntity(FilterWidget.EChangedEntityType.USER, localUserId);
        }
    }

    public void deleteJoinedUsersForCard(long localCardId) {
        db.getJoinCardWithUserDao().deleteByCardId(localCardId);
    }

    public void createJoinBoardWithLabel(long localBoardId, long localLabelId) {
        final JoinBoardWithLabel join = new JoinBoardWithLabel();
        join.setBoardId(localBoardId);
        join.setLabelId(localLabelId);
        db.getJoinBoardWithLabelDao().insert(join);
    }

    public void deleteJoinedLabelsForBoard(Long localBoardId) {
        db.getJoinBoardWithLabelDao().deleteByBoardId(localBoardId);
    }

    public void deleteGroupMembershipsOfGroup(Long localGroupUserId) {
        db.getUserInGroupDao().deleteByGroupId(localGroupUserId);
    }

    public void deleteBoardMembershipsOfBoard(Long localBoardId) {
        db.getUserInBoardDao().deleteByBoardId(localBoardId);
    }

    public void addUserToGroup(Long localGroupUserId, Long localGroupMemberId) {
        final UserInGroup relation = new UserInGroup();
        relation.setGroupId(localGroupUserId);
        relation.setMemberId(localGroupMemberId);
        db.getUserInGroupDao().insert(relation);
    }

    public void addUserToBoard(Long localUserId, Long localBoardId) {
        final UserInBoard relation = new UserInBoard();
        relation.setBoardId(localBoardId);
        relation.setUserId(localUserId);
        db.getUserInBoardDao().insert(relation);
    }

    public void updateLabel(Label label, boolean setStatus) {
        markAsEditedIfNeeded(label, setStatus);
        db.getLabelDao().update(label);
        notifyFilterWidgetsAboutChangedEntity(FilterWidget.EChangedEntityType.LABEL, label.getLocalId());
    }

    public void deleteLabel(Label label, boolean setStatus) {
        markAsDeletedIfNeeded(label, setStatus);
        db.getLabelDao().update(label);
        notifyFilterWidgetsAboutChangedEntity(FilterWidget.EChangedEntityType.LABEL, label.getLocalId());
    }

    public void deleteLabelPhysically(Label label) {
        db.getLabelDao().delete(label);
    }

    @WorkerThread
    public Account createAccountDirectly(@NonNull Account account) {
        final long id = db.getAccountDao().insert(account);

        widgetNotifierExecutor.submit(() -> {
            DeckLog.verbose("Adding new created", Account.class.getSimpleName(), " with ", id, " to all instances of ", EWidgetType.UPCOMING_WIDGET.name());
            for (FilterWidget widget : getFilterWidgetsByType(EWidgetType.UPCOMING_WIDGET)) {
                widget.getAccounts().add(new FilterWidgetAccount(id, false));
                updateFilterWidgetDirectly(widget);
            }
            notifyFilterWidgetsAboutChangedEntity(FilterWidget.EChangedEntityType.ACCOUNT, id);
        });
        return readAccountDirectly(id);
    }

    public void deleteAccount(long id) {
        db.getAccountDao().deleteById(id);
        notifyAllWidgets();
        notifyFilterWidgetsAboutChangedEntity(FilterWidget.EChangedEntityType.ACCOUNT, id);
    }

    public void updateAccount(Account account) {
        db.getAccountDao().update(account);
    }

    @UiThread
    public LiveData<Account> readAccount(long id) {
        return distinctUntilChanged(fillAccountsUserName(db.getAccountDao().getAccountById(id)));
    }

    @UiThread
    public LiveData<Account> readAccount(String name) {
        return distinctUntilChanged(fillAccountsUserName(db.getAccountDao().getAccountByName(name)));
    }

    @UiThread
    public LiveData<List<Account>> readAccounts() {
        return distinctUntilChanged(fillAccountsListUserName(db.getAccountDao().getAllAccounts()));
    }

    private LiveData<Account> fillAccountsUserName(LiveData<Account> source) {
        return LiveDataHelper.interceptLiveData(distinctUntilChanged(source), data -> data.setUserDisplayName(db.getUserDao().getUserNameByUidDirectly(data.getId(), data.getUserName())));
    }

    private LiveData<List<Account>> fillAccountsListUserName(LiveData<List<Account>> source) {
        return LiveDataHelper.interceptLiveData(distinctUntilChanged(source), data -> {
            for (Account a : data) {
                a.setUserDisplayName(db.getUserDao().getUserNameByUidDirectly(a.getId(), a.getUserName()));
            }
        });
    }

    @WorkerThread
    public Account readAccountDirectly(long id) {
        return db.getAccountDao().getAccountByIdDirectly(id);
    }


    public LiveData<List<Board>> getBoards(long accountId) {
        return distinctUntilChanged(db.getBoardDao().getBoardsForAccount(accountId));
    }

    public LiveData<List<Board>> getBoards(long accountId, boolean archived) {
        return distinctUntilChanged(
                archived
                        ? db.getBoardDao().getArchivedBoardsForAccount(accountId)
                        : db.getBoardDao().getNonArchivedBoardsForAccount(accountId));
    }

    public LiveData<List<Board>> getBoardsWithEditPermission(long accountId) {
        return distinctUntilChanged(db.getBoardDao().getBoardsWithEditPermissionsForAccount(accountId));
    }

    @WorkerThread
    public long createBoardDirectly(long accountId, @NonNull Board board) {
        board.setAccountId(accountId);
        final long id = db.getBoardDao().insert(board);
        notifyFilterWidgetsAboutChangedEntity(FilterWidget.EChangedEntityType.BOARD, id);
        return id;
    }

    public void deleteBoard(Board board, boolean setStatus) {
        markAsDeletedIfNeeded(board, setStatus);
        db.getBoardDao().update(board);
        notifyAllWidgets();
        notifyFilterWidgetsAboutChangedEntity(FilterWidget.EChangedEntityType.BOARD, board.getLocalId());
    }

    public void deleteBoardPhysically(Board board) {
        db.getBoardDao().delete(board);
        notifyAllWidgets();
    }

    public void updateBoard(Board board, boolean setStatus) {
        markAsEditedIfNeeded(board, setStatus);
        db.getBoardDao().update(board);
        notifyFilterWidgetsAboutChangedEntity(FilterWidget.EChangedEntityType.BOARD, board.getLocalId());
    }

    public LiveData<List<Stack>> getStacksForBoard(long accountId, long localBoardId) {
        return distinctUntilChanged(db.getStackDao().getStacksForBoard(accountId, localBoardId));
    }

    @WorkerThread
    public List<FullStack> getFullStacksForBoardDirectly(long accountId, long localBoardId) {
        return db.getStackDao().getFullStacksForBoardDirectly(accountId, localBoardId);
    }

    @MainThread
    public LiveData<FullStack> getStack(long accountId, long localStackId) {
        return distinctUntilChanged(db.getStackDao().getFullStack(accountId, localStackId));
    }

    @WorkerThread
    public long createStack(long accountId, Stack stack) {
        stack.setAccountId(accountId);
        final long id = db.getStackDao().insert(stack);
        notifyFilterWidgetsAboutChangedEntity(FilterWidget.EChangedEntityType.STACK, id);
        return id;
    }

    @WorkerThread
    public void deleteStack(Stack stack, boolean setStatus) {
        markAsDeletedIfNeeded(stack, setStatus);
        db.getStackDao().update(stack);
        notifyFilterWidgetsAboutChangedEntity(FilterWidget.EChangedEntityType.STACK, stack.getLocalId());
        notifyAllWidgets();
    }

    @WorkerThread
    public void deleteStackPhysically(Stack stack) {
        db.getStackDao().delete(stack);
        notifyFilterWidgetsAboutChangedEntity(FilterWidget.EChangedEntityType.STACK, stack.getLocalId());
        notifyAllWidgets();
    }

    @WorkerThread
    public void updateStack(Stack stack, boolean setStatus) {
        markAsEditedIfNeeded(stack, setStatus);
        db.getStackDao().update(stack);
        notifyFilterWidgetsAboutChangedEntity(FilterWidget.EChangedEntityType.STACK, stack.getLocalId());
//        if (db.getStackWidgetModelDao().containsStackLocalId(stack.getLocalId())) {
//            DeckLog.info("Notifying " + StackWidget.class.getSimpleName() + " about card changes for \"" + stack.getTitle() + "\"");
//            // FIXME StackWidget.notifyDatasetChanged(context);
//        }
    }

    @WorkerThread
    public Card getCardByLocalIdDirectly(long accountId, long localCardId) {
        return db.getCardDao().getCardByLocalIdDirectly(accountId, localCardId);
    }

    @AnyThread
    public LiveData<FullCard> getCardByLocalId(long accountId, long localCardId) {
        return LiveDataHelper.interceptLiveData(db.getCardDao().getFullCardByLocalId(accountId, localCardId), this::filterRelationsForCard);
    }

    @AnyThread
    public LiveData<FullCardWithProjects> getCardWithProjectsByLocalId(long accountId, long localCardId) {
        return LiveDataHelper.interceptLiveData(db.getCardDao().getFullCardWithProjectsByLocalId(accountId, localCardId), this::filterRelationsForCard);
    }

    @WorkerThread
    public List<FullCard> getLocallyChangedCardsDirectly(long accountId) {
        return db.getCardDao().getLocallyChangedCardsDirectly(accountId);
    }

    @WorkerThread
    public List<FullCard> getLocallyChangedCardsByLocalStackIdDirectly(long accountId, long localStackId) {
        return db.getCardDao().getLocallyChangedCardsByLocalStackIdDirectly(accountId, localStackId);
    }

    @WorkerThread
    public long createCardDirectly(long accountId, Card card) {
        card.setAccountId(accountId);
        final long newCardId = db.getCardDao().insert(card);
        notifyFilterWidgetsAboutChangedEntity(FilterWidget.EChangedEntityType.STACK, card.getStackId());
        return newCardId;
    }

    @WorkerThread
    public int getHighestCardOrderInStack(long localStackId) {
        return db.getCardDao().getHighestOrderInStack(localStackId);
    }

    @WorkerThread
    public int getHighestStackOrderInBoard(long localBoardId) {
        return db.getStackDao().getHighestStackOrderInBoard(localBoardId);
    }

    @WorkerThread
    public void deleteCard(Card card, boolean setStatus) {
        markAsDeletedIfNeeded(card, setStatus);
        if (setStatus) {
            db.getCardDao().update(card);
        } else {
            deleteCardPhysically(card);
        }

        notifyFilterWidgetsAboutChangedEntity(FilterWidget.EChangedEntityType.STACK, card.getStackId());
    }

    @WorkerThread
    public void deleteCardPhysically(Card card) {
        db.getCardDao().delete(card);
    }

    @WorkerThread
    public void updateCard(@NonNull Card card, boolean setStatus) {
        markAsEditedIfNeeded(card, setStatus);
        final Long originalStackLocalId = db.getCardDao().getLocalStackIdByLocalCardId(card.getLocalId());
        db.getCardDao().update(card);
        widgetNotifierExecutor.submit(() -> {
            if (db.getSingleCardWidgetModelDao().containsCardLocalId(card.getLocalId())) {
                DeckLog.info("Notifying", SingleCardWidget.class.getSimpleName(), "about card changes for", card.getTitle());
                SingleCardWidget.notifyDatasetChanged(context);
            }
        });
        notifyFilterWidgetsAboutChangedEntity(FilterWidget.EChangedEntityType.STACK, originalStackLocalId);
    }

    @WorkerThread
    public long createAccessControl(long accountId, @NonNull AccessControl entity) {
        entity.setAccountId(accountId);
        notifyFilterWidgetsAboutChangedEntity(FilterWidget.EChangedEntityType.ACCOUNT, accountId);
        return db.getAccessControlDao().insert(entity);
    }

    @WorkerThread
    public AccessControl getAccessControlByRemoteIdDirectly(long accountId, Long id) {
        return db.getAccessControlDao().getAccessControlByRemoteIdDirectly(accountId, id);
    }

    public LiveData<List<AccessControl>> getAccessControlByLocalBoardId(long accountId, Long localBoardId) {
        return LiveDataHelper.interceptLiveData(db.getAccessControlDao().getAccessControlByLocalBoardId(accountId, localBoardId), this::readRelationsForACL);
    }

    public List<AccessControl> getAccessControlByLocalBoardIdDirectly(long accountId, Long localBoardId) {
        return db.getAccessControlDao().getAccessControlByLocalBoardIdDirectly(accountId, localBoardId);
    }

    public void updateAccessControl(AccessControl entity, boolean setStatus) {
        markAsEditedIfNeeded(entity, setStatus);
        notifyFilterWidgetsAboutChangedEntity(FilterWidget.EChangedEntityType.ACCOUNT, entity.getAccountId());
        db.getAccessControlDao().update(entity);
    }

    public void deleteAccessControl(AccessControl entity, boolean setStatus) {
        markAsDeletedIfNeeded(entity, setStatus);
        if (setStatus) {
            db.getAccessControlDao().update(entity);
        } else {
            db.getAccessControlDao().delete(entity);
        }
        notifyFilterWidgetsAboutChangedEntity(FilterWidget.EChangedEntityType.ACCOUNT, entity.getAccountId());
    }

    public LiveData<FullBoard> getFullBoardById(Long accountId, Long localId) {
        return distinctUntilChanged(db.getBoardDao().getFullBoardById(accountId, localId));
    }

    @WorkerThread
    public Board getBoardByLocalIdDirectly(long localId) {
        return db.getBoardDao().getBoardByLocalIdDirectly(localId);
    }

    public LiveData<User> getUserByLocalId(long accountId, long localId) {
        return db.getUserDao().getUserByLocalId(accountId, localId);
    }

    public LiveData<User> getUserByUid(long accountId, String uid) {
        return db.getUserDao().getUserByUid(accountId, uid);
    }

    public LiveData<List<User>> getUsersForAccount(final long accountId) {
        return db.getUserDao().getUsersForAccount(accountId);
    }

    public LiveData<List<User>> searchUserByUidOrDisplayName(final long accountId, final long boardId, final long notYetAssignedToLocalCardId, final String searchTerm) {
        validateSearchTerm(searchTerm);
        return db.getUserDao().searchUserByUidOrDisplayName(accountId, boardId, notYetAssignedToLocalCardId, "%" + searchTerm.trim() + "%");
    }

    public List<User> searchUserByUidOrDisplayNameForACLDirectly(final long accountId, final long notYetAssignedToACL, final String searchTerm) {
        validateSearchTerm(searchTerm);
        return db.getUserDao().searchUserByUidOrDisplayNameForACLDirectly(accountId, notYetAssignedToACL, "%" + searchTerm.trim() + "%");
    }

    public LiveData<List<Label>> searchNotYetAssignedLabelsByTitle(final long accountId, final long boardId, final long notYetAssignedToLocalCardId, String searchTerm) {
        validateSearchTerm(searchTerm);
        return db.getLabelDao().searchNotYetAssignedLabelsByTitle(accountId, boardId, notYetAssignedToLocalCardId, "%" + searchTerm.trim() + "%");
    }

    public LiveData<List<User>> findProposalsForUsersToAssign(final long accountId, long boardId, long notAssignedToLocalCardId, final int topX) {
        return db.getUserDao().findProposalsForUsersToAssign(accountId, boardId, notAssignedToLocalCardId, topX);
    }

    public LiveData<List<User>> findProposalsForUsersToAssignForACL(final long accountId, long boardId, final int topX) {
        return db.getUserDao().findProposalsForUsersToAssignForACL(accountId, boardId, topX);
    }

    public LiveData<List<Label>> findProposalsForLabelsToAssign(final long accountId, final long boardId, long notAssignedToLocalCardId) {
        return db.getLabelDao().findProposalsForLabelsToAssign(accountId, boardId, notAssignedToLocalCardId);
    }

    @WorkerThread
    public Attachment getAttachmentByRemoteIdDirectly(long accountId, Long id) {
        return db.getAttachmentDao().getAttachmentByRemoteIdDirectly(accountId, id);
    }

    @WorkerThread
    public Attachment getAttachmentByLocalIdDirectly(long accountId, Long id) {
        return db.getAttachmentDao().getAttachmentByLocalIdDirectly(accountId, id);
    }

    @WorkerThread
    public List<Attachment> getAttachmentsForLocalCardIdDirectly(long accountId, Long localCardId) {
        return db.getAttachmentDao().getAttachmentsForLocalCardIdDirectly(accountId, localCardId);
    }

    @WorkerThread
    public List<Attachment> getLocallyChangedAttachmentsByLocalCardIdDirectly(long accountId, Long localCardId) {
        return db.getAttachmentDao().getLocallyChangedAttachmentsByLocalCardIdDirectly(accountId, localCardId);
    }

    @WorkerThread
    public List<Attachment> getLocallyChangedAttachmentsDirectly(long accountId) {
        return db.getAttachmentDao().getLocallyChangedAttachmentsDirectly(accountId);
    }

    @WorkerThread
    public List<Attachment> getLocallyChangedAttachmentsForStackDirectly(long localStackId) {
        return db.getAttachmentDao().getLocallyChangedAttachmentsForStackDirectly(localStackId);
    }

    public long createAttachment(long accountId, @NonNull Attachment attachment) {
        attachment.setAccountId(accountId);
        attachment.setCreatedAt(Instant.now());
        return db.getAttachmentDao().insert(attachment);
    }

    public void updateAttachment(long accountId, @NonNull Attachment attachment, boolean setStatus) {
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

    private void validateSearchTerm(@Nullable String searchTerm) {
        if (searchTerm == null || searchTerm.trim().length() < 1) {
            throw new IllegalArgumentException("please provide a proper search term! \"" + searchTerm + "\" doesn't seem right...");
        }
    }

    @WorkerThread
    public Account getAccountByIdDirectly(long accountId) {
        return db.getAccountDao().getAccountByIdDirectly(accountId);
    }

    @WorkerThread
    public List<Account> getAllAccountsDirectly() {
        return db.getAccountDao().getAllAccountsDirectly();
    }

    @WorkerThread
    public User getUserByLocalIdDirectly(long localUserId) {
        return db.getUserDao().getUserByLocalIdDirectly(localUserId);
    }

    public void setStatusForJoinCardWithUser(long localCardId, long localUserId, int status) {
        db.getJoinCardWithUserDao().setDbStatus(localCardId, localUserId, status);
    }

    public void setStatusForJoinCardWithLabel(long localCardId, long localLabelId, int status) {
        db.getJoinCardWithLabelDao().setDbStatus(localCardId, localLabelId, status);
    }

    @WorkerThread
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

    @WorkerThread
    public Board getBoardByLocalCardIdDirectly(long localCardId) {
        return db.getBoardDao().getBoardByLocalCardIdDirectly(localCardId);
    }

    @WorkerThread
    public Long getBoardLocalIdByLocalCardIdDirectly(long localCardId) {
        return db.getBoardDao().getBoardLocalIdByLocalCardIdDirectly(localCardId);
    }

    @WorkerThread
    public FullBoard getFullBoardByLocalCardIdDirectly(long localCardId) {
        return db.getBoardDao().getFullBoardByLocalCardIdDirectly(localCardId);
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

    public List<JoinCardWithLabel> getAllChangedLabelJoins() {
        return db.getJoinCardWithLabelDao().getAllChangedJoins();
    }

    public List<JoinCardWithLabel> getAllChangedLabelJoinsForStack(Long localStackId) {
        return db.getJoinCardWithLabelDao().getAllChangedJoinsForStack(localStackId);
    }

    public JoinCardWithLabel getAllChangedLabelJoinsWithRemoteIDs(Long localCardId, Long localLabelId) {
        return db.getJoinCardWithLabelDao().getRemoteIdsForJoin(localCardId, localLabelId);
    }

    public List<JoinCardWithUser> getAllChangedUserJoinsWithRemoteIDs() {
        return db.getJoinCardWithUserDao().getChangedJoinsWithRemoteIDs();
    }

    public List<JoinCardWithUser> getAllChangedUserJoinsWithRemoteIDsForStack(Long localStackId) {
        return db.getJoinCardWithUserDao().getChangedJoinsWithRemoteIDsForStack(localStackId);
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

    @WorkerThread
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

    public LiveData<List<FullDeckComment>> getFullCommentsForLocalCardId(long localCardId) {
        return LiveDataHelper.interceptLiveData(db.getCommentDao().getFullCommentByLocalCardId(localCardId), (list) -> {
            for (FullDeckComment deckComment : list) {
                deckComment.getComment().setMentions(db.getMentionDao().getMentionsForCommentIdDirectly(deckComment.getLocalId()));
                if (deckComment.getParent() != null) {
                    deckComment.getParent().setMentions(db.getMentionDao().getMentionsForCommentIdDirectly(deckComment.getComment().getParentId()));
                }
            }
        });
    }

    @WorkerThread
    public DeckComment getCommentByRemoteIdDirectly(long accountId, Long remoteCommentId) {
        return db.getCommentDao().getCommentByRemoteIdDirectly(accountId, remoteCommentId);
    }

    @WorkerThread
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

    @WorkerThread
    public List<DeckComment> getLocallyChangedCommentsByLocalCardIdDirectly(long accountId, long localCardId) {
        return db.getCommentDao().getLocallyChangedCommentsByLocalCardIdDirectly(accountId, localCardId);
    }

    public void clearMentionsForCommentId(long commentID) {
        db.getMentionDao().clearMentionsForCommentId(commentID);
    }

    public long createMention(Mention mention) {
        return db.getMentionDao().insert(mention);
    }

    @WorkerThread
    public List<DeckComment> getCommentByLocalCardIdDirectly(Long localCardId) {
        return db.getCommentDao().getCommentByLocalCardIdDirectly(localCardId);
    }

    @WorkerThread
    public List<Card> getCardsWithLocallyChangedCommentsDirectly(Long accountId) {
        return db.getCardDao().getCardsWithLocallyChangedCommentsDirectly(accountId);
    }

    @WorkerThread
    public List<Card> getCardsWithLocallyChangedCommentsForStackDirectly(Long localStackId) {
        return db.getCardDao().getCardsWithLocallyChangedCommentsForStackDirectly(localStackId);
    }

    @WorkerThread
    public Long getLocalStackIdByRemoteStackIdDirectly(long accountId, Long stackId) {
        return db.getStackDao().getLocalStackIdByRemoteStackIdDirectly(accountId, stackId);
    }

    public LiveData<Long> getLocalBoardIdByCardRemoteIdAndAccountId(long cardRemoteId, long accountId) {
        return db.getBoardDao().getLocalBoardIdByCardRemoteIdAndAccountId(cardRemoteId, accountId);
    }

    @WorkerThread
    public void countCardsInStackDirectly(long accountId, long localStackId, @NonNull IResponseCallback<Integer> callback) {
        callback.onResponse(db.getCardDao().countCardsInStackDirectly(accountId, localStackId));
    }

    @WorkerThread
    public void countCardsWithLabel(long localLabelId, @NonNull IResponseCallback<Integer> callback) {
        callback.onResponse(db.getJoinCardWithLabelDao().countCardsWithLabelDirectly(localLabelId));
    }

    @WorkerThread
    public Label getLabelByBoardIdAndTitleDirectly(long boardId, String title) {
        return db.getLabelDao().getLabelByBoardIdAndTitleDirectly(boardId, title);
    }

    public LiveData<List<FullBoard>> getFullBoards(long accountId, boolean archived) {
        return db.getBoardDao().getArchivedFullBoards(accountId, (archived ? 1 : 0));
    }

    public LiveData<Boolean> hasArchivedBoards(long accountId) {
        return LiveDataHelper.postCustomValue(distinctUntilChanged(db.getBoardDao().countArchivedBoards(accountId)), data -> data != null && data > 0);
    }

    @WorkerThread
    public Long getRemoteCommentIdForLocalIdDirectly(Long localCommentId) {
        return db.getCommentDao().getRemoteCommentIdForLocalIdDirectly(localCommentId);
    }

    @WorkerThread
    public Long getLocalCommentIdForRemoteIdDirectly(long accountId, Long remoteCommentId) {
        return db.getCommentDao().getLocalCommentIdForRemoteIdDirectly(accountId, remoteCommentId);
    }


    // -------------------
    // Widgets
    // -------------------

    @WorkerThread
    public long createSingleCardWidget(int widgetId, long accountId, long boardLocalId, long cardLocalId) {
        final SingleCardWidgetModel model = new SingleCardWidgetModel();
        model.setWidgetId(widgetId);
        model.setAccountId(accountId);
        model.setBoardId(boardLocalId);
        model.setCardId(cardLocalId);
        return db.getSingleCardWidgetModelDao().insert(model);
    }

    public FullSingleCardWidgetModel getFullSingleCardWidgetModel(int widgetId) {
        final FullSingleCardWidgetModel model = db.getSingleCardWidgetModelDao().getFullCardByRemoteIdDirectly(widgetId);
        if (model != null) {
            model.setFullCard(db.getCardDao().getFullCardByLocalIdDirectly(model.getAccount().getId(), model.getModel().getCardId()));
        }
        return model;
    }

    public void deleteSingleCardWidget(int widgetId) {
        final SingleCardWidgetModel model = new SingleCardWidgetModel();
        model.setWidgetId(widgetId);
        db.getSingleCardWidgetModelDao().delete(model);
    }

    public void createStackWidget(int appWidgetId, long accountId, long stackId, boolean darkTheme) {
        final StackWidgetModel model = new StackWidgetModel();
        model.setAppWidgetId(appWidgetId);
        model.setAccountId(accountId);
        model.setStackId(stackId);
        model.setDarkTheme(darkTheme);

//        db.getStackWidgetModelDao().insert(model);
    }

    public StackWidgetModel getStackWidgetModelDirectly(int appWidgetId) {
//        return db.getStackWidgetModelDao().getStackWidgetByAppWidgetIdDirectly(appWidgetId);
        return null;
    }

    public int createFilterWidgetDirectly(@NonNull FilterWidget filterWidget) {
        db.getFilterWidgetDao().insert(filterWidget);
        insertFilterWidgetDecendants(filterWidget);
        return filterWidget.getId();
    }

    private void insertFilterWidgetDecendants(FilterWidget filterWidget) {
        final long widgetId = filterWidget.getId();
        for (FilterWidgetAccount account : filterWidget.getAccounts()) {
            account.setFilterWidgetId(widgetId);
            long accountId = db.getFilterWidgetAccountDao().insert(account);
            for (FilterWidgetUser user : account.getUsers()) {
                user.setFilterAccountId(accountId);
                db.getFilterWidgetUserDao().insert(user);
            }
            for (FilterWidgetProject project : account.getProjects()) {
                project.setFilterAccountId(accountId);
                db.getFilterWidgetProjectDao().insert(project);
            }
            for (FilterWidgetBoard board : account.getBoards()) {
                board.setFilterAccountId(accountId);
                final long boardId = db.getFilterWidgetBoardDao().insert(board);
                for (FilterWidgetStack stack : board.getStacks()) {
                    stack.setFilterBoardId(boardId);
                    db.getFilterWidgetStackDao().insert(stack);
                }
                for (FilterWidgetLabel label : board.getLabels()) {
                    label.setFilterBoardId(boardId);
                    db.getFilterWidgetLabelDao().insert(label);
                }
            }
        }
        for (FilterWidgetSort sort : filterWidget.getSorts()) {
            sort.setFilterWidgetId(widgetId);
            db.getFilterWidgetSortDao().insert(sort);
        }
    }

    public void deleteFilterWidgetDirectly(Integer filterWidgetId) {
        db.getFilterWidgetDao().delete(filterWidgetId);
    }

    public void updateFilterWidgetDirectly(FilterWidget filterWidget) {
        db.getFilterWidgetSortDao().deleteByFilterWidgetId(filterWidget.getId());
        db.getFilterWidgetAccountDao().deleteByFilterWidgetId(filterWidget.getId());
        db.getFilterWidgetDao().update(filterWidget);
        insertFilterWidgetDecendants(filterWidget);
    }

    public FilterWidget getFilterWidgetByIdDirectly(Integer filterWidgetId) {
        final FilterWidget filterWidget = db.getFilterWidgetDao().getFilterWidgetByIdDirectly(filterWidgetId);
        if (filterWidget == null) {
            throw new NoSuchElementException("No widget with id " + filterWidgetId + " configured.");
        }
        filterWidget.setSorts(db.getFilterWidgetSortDao().getFilterWidgetSortByFilterWidgetIdDirectly(filterWidgetId));
        filterWidget.setAccounts(db.getFilterWidgetAccountDao().getFilterWidgetAccountsByFilterWidgetIdDirectly(filterWidgetId));
        for (FilterWidgetAccount account : filterWidget.getAccounts()) {
            account.setBoards(db.getFilterWidgetBoardDao().getFilterWidgetBoardsByFilterWidgetAccountIdDirectly(account.getId()));
            account.setUsers(db.getFilterWidgetUserDao().getFilterWidgetUsersByFilterWidgetAccountIdDirectly(account.getId()));
            account.setProjects(db.getFilterWidgetProjectDao().getFilterWidgetProjectsByFilterWidgetAccountIdDirectly(account.getId()));
            for (FilterWidgetBoard board : account.getBoards()) {
                board.setLabels(db.getFilterWidgetLabelDao().getFilterWidgetLabelsByFilterWidgetBoardIdDirectly(board.getId()));
                board.setStacks(db.getFilterWidgetStackDao().getFilterWidgetStacksByFilterWidgetBoardIdDirectly(board.getId()));
            }
        }

        return filterWidget;
    }

    public LiveData<List<UpcomingCardsAdapterItem>> getCardsForUpcomingCard() {
        return LiveDataHelper.postCustomValue(db.getCardDao().getUpcomingCards(), this::cardResultsToUpcomingCardsAdapterItems);
    }

    public List<UpcomingCardsAdapterItem> getCardsForUpcomingCardForWidget() {
        return cardResultsToUpcomingCardsAdapterItems(db.getCardDao().getUpcomingCardsDirectly());
    }

    @NotNull
    private List<UpcomingCardsAdapterItem> cardResultsToUpcomingCardsAdapterItems(List<FullCard> cardsResult) {
        filterRelationsForCard(cardsResult);
        final List<UpcomingCardsAdapterItem> result = new ArrayList<>(cardsResult.size());
        final Map<Long, Account> accountCache = new HashMap<>();
        for (FullCard fullCard : cardsResult) {
            final Board board = db.getBoardDao().getBoardByLocalCardIdDirectly(fullCard.getLocalId());
            Account account = accountCache.get(fullCard.getAccountId());
            if (account == null) {
                account = db.getAccountDao().getAccountByIdDirectly(fullCard.getAccountId());
                accountCache.put(fullCard.getAccountId(), account);
            }
            result.add(new UpcomingCardsAdapterItem(fullCard, account, board.getLocalId(), board.getId(), board.isPermissionEdit()));
        }
        return result;
    }

    public List<FilterWidgetCard> getCardsForFilterWidget(@NonNull Integer filterWidgetId) {
        final FilterWidget filterWidget = getFilterWidgetByIdDirectly(filterWidgetId);
        final FilterInformation filter = new FilterInformation();
        final Set<FullCard> cardsResult = new HashSet<>();
        if (filterWidget.getDueType() != null) {
            filter.setDueType(filterWidget.getDueType());
        } else filter.setDueType(EDueType.NO_FILTER);

        if (filterWidget.getAccounts().isEmpty()) {
            cardsResult.addAll(db.getCardDao().getFilteredFullCardsForStackDirectly(getQueryForFilter(filter, null, null)));
        } else {
            for (FilterWidgetAccount account : filterWidget.getAccounts()) {
                filter.setNoAssignedUser(account.isIncludeNoUser());
                final List<User> users = new ArrayList<>();
                if (!account.getUsers().isEmpty()) {
                    for (FilterWidgetUser user : account.getUsers()) {
                        User u = new User();
                        u.setLocalId(user.getUserId());
                        users.add(u);
                    }
                }
                filter.setUsers(users);
                filter.setNoAssignedProject(account.isIncludeNoProject());
                final List<OcsProject> projects = new ArrayList<>();
                if (!account.getProjects().isEmpty()) {
                    for (FilterWidgetProject project : account.getProjects()) {
                        OcsProject u = new OcsProject();
                        u.setLocalId(project.getProjectId());
                        projects.add(u);
                    }
                }
                filter.setProjects(projects);
                if (!account.getBoards().isEmpty()) {
                    for (FilterWidgetBoard board : account.getBoards()) {
                        filter.setNoAssignedLabel(board.isIncludeNoLabel());
                        final List<Long> stacks;
                        for (FilterWidgetLabel label : board.getLabels()) {
                            Label l = new Label();
                            l.setLocalId(label.getLabelId());
                            filter.addLabel(l);
                        }
                        if (board.getStacks().isEmpty()) {
                            stacks = db.getStackDao().getLocalStackIdsByLocalBoardIdDirectly(board.getBoardId());
                        } else {
                            stacks = new ArrayList<>();
                            for (FilterWidgetStack stack : board.getStacks()) {
                                stacks.add(stack.getStackId());
                            }
                        }
                        cardsResult.addAll(db.getCardDao().getFilteredFullCardsForStackDirectly(getQueryForFilter(filter, Collections.singletonList(account.getAccountId()), stacks)));
                    }
                } else {
                    cardsResult.addAll(db.getCardDao().getFilteredFullCardsForStackDirectly(getQueryForFilter(filter, Collections.singletonList(account.getAccountId()), null)));
                }
            }
        }

        handleWidgetTypeExtras(filterWidget, cardsResult);

        filterRelationsForCard(cardsResult);

        final List<FilterWidgetCard> result = new ArrayList<>(cardsResult.size());
        final Map<Long, Board> boardCache = new HashMap<>();
        final Map<Long, Stack> stackCache = new HashMap<>();
        for (FullCard fullCard : cardsResult) {
            final Long stackId = fullCard.getCard().getStackId();
            Stack stack = stackCache.get(stackId);
            if (stack == null) {
                stack = db.getStackDao().getStackByLocalIdDirectly(stackId);
                stackCache.put(stackId, stack);
            }

            Board board = boardCache.get(stack.getBoardId());
            if (board == null) {
                board = db.getBoardDao().getBoardByLocalIdDirectly(stackId);
                boardCache.put(stackId, board);
            }
            result.add(new FilterWidgetCard(fullCard, stack, board));
        }
        return result;
    }

    private void handleWidgetTypeExtras(FilterWidget filterWidget, Collection<FullCard> cardsResult) {
        if (filterWidget.getWidgetType() == EWidgetType.UPCOMING_WIDGET) {
            // https://github.com/stefan-niedermann/nextcloud-deck/issues/819 "no due" cards are only shown if they are on a shared board
            for (FullCard fullCard : new ArrayList<>(cardsResult)) {
                if (fullCard.getCard().getDueDate() == null && !db.getStackDao().isStackOnSharedBoardDirectly(fullCard.getCard().getStackId())) {
                    cardsResult.remove(fullCard);
                }
            }
            List<Long> accountIds = null;
            if (!filterWidget.getAccounts().isEmpty()) {
                accountIds = filterWidget.getAccounts().stream().map(FilterWidgetAccount::getAccountId).collect(Collectors.toList());
            }
            // https://github.com/stefan-niedermann/nextcloud-deck/issues/822 exclude archived cards and boards
            final List<Long> archivedStacks = db.getStackDao().getLocalStackIdsInArchivedBoardsByAccountIdsDirectly(accountIds);
            for (Long archivedStack : archivedStacks) {
                final List<FullCard> archivedCards = cardsResult.stream()
                        .filter(c -> c.getCard().isArchived() || archivedStack.equals(c.getCard().getStackId()))
                        .collect(Collectors.toList());
                cardsResult.removeAll(archivedCards);
            }
            // https://github.com/stefan-niedermann/nextcloud-deck/issues/800 all cards within non-shared boards need to be included
            cardsResult.addAll(db.getCardDao().getFullCardsForNonSharedBoardsWithDueDateForUpcomingCardsWidgetDirectly(accountIds));
        }
    }

    public List<FilterWidget> getFilterWidgetsByType(EWidgetType type) {
        final List<Integer> ids = db.getFilterWidgetDao().getFilterWidgetIdsByType(type.getId());
        final List<FilterWidget> widgets = new ArrayList<>(ids.size());
        for (Integer id : ids) {
            widgets.add(getFilterWidgetByIdDirectly(id));
        }
        return widgets;
    }

    public boolean filterWidgetExists(int id) {
        return db.getFilterWidgetDao().filterWidgetExists(id);
    }

    public void deleteStackWidget(int appWidgetId) {
        final StackWidgetModel model = new StackWidgetModel();
        model.setAppWidgetId(appWidgetId);
//        db.getStackWidgetModelDao().delete(model);
    }

    public LiveData<List<Account>> readAccountsForHostWithReadAccessToBoard(String host, long boardRemoteId) {
        return db.getAccountDao().readAccountsForHostWithReadAccessToBoard("%" + host + "%", boardRemoteId);
    }

    public List<Account> readAccountsForHostWithReadAccessToBoardDirectly(String host, long boardRemoteId) {
        return db.getAccountDao().readAccountsForHostWithReadAccessToBoardDirectly("%" + host + "%", boardRemoteId);
    }

    public Board getBoardForAccountByNameDirectly(long account, String title) {
        return db.getBoardDao().getBoardForAccountByNameDirectly(account, title);
    }

    public OcsProject getProjectByRemoteIdDirectly(long accountId, Long remoteId) {
        return db.getOcsProjectDao().getProjectByRemoteIdDirectly(accountId, remoteId);
    }

    public Long createProjectDirectly(long accountId, OcsProject entity) {
        entity.setAccountId(accountId);
        return db.getOcsProjectDao().insert(entity);
    }

    public void deleteProjectResourcesForProjectIdDirectly(Long localProjectId) {
        db.getOcsProjectResourceDao().deleteByProjectId(localProjectId);
    }

    public void updateProjectDirectly(long accountId, OcsProject entity) {
        entity.setAccountId(accountId);
        db.getOcsProjectDao().update(entity);
    }

    public void deleteProjectDirectly(OcsProject ocsProject) {
        db.getOcsProjectResourceDao().deleteByProjectId(ocsProject.getLocalId());
        db.getOcsProjectDao().delete(ocsProject);
    }

    public Long createProjectResourceDirectly(Long accountId, OcsProjectResource resource) {
        resource.setAccountId(accountId);
        return db.getOcsProjectResourceDao().insert(resource);
    }

    public int countProjectResourcesInProjectDirectly(Long projectLocalId) {
        return db.getOcsProjectResourceDao().countProjectResourcesInProjectDirectly(projectLocalId);
    }

    public LiveData<Integer> countProjectResourcesInProject(Long projectLocalId) {
        return db.getOcsProjectResourceDao().countProjectResourcesInProject(projectLocalId);
    }

    public LiveData<List<OcsProjectResource>> getResourcesByLocalProjectId(Long projectLocalId) {
        return db.getOcsProjectResourceDao().getResourcesByLocalProjectId(projectLocalId);
    }

    public void assignCardToProjectIfMissng(Long accountId, Long localProjectId, Long remoteCardId) {
        final Card card = db.getCardDao().getCardByRemoteIdDirectly(accountId, remoteCardId);
        if (card != null) {
            final JoinCardWithProject existing = db.getJoinCardWithOcsProjectDao().getAssignmentByCardIdAndProjectIdDirectly(card.getLocalId(), localProjectId);
            if (existing == null) {
                final JoinCardWithProject assignment = new JoinCardWithProject();
                assignment.setStatus(DBStatus.UP_TO_DATE.getId());
                assignment.setCardId(card.getLocalId());
                assignment.setProjectId(localProjectId);
                final long id = db.getJoinCardWithOcsProjectDao().insert(assignment);
                notifyFilterWidgetsAboutChangedEntity(FilterWidget.EChangedEntityType.PROJECT, id);
            }
        }
    }

    private void notifyFilterWidgetsAboutChangedEntity(@NonNull FilterWidget.EChangedEntityType type, Long entityId) {
        widgetNotifierExecutor.submit(() -> {
            final List<EWidgetType> widgetTypesToNotify = db.getFilterWidgetDao().getChangedListTypesByEntity(type.toString(), entityId);
            for (EWidgetType t : widgetTypesToNotify) {
                DeckLog.info("Notifying", t.getWidgetClass().getSimpleName(), "about entity change:", type.name(), "with ID", entityId);
                context.sendBroadcast(new Intent(context, t.getWidgetClass()).setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE));
            }
        });
    }

    private void notifyAllWidgets() {
        widgetNotifierExecutor.submit(() -> SingleCardWidget.notifyDatasetChanged(context));
        /// FIXME StackWidget.notifyDatasetChanged(context);
//        UpcomingWidget.notifyDatasetChanged(context);
    }

    @ColorInt
    public Integer getBoardColorDirectly(long accountId, long localBoardId) {
        return db.getBoardDao().getBoardColorByLocalIdDirectly(accountId, localBoardId);
    }

    public void deleteProjectResourcesByCardIdExceptGivenProjectIdsDirectly(long accountId, Long localCardId, List<Long> remoteProjectIDs) {
        db.getJoinCardWithOcsProjectDao().deleteProjectResourcesByCardIdExceptGivenProjectIdsDirectly(accountId, localCardId, remoteProjectIDs);
    }

    public void deleteProjectResourcesByCardIdDirectly(Long localCardId) {
        db.getJoinCardWithOcsProjectDao().deleteProjectResourcesByCardIdDirectly(localCardId);
    }
}
