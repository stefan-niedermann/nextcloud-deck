package it.niedermann.nextcloud.deck.deprecated.database;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.stream.Collectors.toList;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteConstraintException;
import android.text.TextUtils;

import androidx.annotation.AnyThread;
import androidx.annotation.ColorInt;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.annotation.VisibleForTesting;
import androidx.annotation.WorkerThread;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.preference.PreferenceManager;
import androidx.sqlite.db.SimpleSQLiteQuery;

import com.nextcloud.android.sso.helper.SingleAccountHelper;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.rxjava3.core.Flowable;
import it.niedermann.android.reactivelivedata.ReactiveLiveData;
import it.niedermann.android.sharedpreferences.SharedPreferenceLongLiveData;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.feature.shared.SharedExecutors;
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
import it.niedermann.nextcloud.deck.model.enums.EDoneType;
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
import it.niedermann.nextcloud.deck.model.ocs.user.UserForAssignment;
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
import it.niedermann.nextcloud.deck.remote.api.IResponseCallback;
import it.niedermann.nextcloud.deck.ui.upcomingcards.UpcomingCardsAdapterItem;
import it.niedermann.nextcloud.deck.ui.widget.singlecard.SingleCardWidget;

public class DataBaseAdapter {
    @NonNull
    private final DeckDatabase db;
    @NonNull
    private final Context context;
    @NonNull
    private final ExecutorService widgetNotifierExecutor;
    @NonNull
    private final ExecutorService executor;
    private static final Long NOT_AVAILABLE = -1L;
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor sharedPreferencesEditor;
    @ColorInt
    private final int defaultColor;

    public DataBaseAdapter(@NonNull Context appContext) {
        this(appContext, DeckDatabase.getInstance(appContext),Executors.newCachedThreadPool(), SharedExecutors.getLinkedBlockingQueueExecutor());
    }

    @VisibleForTesting
    protected DataBaseAdapter(@NonNull Context applicationContext,
                              @NonNull DeckDatabase db,
                              @NonNull ExecutorService widgetNotifierExecutor,
                              @NonNull ExecutorService executor) {
        this.context = applicationContext;
        this.db = db;
        this.widgetNotifierExecutor = widgetNotifierExecutor;
        this.executor = executor;
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.sharedPreferencesEditor = this.sharedPreferences.edit();
        this.defaultColor = ContextCompat.getColor(context, R.color.defaultBrand);
    }

    @NonNull
    public Context getContext() {
        return context;
    }

    public void runInTransaction(Runnable runnable) {
        this.db.runInTransaction(runnable);
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

    public Flowable<Boolean> hasAnyAccounts() {
        return db.getAccountDao().hasAccounts();
    }

    public LiveData<Boolean> hasAccounts() {
        return new ReactiveLiveData<>(db.getAccountDao().countAccounts())
                .distinctUntilChanged()
                .map(count -> count != null && count > 0);
    }

    public LiveData<Board> getBoardByRemoteId(long accountId, long remoteId) {
        return new ReactiveLiveData<>(db.getBoardDao().getBoardByRemoteId(accountId, remoteId))
                .distinctUntilChanged();
    }

    @WorkerThread
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
        return new ReactiveLiveData<>(db.getStackDao().getStackByRemoteId(accountId, localBoardId, remoteId))
                .distinctUntilChanged();
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
        return new ReactiveLiveData<>(db.getCardDao().getCardByRemoteId(accountId, remoteId))
                .distinctUntilChanged();
    }

    @WorkerThread
    public Card getCardByRemoteIDDirectly(long accountId, long remoteId) {
        return db.getCardDao().getCardByRemoteIdDirectly(accountId, remoteId);
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

    public LiveData<List<FullCard>> getFullCardsForStack(long accountId, long localStackId, @Nullable FilterInformation filter) {
        return new ReactiveLiveData<>(
                FilterInformation.hasActiveFilter(filter)
                        ? db.getCardDao().getFilteredFullCardsForStack(getQueryForFilter(filter, accountId, localStackId))
                        : db.getCardDao().getFullCardsForStack(accountId, localStackId))
                .tap(this::filterRelationsForCard, executor)
                .distinctUntilChanged();

    }

    private void fillSqlWithEntityListValues(StringBuilder query, Collection<Object> args, @NonNull List<? extends IRemoteEntity> entities) {
        List<Long> idList = entities.stream().map(IRemoteEntity::getLocalId).collect(toList());
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
        return FilterInformation.hasActiveFilter(filter)
                ? db.getCardDao().getFilteredFullCardsForStackDirectly(getQueryForFilter(filter, accountId, localStackId))
                : db.getCardDao().getFullCardsForStackDirectly(accountId, localStackId);
    }

    @AnyThread
    private SimpleSQLiteQuery getQueryForFilter(FilterInformation filter, long accountId, long localStackId) {
        return getQueryForFilter(filter, singletonList(accountId), singletonList(localStackId));
    }

    @AnyThread
    private SimpleSQLiteQuery getQueryForFilter(@NonNull FilterInformation filter, @NonNull List<Long> accountIds, @NonNull List<Long> localStackIds) {
        final Collection<Object> args = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT * FROM card c WHERE 1=1 ");
        if (!accountIds.isEmpty()) {
            query.append("and accountId in (");
            fillSqlWithListValues(query, args, accountIds);
            query.append(") ");
        }
        if (!localStackIds.isEmpty()) {
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
                case EDueType.NO_DUE -> query.append("and c.dueDate is null ");
                case EDueType.OVERDUE ->
                        query.append("and datetime(c.duedate/1000, 'unixepoch', 'localtime') <= datetime('now', 'localtime') ");
                case EDueType.TODAY ->
                        query.append("and datetime(c.duedate/1000, 'unixepoch', 'localtime') between datetime('now', 'localtime') and datetime('now', '+24 hour', 'localtime') ");
                case EDueType.WEEK ->
                        query.append("and datetime(c.duedate/1000, 'unixepoch', 'localtime') between datetime('now', 'localtime') and datetime('now', '+7 day', 'localtime') ");
                case EDueType.MONTH ->
                        query.append("and datetime(c.duedate/1000, 'unixepoch', 'localtime') between datetime('now', 'localtime') and datetime('now', '+30 day', 'localtime') ");
                default ->
                        throw new IllegalArgumentException("You need to add your new " + EDueType.class.getSimpleName() + " value\"" + filter.getDueType() + "\" here!");
            }
        }

        if (filter.getDoneType() != EDoneType.NO_FILTER) {
            switch (filter.getDoneType()) {
                case EDoneType.DONE -> query.append("and (c.done is not null and c.done != 0) ");
                case EDoneType.UNDONE -> query.append("and (c.done is null or c.done = 0) ");
                default ->
                        throw new IllegalArgumentException("You need to add your new " + EDoneType.class.getSimpleName() + " value\"" + filter.getDueType() + "\" here!");
            }
        }

        if (!TextUtils.isEmpty(filter.getFilterText())) {
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

    @WorkerThread
    public UserForAssignment getUserForAssignmentDirectly(long localUserId) {
        SimpleSQLiteQuery query = new SimpleSQLiteQuery(
                "SELECT u.type as type, u.uid as userId " +
                        "FROM User u " +
                        " WHERE u.localId = ? LIMIT 1",
                new Object[]{localUserId});
        return db.getUserInGroupDao().getUserForAssignment(query);
    }

    @UiThread
    public LiveData<Label> getLabelByRemoteId(long accountId, long remoteId) {
        return new ReactiveLiveData<>(db.getLabelDao().getLabelByRemoteId(accountId, remoteId))
                .distinctUntilChanged();
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

    public void deleteJoinedUsersForCardsInBoardWithoutPermissionPhysically(long localBoardId) {
        db.getJoinCardWithUserDao().deleteJoinedUsersForCardsInBoardWithoutPermissionPhysically(localBoardId);
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
        try {
            db.getUserInBoardDao().insert(relation);
        } catch (SQLiteConstraintException e) {
            // do nothing, since link already exists (→ only constraint that can fail: unique board ↔ user)
        }
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
            DeckLog.verbose("Adding new created", Account.class.getSimpleName(), "with", id, "to all instances of", EWidgetType.UPCOMING_WIDGET.name());
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
        return new ReactiveLiveData<>(db.getAccountDao().getAccountById(id))
                .tap(account -> account.setUserDisplayName(db.getUserDao().getUserNameByUidDirectly(account.getId(), account.getUserName())), executor)
                .distinctUntilChanged();
    }

    @UiThread
    public LiveData<Account> readAccount(String name) {
        return new ReactiveLiveData<>(db.getAccountDao().getAccountByName(name))
                .tap(account -> account.setUserDisplayName(db.getUserDao().getUserNameByUidDirectly(account.getId(), account.getUserName())), executor)
                .distinctUntilChanged();
    }

    @UiThread
    public LiveData<List<Account>> readAccounts() {
        return new ReactiveLiveData<>(db.getAccountDao().getAllAccounts())
                .tap(accounts -> accounts.forEach(account -> account.setUserDisplayName(db.getUserDao().getUserNameByUidDirectly(account.getId(), account.getUserName()))), executor)
                .distinctUntilChanged();
    }

    public LiveData<Integer> getAccountColor(long accountId) {
        return new ReactiveLiveData<>(db.getAccountDao().getAccountColor(accountId))
                .distinctUntilChanged()
                .map(color -> color == null ? defaultColor : color);
    }

    @WorkerThread
    public Account readAccountDirectly(long id) {
        return db.getAccountDao().getAccountByIdDirectly(id);
    }

    @WorkerThread
    public Account readAccountDirectly(@Nullable String name) {
        final var account = db.getAccountDao().getAccountByNameDirectly(name);
        account.setUserDisplayName(db.getUserDao().getUserNameByUidDirectly(account.getId(), account.getUserName()));
        return account;
    }

    public LiveData<List<Board>> getBoards(long accountId, boolean archived) {
        return new ReactiveLiveData<>(db.getBoardDao().getNotDeletedBoards(accountId, archived ? 1 : 0))
                .distinctUntilChanged();
    }

    public LiveData<List<Board>> getBoardsWithEditPermission(long accountId) {
        return new ReactiveLiveData<>(db.getBoardDao().getBoardsWithEditPermissionsForAccount(accountId))
                .distinctUntilChanged();
    }

    @WorkerThread
    public long createBoardDirectly(long accountId, @NonNull Board board) {
        board.setAccountId(accountId);
        final long id = db.getBoardDao().insert(board);
        notifyFilterWidgetsAboutChangedEntity(FilterWidget.EChangedEntityType.BOARD, id);
        return id;
    }

    public void deleteBoard(@NonNull Board board, boolean setStatus) {
        markAsDeletedIfNeeded(board, setStatus);
        db.getBoardDao().update(board);
        notifyAllWidgets();
        notifyFilterWidgetsAboutChangedEntity(FilterWidget.EChangedEntityType.BOARD, board.getLocalId());
    }

    public void deleteBoardPhysically(@NonNull Board board) {
        db.getBoardDao().delete(board);
        notifyAllWidgets();
    }

    public void updateBoard(Board board, boolean setStatus) {
        markAsEditedIfNeeded(board, setStatus);
        db.getBoardDao().update(board);
        notifyFilterWidgetsAboutChangedEntity(FilterWidget.EChangedEntityType.BOARD, board.getLocalId());
    }

    public LiveData<List<Stack>> getStacksForBoard(long accountId, long localBoardId) {
        return new ReactiveLiveData<>(db.getStackDao().getStacksForBoard(accountId, localBoardId))
                .distinctUntilChanged();
    }

    @WorkerThread
    public List<FullStack> getFullStacksForBoardDirectly(long accountId, long localBoardId) {
        return db.getStackDao().getFullStacksForBoardDirectly(accountId, localBoardId);
    }

    @MainThread
    public LiveData<FullStack> getStack(long accountId, long localStackId) {
        return new ReactiveLiveData<>(db.getStackDao().getFullStack(accountId, localStackId))
                .distinctUntilChanged();
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
        return new ReactiveLiveData<>(db.getCardDao().getFullCardByLocalId(accountId, localCardId))
                .tap(this::filterRelationsForCard, executor)
                .distinctUntilChanged();
    }

    @AnyThread
    public LiveData<FullCardWithProjects> getCardWithProjectsByLocalId(long accountId, long localCardId) {
        return new ReactiveLiveData<>(db.getCardDao().getFullCardWithProjectsByLocalId(accountId, localCardId))
                .tap(this::filterRelationsForCard, executor)
                .distinctUntilChanged();
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
        return new ReactiveLiveData<>(db.getAccessControlDao().getAccessControlByLocalBoardId(accountId, localBoardId))
                .tap(this::readRelationsForACL, executor)
                .distinctUntilChanged();
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

    public void deleteAccessControlsForBoardWhereLocalIdsNotInDirectly(Long localBoardId, Set<Long> idsToKeep) {
        db.getAccessControlDao().deleteAccessControlsForBoardWhereLocalIdsNotInDirectly(localBoardId, idsToKeep);
        notifyFilterWidgetsAboutChangedEntity(FilterWidget.EChangedEntityType.BOARD, localBoardId);
    }

    public LiveData<FullBoard> getFullBoardById(Long accountId, Long localId) {
        return new ReactiveLiveData<>(db.getBoardDao().getFullBoardById(accountId, localId))
                .distinctUntilChanged();
    }

    @WorkerThread
    public Board getBoardByLocalIdDirectly(long localId) {
        return db.getBoardDao().getBoardByLocalIdDirectly(localId);
    }

    public LiveData<User> getUserByLocalId(long accountId, long localId) {
        return new ReactiveLiveData<>(db.getUserDao().getUserByLocalId(accountId, localId))
                .distinctUntilChanged();
    }

    public LiveData<User> getUserByUid(long accountId, String uid) {
        return new ReactiveLiveData<>(db.getUserDao().getUserByUid(accountId, uid))
                .distinctUntilChanged();
    }

    public LiveData<List<User>> getUsersForAccount(final long accountId) {
        return new ReactiveLiveData<>(db.getUserDao().getUsersForAccount(accountId))
                .distinctUntilChanged();
    }

    public List<User> getAllUsersDirectly() {
        return db.getUserDao().getAllUsersDirectly();
    }

    public LiveData<List<User>> searchUserByUidOrDisplayName(final long accountId, final long boardId, final long notYetAssignedToLocalCardId, final String searchTerm) {
        validateSearchTerm(searchTerm);
        return new ReactiveLiveData<>(db.getUserDao().searchUserByUidOrDisplayName(accountId, boardId, notYetAssignedToLocalCardId, "%" + searchTerm.trim() + "%"))
                .distinctUntilChanged();
    }

    public LiveData<List<User>> searchUserByUidOrDisplayNameForACL(final long accountId, final long notYetAssignedToACL, final String searchTerm) {
        validateSearchTerm(searchTerm);
        return db.getUserDao().searchUserByUidOrDisplayNameForACL(accountId, notYetAssignedToACL, "%" + searchTerm.trim() + "%");
    }

    public LiveData<List<Label>> searchNotYetAssignedLabelsByTitle(final long accountId, final long boardId, final long notYetAssignedToLocalCardId, String searchTerm) {
        validateSearchTerm(searchTerm);
        return new ReactiveLiveData<>(db.getLabelDao().searchNotYetAssignedLabelsByTitle(accountId, boardId, notYetAssignedToLocalCardId, "%" + searchTerm.trim() + "%"))
                .distinctUntilChanged();
    }

    /**
     * Search all {@link FullCard}s grouped by {@link Stack}s which contain the term in {@link Card#getTitle()} or {@link Card#getDescription()}.
     * {@link Stack}s are sorted by {@link Stack#getOrder()}, {@link Card}s for each {@link Stack} are sorted by {@link Card#getOrder()}.
     */
    public LiveData<Map<Stack, List<FullCard>>> searchCards(final long accountId, final long localBoardId, @NonNull String term, int limitPerStack) {
        String sqlSearchTerm = term.trim();
        if (sqlSearchTerm.isEmpty()) {
            throw new IllegalArgumentException("empty search term");
        }
        sqlSearchTerm = "%" + sqlSearchTerm + "%";


        return new ReactiveLiveData<>(db.getCardDao().searchCard(accountId, localBoardId, sqlSearchTerm))
                .map(result -> mapToStacksForCardSearch(result, limitPerStack), executor);
    }

    private Map<Stack, List<FullCard>> mapToStacksForCardSearch(List<FullCard> matches, int limitPerStack) {
        Map<Stack, List<FullCard>> result = new HashMap<>();
        if (matches != null && !matches.isEmpty()) {
            // results are sorted by stack -> jackpot:
            Stack lastStack = null;
            for (FullCard card : matches) {
                // find right bucket
                if (lastStack == null || !Objects.equals(lastStack.getLocalId(), card.getCard().getStackId())) {
                    lastStack = db.getStackDao().getStackByLocalIdDirectly(card.getCard().getStackId());
                }
                // check if bucket exists
                List<FullCard> fullCards = result.computeIfAbsent(lastStack, k -> new ArrayList<>());
                // create bucket
                if (fullCards.size() < limitPerStack) {
                    // put into bucket
                    fullCards.add(card);
                }
            }
        }
        return result;
    }

    public LiveData<List<User>> findProposalsForUsersToAssign(final long accountId, long boardId, long notAssignedToLocalCardId, final int topX) {
        return new ReactiveLiveData<>(db.getUserDao().findProposalsForUsersToAssign(accountId, boardId, notAssignedToLocalCardId, topX))
                .distinctUntilChanged();
    }

    public LiveData<List<User>> findProposalsForUsersToAssignForACL(final long accountId, long boardId, final int topX) {
        return new ReactiveLiveData<>(db.getUserDao().findProposalsForUsersToAssignForACL(accountId, boardId, topX))
                .distinctUntilChanged();
    }

    public LiveData<List<Label>> findProposalsForLabelsToAssign(final long accountId, final long boardId, long notAssignedToLocalCardId) {
        return new ReactiveLiveData<>(db.getLabelDao().findProposalsForLabelsToAssign(accountId, boardId, notAssignedToLocalCardId))
                .distinctUntilChanged();
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
        return new ReactiveLiveData<>(db.getLabelDao().getLabelByLocalId(localLabelId))
                .distinctUntilChanged();
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
        return new ReactiveLiveData<>(db.getActivityDao().getActivitiesForCard(localCardId))
                .distinctUntilChanged();
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
        return new ReactiveLiveData<>(db.getCommentDao().getCommentByLocalCardId(localCardId))
                .tap(list -> list.forEach(comment -> comment.setMentions(db.getMentionDao().getMentionsForCommentIdDirectly(comment.getLocalId()))), executor)
                .distinctUntilChanged();
    }

    public LiveData<List<FullDeckComment>> getFullCommentsForLocalCardId(long localCardId) {
        return new ReactiveLiveData<>(db.getCommentDao().getFullCommentByLocalCardId(localCardId))
                .tap(list -> {
                    for (FullDeckComment deckComment : list) {
                        deckComment.getComment().setMentions(db.getMentionDao().getMentionsForCommentIdDirectly(deckComment.getLocalId()));
                        if (deckComment.getParent() != null) {
                            deckComment.getParent().setMentions(db.getMentionDao().getMentionsForCommentIdDirectly(deckComment.getComment().getParentId()));
                        }
                    }
                }, executor)
                .distinctUntilChanged();
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
        return new ReactiveLiveData<>(db.getBoardDao().getLocalBoardIdByCardRemoteIdAndAccountId(cardRemoteId, accountId))
                .distinctUntilChanged();
    }

    @WorkerThread
    public Long getBoardLocalIdByAccountAndCardRemoteIdDirectly(long accountId, long cardRemoteId) {
        return db.getBoardDao().getBoardLocalIdByAccountAndCardRemoteIdDirectly(accountId, cardRemoteId);
    }

    @WorkerThread
    public void countCardsInStackDirectly(long accountId, long localStackId, @NonNull IResponseCallback<Integer> callback) {
        callback.onResponse(db.getCardDao().countCardsInStackDirectly(accountId, localStackId), IResponseCallback.EMPTY_HEADERS);
    }

    @WorkerThread
    public void countCardsWithLabel(long localLabelId, @NonNull IResponseCallback<Integer> callback) {
        callback.onResponse(db.getJoinCardWithLabelDao().countCardsWithLabelDirectly(localLabelId), IResponseCallback.EMPTY_HEADERS);
    }

    @WorkerThread
    public Label getLabelByBoardIdAndTitleDirectly(long boardId, String title) {
        return db.getLabelDao().getLabelByBoardIdAndTitleDirectly(boardId, title);
    }

    public LiveData<List<FullBoard>> getFullBoards(long accountId, boolean archived) {
        return new ReactiveLiveData<>(db.getBoardDao().getNotDeletedFullBoards(accountId, archived ? 1 : 0))
                .distinctUntilChanged();
    }

    public LiveData<Boolean> hasArchivedBoards(long accountId) {
        return new ReactiveLiveData<>(db.getBoardDao().countArchivedBoards(accountId))
                .distinctUntilChanged()
                .map(hasArchivedBoards -> hasArchivedBoards != null && hasArchivedBoards > 0);
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
        return new ReactiveLiveData<>(db.getCardDao().getUpcomingCards())
                .map(this::cardResultsToUpcomingCardsAdapterItems, executor)
                .distinctUntilChanged();
    }

    public List<UpcomingCardsAdapterItem> getCardsForUpcomingCardForWidget() {
        return cardResultsToUpcomingCardsAdapterItems(db.getCardDao().getUpcomingCardsDirectly());
    }

    @NonNull
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
            cardsResult.addAll(db.getCardDao().getFilteredFullCardsForStackDirectly(getQueryForFilter(filter, emptyList(), emptyList())));
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
                        final List<Long> stacks = new ArrayList<>();
                        for (FilterWidgetLabel label : board.getLabels()) {
                            Label l = new Label();
                            l.setLocalId(label.getLabelId());
                            filter.addLabel(l);
                        }
                        if (board.getStacks().isEmpty()) {
                            stacks.addAll(db.getStackDao().getLocalStackIdsByLocalBoardIdDirectly(board.getBoardId()));
                        } else {
                            stacks.addAll(board.getStacks().stream().map(FilterWidgetStack::getStackId).collect(toList()));
                        }
                        cardsResult.addAll(db.getCardDao().getFilteredFullCardsForStackDirectly(getQueryForFilter(filter, singletonList(account.getAccountId()), stacks)));
                    }
                } else {
                    cardsResult.addAll(db.getCardDao().getFilteredFullCardsForStackDirectly(getQueryForFilter(filter, singletonList(account.getAccountId()), emptyList())));
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
            cardsResult.removeIf(fullCard -> fullCard.getCard().getDueDate() == null && !db.getStackDao().isStackOnSharedBoardDirectly(fullCard.getCard().getStackId()));
            List<Long> accountIds = null;
            if (!filterWidget.getAccounts().isEmpty()) {
                accountIds = filterWidget.getAccounts().stream().map(FilterWidgetAccount::getAccountId).collect(toList());
            }
            // https://github.com/stefan-niedermann/nextcloud-deck/issues/822 exclude archived cards and boards
            final List<Long> archivedStacks = db.getStackDao().getLocalStackIdsInArchivedBoardsByAccountIdsDirectly(accountIds);
            for (Long archivedStack : archivedStacks) {
                final List<FullCard> archivedCards = cardsResult.stream()
                        .filter(c -> c.getCard().isArchived() || archivedStack.equals(c.getCard().getStackId()))
                        .collect(toList());
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
        return new ReactiveLiveData<>(db.getAccountDao().readAccountsForHostWithReadAccessToBoard("%" + host + "%", boardRemoteId))
                .distinctUntilChanged();
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
        return new ReactiveLiveData<>(db.getOcsProjectResourceDao().countProjectResourcesInProject(projectLocalId))
                .distinctUntilChanged();
    }

    public LiveData<List<OcsProjectResource>> getResourcesByLocalProjectId(Long projectLocalId) {
        return new ReactiveLiveData<>(db.getOcsProjectResourceDao().getResourcesByLocalProjectId(projectLocalId))
                .distinctUntilChanged();
    }

    public void assignCardToProjectIfMissing(Long accountId, Long localProjectId, Long remoteCardId) {
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

    public LiveData<Integer> getBoardColor$(long accountId, long localBoardId) {
        return new ReactiveLiveData<>(db.getBoardDao().getBoardColor(accountId, localBoardId))
                .map(color -> color == null ? defaultColor : color)
                .distinctUntilChanged();
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

    // =============================================================================================
    // APP STATE
    // TODO last boards and stacks per account should be moved to a table to benefit from cascading
    // =============================================================================================

    // ---------------
    // Current account
    // ---------------

    public void saveCurrentAccount(@NonNull Account account) {
        executor.submit(() -> {
            // Glide Module depends on correct account being set.
            // TODO Use SingleSignOnURL where possible, allow passing ssoAccountName to MarkdownEditor
            SingleAccountHelper.commitCurrentAccount(context, account.getName());

            DeckLog.log("--- Write:", context.getString(R.string.shared_preference_last_account), "→", account.getId());
            sharedPreferencesEditor.putLong(context.getString(R.string.shared_preference_last_account), account.getId());
            sharedPreferencesEditor.apply();
        });
    }

    public void removeCurrentAccount() {
        executor.submit(() -> {
            // Glide Module depends on correct account being set.
            // TODO Use SingleSignOnURL where possible, allow passing ssoAccountName to MarkdownEditor
            SingleAccountHelper.commitCurrentAccount(context, null);

            DeckLog.log("--- Remove:", context.getString(R.string.shared_preference_last_account));
            sharedPreferencesEditor.remove(context.getString(R.string.shared_preference_last_account));
            sharedPreferencesEditor.apply();
        });
    }

    public LiveData<Long> getCurrentAccountId$() {
        return new ReactiveLiveData<>(new SharedPreferenceLongLiveData(sharedPreferences, this.context.getString(R.string.shared_preference_last_account), NOT_AVAILABLE))
                .distinctUntilChanged()
                .tap(accountId -> {
                    DeckLog.log("--- Read:", context.getString(R.string.shared_preference_last_account), "→", accountId);
                    if (NOT_AVAILABLE.equals(accountId)) {
                        executor.submit(this::removeCurrentAccount);
                    }
                });
    }

    public CompletableFuture<Long> getCurrentAccountId() {
        return supplyAsync(() -> {
            final long accountId = sharedPreferences.getLong(context.getString(R.string.shared_preference_last_account), NOT_AVAILABLE);
            DeckLog.log("--- Read:", context.getString(R.string.shared_preference_last_account), "→", accountId);

            if (NOT_AVAILABLE.equals(accountId)) {
                saveNeighbourOfAccount(NOT_AVAILABLE);
                throw new CompletionException(new IllegalStateException("No current account ID set"));
            }

            return accountId;
        }, executor);
    }

    @WorkerThread
    public void saveNeighbourOfAccount(long currentAccountId) {
        getAllAccountsDirectly()
                .stream()
                .filter(account -> currentAccountId != account.getId())
                .findFirst()
                .ifPresentOrElse(this::saveCurrentAccount, this::removeCurrentAccount);
    }

    @ColorInt
    public CompletableFuture<Integer> getCurrentAccountColor(long accountId) {
        return supplyAsync(() -> db.getAccountDao().getAccountColorDirectly(accountId), executor)
                .thenApplyAsync(color -> color == null ? defaultColor : color, executor);
    }

    // -------------
    // Current board
    // -------------

    public void saveCurrentBoardId(long accountId, long boardId) {
        DeckLog.log("--- Write:", context.getString(R.string.shared_preference_last_board_for_account_) + accountId, "→", boardId);
        sharedPreferencesEditor.putLong(context.getString(R.string.shared_preference_last_board_for_account_) + accountId, boardId);
        sharedPreferencesEditor.apply();
    }

    public void removeCurrentBoardId(long accountId) {
        DeckLog.log("--- Remove:", context.getString(R.string.shared_preference_last_board_for_account_) + accountId);
        sharedPreferencesEditor.remove(context.getString(R.string.shared_preference_last_board_for_account_) + accountId);
        sharedPreferencesEditor.apply();
    }

    public LiveData<Long> getCurrentBoardId$(long accountId) {
        return new ReactiveLiveData<>(new SharedPreferenceLongLiveData(sharedPreferences,
                this.context.getString(R.string.shared_preference_last_board_for_account_) + accountId, NOT_AVAILABLE))
                .distinctUntilChanged()
                .tap(boardId -> {
                    DeckLog.log("--- Read:", context.getString(R.string.shared_preference_last_board_for_account_) + accountId, "→", boardId);
                    if (NOT_AVAILABLE.equals(boardId)) {
                        executor.submit(() -> saveNeighbourOfBoard(accountId, NOT_AVAILABLE));
                    }
                });
    }

    @WorkerThread
    public void saveNeighbourOfBoard(long accountId, long currentBoardId) {
        getNeighbour(db.getBoardDao().getNotDeletedBoardsDirectly(accountId, 0), currentBoardId)
                .ifPresentOrElse(neighbourBoardId -> saveCurrentBoardId(accountId, neighbourBoardId), () -> removeCurrentBoardId(accountId));
    }

    public CompletableFuture<Integer> getCurrentBoardColor(long accountId, long boardId) {
        return supplyAsync(() -> getBoardColorDirectly(accountId, boardId), executor)
                .thenApplyAsync(color -> color == null ? defaultColor : color, executor);
    }

    // -------------
    // Current stack
    // -------------

    public void saveCurrentStackId(long accountId, long boardId, long stackId) {
        DeckLog.log("--- Write:", context.getString(R.string.shared_preference_last_stack_for_account_and_board_) + accountId + "_" + boardId, "→", stackId);
        sharedPreferencesEditor.putLong(context.getString(R.string.shared_preference_last_stack_for_account_and_board_) + accountId + "_" + boardId, stackId);
        sharedPreferencesEditor.apply();
    }

    public void removeCurrentStackId(long accountId, long boardId) {
        DeckLog.log("--- Remove:", context.getString(R.string.shared_preference_last_stack_for_account_and_board_) + accountId + "_" + boardId);
        sharedPreferencesEditor.remove(context.getString(R.string.shared_preference_last_stack_for_account_and_board_) + accountId + "_" + boardId);
        sharedPreferencesEditor.apply();
    }

    public LiveData<Long> getCurrentStackId$(long accountId, long boardId) {
        return new ReactiveLiveData<>(new SharedPreferenceLongLiveData(sharedPreferences, context.getString(R.string.shared_preference_last_stack_for_account_and_board_) + accountId + "_" + boardId, NOT_AVAILABLE))
                .distinctUntilChanged()
                .tap(stackId -> {
                    DeckLog.log("--- Read:", context.getString(R.string.shared_preference_last_stack_for_account_and_board_) + accountId + "_" + boardId, "→", stackId);
                    if (NOT_AVAILABLE.equals(stackId)) {
                        executor.submit(() -> saveNeighbourOfStack(accountId, boardId, NOT_AVAILABLE));
                    }
                });
    }

    @WorkerThread
    public void saveNeighbourOfStack(long accountId, long boardId, long currentStackId) {
        getNeighbour(getFullStacksForBoardDirectly(accountId, boardId), currentStackId)
                .ifPresentOrElse(
                        neighbourStackId -> saveCurrentStackId(accountId, boardId, neighbourStackId),
                        () -> removeCurrentStackId(accountId, boardId));
    }

    /**
     * @return the local ID of the direct neighbour of the given {@param currentId} if available. Prefers neighbours to the start of the wanted, but might also return a neighbour to the end.
     */
    private Optional<Long> getNeighbour(List<? extends IRemoteEntity> entities, long currentId) {
        if (entities.size() < 1) {
            return Optional.empty();
        }

        @Nullable Integer position = null;

        for (int i = 0; i < entities.size(); i++) {
            if (entities.get(i).getLocalId() == currentId) {
                position = i;
            }
        }

        // Not found, but there is an entry
        if (position == null) {
            return Optional.of(entities.get(0).getLocalId());
        }

        // Current entity is last entity
        if (position == 0 && entities.size() == 1) {
            return Optional.empty();
        }

        return Optional.of(position > 0
                ? entities.get(position - 1).getLocalId()
                : entities.get(position + 1).getLocalId());
    }

    // TODO TEST stuff, remove when done
    public List<Long> getAllStackIDs() {
        return db.getStackDao().getAllIDs();
    }
    public List<Long> getAllCardIDs() {
        return db.getCardDao().getAllIDs();
    }
}
