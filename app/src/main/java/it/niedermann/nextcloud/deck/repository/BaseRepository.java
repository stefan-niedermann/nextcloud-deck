package it.niedermann.nextcloud.deck.repository;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.AnyThread;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import it.niedermann.android.reactivelivedata.ReactiveLiveData;
import it.niedermann.nextcloud.deck.database.DataBaseAdapter;
import it.niedermann.nextcloud.deck.model.AccessControl;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.appwidgets.StackWidgetModel;
import it.niedermann.nextcloud.deck.model.enums.DBStatus;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.model.full.FullCardWithProjects;
import it.niedermann.nextcloud.deck.model.full.FullSingleCardWidgetModel;
import it.niedermann.nextcloud.deck.model.full.FullStack;
import it.niedermann.nextcloud.deck.model.internal.FilterInformation;
import it.niedermann.nextcloud.deck.model.ocs.comment.full.FullDeckComment;
import it.niedermann.nextcloud.deck.model.ocs.projects.OcsProjectResource;
import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidget;
import it.niedermann.nextcloud.deck.model.widget.filter.dto.FilterWidgetCard;
import it.niedermann.nextcloud.deck.remote.api.IResponseCallback;
import it.niedermann.nextcloud.deck.remote.api.LastSyncUtil;
import it.niedermann.nextcloud.deck.remote.api.ResponseCallback;
import it.niedermann.nextcloud.deck.remote.helpers.util.ConnectivityUtil;
import it.niedermann.nextcloud.deck.ui.upcomingcards.UpcomingCardsAdapterItem;
import it.niedermann.nextcloud.deck.util.ExecutorServiceProvider;

/**
 * Allows basic local access to the {@link DataBaseAdapter} layer but also to some app states which are stored in {@link SharedPreferences}.
 * <p>
 * This repository does not know anything about remote synchronization.
 */
@SuppressWarnings("WeakerAccess")
public class BaseRepository {

    @NonNull
    protected final Context context;
    @NonNull
    protected final DataBaseAdapter dataBaseAdapter;
    @NonNull
    protected final ExecutorService executor;
    @NonNull
    protected final ConnectivityUtil connectivityUtil;
    @NonNull
    protected final ReactiveLiveData<Long> currentAccountId$;

    public BaseRepository(@NonNull Context context) {
        this(context, new ConnectivityUtil(context));
    }

    protected BaseRepository(@NonNull Context context, @NonNull ConnectivityUtil connectivityUtil) {
        this(context, connectivityUtil, new DataBaseAdapter(context.getApplicationContext()), ExecutorServiceProvider.getLinkedBlockingQueueExecutor());
    }

    protected BaseRepository(@NonNull Context context,
                             @NonNull ConnectivityUtil connectivityUtil,
                             @NonNull DataBaseAdapter databaseAdapter,
                             @NonNull ExecutorService executor) {
        this.context = context.getApplicationContext();
        this.connectivityUtil = connectivityUtil;
        this.dataBaseAdapter = databaseAdapter;
        this.executor = executor;
        this.currentAccountId$ = new ReactiveLiveData<>(dataBaseAdapter.getCurrentAccountId$()).distinctUntilChanged();
        LastSyncUtil.init(context.getApplicationContext());
    }

    public void saveCurrentAccount(@NonNull Account account) {
        dataBaseAdapter.saveCurrentAccount(account);
    }

    public LiveData<Long> getCurrentAccountId$() {
        return this.currentAccountId$;
    }

    public CompletableFuture<Long> getCurrentAccountId() {
        return dataBaseAdapter.getCurrentAccountId();
    }

    public LiveData<Integer> getAccountColor(long accountId) {
        return dataBaseAdapter.getAccountColor(accountId);
    }

    @ColorInt
    public CompletableFuture<Integer> getCurrentAccountColor(long accountId) {
        return dataBaseAdapter.getCurrentAccountColor(accountId);
    }

    // -------------
    // Current board
    // -------------

    public void saveCurrentBoardId(long accountId, long boardId) {
        dataBaseAdapter.saveCurrentBoardId(accountId, boardId);
    }

    public LiveData<Long> getCurrentBoardId$(long accountId) {
        return dataBaseAdapter.getCurrentBoardId$(accountId);
    }

    public LiveData<Integer> getBoardColor$(long accountId, long boardId) {
        return dataBaseAdapter.getBoardColor$(accountId, boardId);
    }

    public CompletableFuture<Integer> getCurrentBoardColor(long accountId, long boardId) {
        return dataBaseAdapter.getCurrentBoardColor(accountId, boardId);
    }

    // -------------
    // Current stack
    // -------------

    public void saveCurrentStackId(long accountId, long boardId, long stackId) {
        dataBaseAdapter.saveCurrentStackId(accountId, boardId, stackId);
    }

    public LiveData<Long> getCurrentStackId$(long accountId, long boardId) {
        return dataBaseAdapter.getCurrentStackId$(accountId, boardId);
    }

    // ==================================================================================================================================

    @AnyThread
    public void createAccount(@NonNull Account account, @NonNull IResponseCallback<Account> callback) {
        executor.submit(() -> {
            try {
                callback.onResponse(dataBaseAdapter.createAccountDirectly(account));
            } catch (Throwable t) {
                callback.onError(t);
            }
        });
    }

    @AnyThread
    public void deleteAccount(long id) {
        executor.submit(() -> {
            dataBaseAdapter.saveNeighbourOfAccount(id);
            dataBaseAdapter.removeCurrentBoardId(id);
            dataBaseAdapter.deleteAccount(id);
            LastSyncUtil.resetLastSyncDate(id);
        });
    }

    @AnyThread
    public LiveData<Boolean> hasAccounts() {
        return dataBaseAdapter.hasAccounts();
    }

    @UiThread
    public LiveData<Account> readAccount(long id) {
        return dataBaseAdapter.readAccount(id);
    }

    @WorkerThread
    public Account readAccountDirectly(long id) {
        return dataBaseAdapter.readAccountDirectly(id);
    }

    @WorkerThread
    public Account readAccountDirectly(@Nullable String name) {
        return dataBaseAdapter.readAccountDirectly(name);
    }

    @UiThread
    public LiveData<Account> readAccount(@Nullable String name) {
        return dataBaseAdapter.readAccount(name);
    }

    @WorkerThread
    public Long getBoardLocalIdByAccountAndCardRemoteIdDirectly(long accountId, long cardRemoteId) {
        return dataBaseAdapter.getBoardLocalIdByAccountAndCardRemoteIdDirectly(accountId, cardRemoteId);
    }

    @UiThread
    public LiveData<List<Account>> readAccounts() {
        return dataBaseAdapter.readAccounts();
    }

    @WorkerThread
    public List<Account> readAccountsDirectly() {
        return dataBaseAdapter.getAllAccountsDirectly();
    }

    /**
     * @param localProjectId LocalId of the OcsProject
     * @return all {@link OcsProjectResource}s of the Project
     */
    @AnyThread
    public LiveData<List<OcsProjectResource>> getResourcesForProject(long localProjectId) {
        return dataBaseAdapter.getResourcesByLocalProjectId(localProjectId);
    }

    /**
     * @param accountId ID of the account
     * @param archived  Decides whether only archived or not-archived boards for the specified account will be returned
     * @return all archived or non-archived <code>Board</code>s depending on <code>archived</code> parameter
     */
    @AnyThread
    public LiveData<List<Board>> getBoards(long accountId, boolean archived) {
        return dataBaseAdapter.getBoards(accountId, archived);
    }

    /**
     * @param accountId ID of the account
     * @param archived  Decides whether only archived or not-archived boards for the specified account will be returned
     * @return all archived or non-archived <code>FullBoard</code>s depending on <code>archived</code> parameter
     */
    @AnyThread
    public LiveData<List<FullBoard>> getFullBoards(long accountId, boolean archived) {
        return dataBaseAdapter.getFullBoards(accountId, archived);
    }

    /**
     * Get all non-archived  <code>FullBoard</code>s with edit permissions for the specified account.
     *
     * @param accountId ID of the account
     * @return all non-archived <code>Board</code>s with edit permission
     */
    @AnyThread
    public LiveData<List<Board>> getBoardsWithEditPermission(long accountId) {
        return dataBaseAdapter.getBoardsWithEditPermission(accountId);
    }

    @AnyThread
    public LiveData<Boolean> hasArchivedBoards(long accountId) {
        return dataBaseAdapter.hasArchivedBoards(accountId);
    }

    public LiveData<FullBoard> getFullBoardById(Long accountId, Long localId) {
        return dataBaseAdapter.getFullBoardById(accountId, localId);
    }

    public Board getBoardById(Long localId) {
        return dataBaseAdapter.getBoardByLocalIdDirectly(localId);
    }

    public LiveData<List<FullDeckComment>> getFullCommentsForLocalCardId(long localCardId) {
        return dataBaseAdapter.getFullCommentsForLocalCardId(localCardId);
    }

    public LiveData<List<Stack>> getStacksForBoard(long accountId, long localBoardId) {
        return dataBaseAdapter.getStacksForBoard(accountId, localBoardId);
    }

    public LiveData<FullStack> getStack(long accountId, long localStackId) {
        return dataBaseAdapter.getStack(accountId, localStackId);
    }

    public void countCardsInStackDirectly(long accountId, long localStackId, @NonNull IResponseCallback<Integer> callback) {
        executor.submit(() -> dataBaseAdapter.countCardsInStackDirectly(accountId, localStackId, callback));
    }

    public void countCardsWithLabel(long localLabelId, @NonNull IResponseCallback<Integer> callback) {
        executor.submit(() -> dataBaseAdapter.countCardsWithLabel(localLabelId, callback));
    }

    public LiveData<FullCardWithProjects> getFullCardWithProjectsByLocalId(long accountId, long cardLocalId) {
        return dataBaseAdapter.getCardWithProjectsByLocalId(accountId, cardLocalId);
    }

    public LiveData<List<FullCard>> getFullCardsForStack(long accountId, long localStackId, @Nullable FilterInformation filter) {
        return dataBaseAdapter.getFullCardsForStack(accountId, localStackId, filter);
    }

    @WorkerThread
    public Long getBoardLocalIdByLocalCardIdDirectly(long localCardId) {
        return dataBaseAdapter.getBoardLocalIdByLocalCardIdDirectly(localCardId);
    }

    @WorkerThread
    public AccessControl getAccessControlByRemoteIdDirectly(long accountId, Long id) {
        return dataBaseAdapter.getAccessControlByRemoteIdDirectly(accountId, id);
    }

    public LiveData<List<AccessControl>> getAccessControlByLocalBoardId(long accountId, Long id) {
        return dataBaseAdapter.getAccessControlByLocalBoardId(accountId, id);
    }

    // --- User search ---

    public LiveData<List<User>> findProposalsForUsersToAssignForACL(final long accountId, long boardId, final int topX) {
        return dataBaseAdapter.findProposalsForUsersToAssignForACL(accountId, boardId, topX);
    }

    public LiveData<List<User>> searchUserByUidOrDisplayNameForACL(final long accountId, final long notYetAssignedToACL, final String constraint) {
        return dataBaseAdapter.searchUserByUidOrDisplayNameForACL(accountId, notYetAssignedToACL, constraint);
    }

    public LiveData<List<User>> findProposalsForUsersToAssignForCards(final long accountId, long boardId, long notAssignedToLocalCardId, final int topX) {
        return dataBaseAdapter.findProposalsForUsersToAssign(accountId, boardId, notAssignedToLocalCardId, topX);
    }

    public LiveData<List<User>> searchUserByUidOrDisplayNameForCards(final long accountId, final long boardId, final long notYetAssignedToLocalCardId, final String searchTerm) {
        return dataBaseAdapter.searchUserByUidOrDisplayName(accountId, boardId, notYetAssignedToLocalCardId, searchTerm);
    }

    // --- Label search ---

    public LiveData<List<Label>> findProposalsForLabelsToAssign(final long accountId, final long boardId) {
        return findProposalsForLabelsToAssign(accountId, boardId, -1L);
    }

    public LiveData<List<Label>> findProposalsForLabelsToAssign(final long accountId, final long boardId, long notAssignedToLocalCardId) {
        return dataBaseAdapter.findProposalsForLabelsToAssign(accountId, boardId, notAssignedToLocalCardId);
    }

    public LiveData<List<Label>> searchNotYetAssignedLabelsByTitle(@NonNull Account account, final long boardId, final long notYetAssignedToLocalCardId, @NonNull String searchTerm) {
        return dataBaseAdapter.searchNotYetAssignedLabelsByTitle(account.getId(), boardId, notYetAssignedToLocalCardId, searchTerm);
    }

    public LiveData<User> getUserByLocalId(long accountId, long localId) {
        return dataBaseAdapter.getUserByLocalId(accountId, localId);
    }

    public LiveData<User> getUserByUid(long accountId, String uid) {
        return dataBaseAdapter.getUserByUid(accountId, uid);
    }

    @WorkerThread
    public User getUserByUidDirectly(long accountId, String uid) {
        return dataBaseAdapter.getUserByUidDirectly(accountId, uid);
    }

    public LiveData<Board> getBoardByRemoteId(long accountId, long remoteId) {
        return dataBaseAdapter.getBoardByRemoteId(accountId, remoteId);
    }

    @WorkerThread
    public Board getBoardByRemoteIdDirectly(long accountId, long remoteId) {
        return dataBaseAdapter.getBoardByRemoteIdDirectly(accountId, remoteId);
    }

    public LiveData<Stack> getStackByRemoteId(long accountId, long localBoardId, long remoteId) {
        return dataBaseAdapter.getStackByRemoteId(accountId, localBoardId, remoteId);
    }

    public LiveData<Card> getCardByRemoteID(long accountId, long remoteId) {
        return dataBaseAdapter.getCardByRemoteID(accountId, remoteId);
    }

    @WorkerThread
    public Optional<Card> getCardByRemoteIDDirectly(long accountId, long remoteId) {
        return Optional.ofNullable(dataBaseAdapter.getCardByRemoteIDDirectly(accountId, remoteId));
    }

    public long createUser(long accountId, User user) {
        return dataBaseAdapter.createUser(accountId, user);
    }

    public void updateUser(long accountId, @NonNull User user) {
        dataBaseAdapter.updateUser(accountId, user, true);
    }

    protected void reorderLocally(List<FullCard> cardsOfNewStack, @NonNull FullCard movedCard, long newStackId, int newOrder) {
        // set new stack and order
        Card movedInnerCard = movedCard.getCard();
        int oldOrder = movedInnerCard.getOrder();
        long oldStackId = movedInnerCard.getStackId();


        List<Card> changedCards = new ArrayList<>();

        int startingAtOrder = newOrder;
        if (oldStackId == newStackId) {
            // card was only reordered in the same stack
            movedInnerCard.setStatusEnum(movedInnerCard.getStatus() == DBStatus.LOCAL_MOVED.getId() ? DBStatus.LOCAL_MOVED : DBStatus.LOCAL_EDITED);
            // move direction?
            if (oldOrder > newOrder) {
                // up
                changedCards.add(movedCard.getCard());
                for (FullCard cardToUpdate : cardsOfNewStack) {
                    Card cardEntity = cardToUpdate.getCard();
                    if (cardEntity.getOrder() < newOrder) {
                        continue;
                    }
                    if (cardEntity.getOrder() >= oldOrder) {
                        break;
                    }
                    changedCards.add(cardEntity);
                }
            } else {
                // down
                startingAtOrder = oldOrder;
                for (FullCard cardToUpdate : cardsOfNewStack) {
                    Card cardEntity = cardToUpdate.getCard();
                    if (cardEntity.getOrder() <= oldOrder) {
                        continue;
                    }
                    if (cardEntity.getOrder() > newOrder) {
                        break;
                    }
                    changedCards.add(cardEntity);
                }
                changedCards.add(movedCard.getCard());
            }
        } else {
            // card was moved to an other stack
            movedInnerCard.setStackId(newStackId);
            movedInnerCard.setStatusEnum(DBStatus.LOCAL_MOVED);
            changedCards.add(movedCard.getCard());
            for (FullCard fullCard : cardsOfNewStack) {
                // skip unchanged cards
                if (fullCard.getCard().getOrder() < newOrder) {
                    continue;
                }
                changedCards.add(fullCard.getCard());
            }
        }
        reorderAscending(movedInnerCard, changedCards, startingAtOrder);
    }

    private void reorderAscending(@NonNull Card movedCard, @NonNull List<Card> cardsToReorganize, int startingAtOrder) {
        final Instant now = Instant.now();
        for (Card card : cardsToReorganize) {
            card.setOrder(startingAtOrder);
            if (card.getStatus() == DBStatus.UP_TO_DATE.getId()) {
                card.setStatusEnum(DBStatus.LOCAL_EDITED_SILENT);
                card.setLastModifiedLocal(now);
            }
            startingAtOrder++;
        }
        //update the moved one first, because otherwise a bunch of livedata is fired, leading the card to dispose and reappear
        cardsToReorganize.remove(movedCard);
        dataBaseAdapter.updateCard(movedCard, false);
        for (Card card : cardsToReorganize) {
            dataBaseAdapter.updateCard(card, false);
        }
    }

    // -------------------
    // Widgets
    // -------------------

    // # filter widget

    @AnyThread
    public void createFilterWidget(@NonNull FilterWidget filterWidget, @NonNull IResponseCallback<Integer> callback) {
        executor.submit(() -> {
            try {
                int filterWidgetId = dataBaseAdapter.createFilterWidgetDirectly(filterWidget);
                callback.onResponse(filterWidgetId);
            } catch (Throwable t) {
                callback.onError(t);
            }
        });
    }

    @AnyThread
    public void updateFilterWidget(@NonNull FilterWidget filterWidget, @NonNull ResponseCallback<Boolean> callback) {
        executor.submit(() -> {
            try {
                dataBaseAdapter.updateFilterWidgetDirectly(filterWidget);
                callback.onResponse(Boolean.TRUE);
            } catch (Throwable t) {
                callback.onError(t);
            }
        });
    }

    @AnyThread
    public void getFilterWidget(@NonNull Integer filterWidgetId, @NonNull IResponseCallback<FilterWidget> callback) {
        executor.submit(() -> {
            try {
                callback.onResponse(dataBaseAdapter.getFilterWidgetByIdDirectly(filterWidgetId));
            } catch (Throwable t) {
                callback.onError(t);
            }
        });
    }

    @AnyThread
    public void deleteFilterWidget(int filterWidgetId, @NonNull IResponseCallback<Boolean> callback) {
        executor.submit(() -> {
            try {
                dataBaseAdapter.deleteFilterWidgetDirectly(filterWidgetId);
                callback.onResponse(Boolean.TRUE);
            } catch (Throwable t) {
                callback.onError(t);
            }
        });
    }

    public boolean filterWidgetExists(int id) {
        return dataBaseAdapter.filterWidgetExists(id);
    }

    @WorkerThread
    public List<FilterWidgetCard> getCardsForFilterWidget(@NonNull Integer filterWidgetId) {
        return dataBaseAdapter.getCardsForFilterWidget(filterWidgetId);
    }

    @WorkerThread
    public LiveData<List<UpcomingCardsAdapterItem>> getCardsForUpcomingCards() {
        return dataBaseAdapter.getCardsForUpcomingCard();
    }

    @WorkerThread
    public List<UpcomingCardsAdapterItem> getCardsForUpcomingCardsForWidget() {
        return dataBaseAdapter.getCardsForUpcomingCardForWidget();
    }

    // # single card widget

    /**
     * Can be called from a configuration screen or a picker.
     * Creates a new entry in the database, if row with given widgetId does not yet exist.
     */
    @AnyThread
    public void addOrUpdateSingleCardWidget(int widgetId, long accountId, long boardId, long localCardId) {
        executor.submit(() -> dataBaseAdapter.createSingleCardWidget(widgetId, accountId, boardId, localCardId));
    }

    @WorkerThread
    public FullSingleCardWidgetModel getSingleCardWidgetModelDirectly(int appWidgetId) throws NoSuchElementException {
        final FullSingleCardWidgetModel model = dataBaseAdapter.getFullSingleCardWidgetModel(appWidgetId);
        if (model == null) {
            throw new NoSuchElementException("There is no " + FullSingleCardWidgetModel.class.getSimpleName() + " with the given appWidgetId " + appWidgetId);
        }
        return model;
    }

    @AnyThread
    public void deleteSingleCardWidgetModel(int widgetId) {
        executor.submit(() -> dataBaseAdapter.deleteSingleCardWidget(widgetId));
    }

    public void addStackWidget(int appWidgetId, long accountId, long stackId, boolean darkTheme) {
        executor.submit(() -> dataBaseAdapter.createStackWidget(appWidgetId, accountId, stackId, darkTheme));
    }

    @WorkerThread
    public StackWidgetModel getStackWidgetModelDirectly(int appWidgetId) throws NoSuchElementException {
        final StackWidgetModel model = dataBaseAdapter.getStackWidgetModelDirectly(appWidgetId);
        if (model == null) {
            throw new NoSuchElementException();
        }
        return model;
    }

    public void deleteStackWidgetModel(int appWidgetId) {
        executor.submit(() -> dataBaseAdapter.deleteStackWidget(appWidgetId));
    }

    @WorkerThread
    public Stack getStackDirectly(long stackLocalId) {
        return dataBaseAdapter.getStackByLocalIdDirectly(stackLocalId);
    }

    @ColorInt
    @WorkerThread
    public Integer getBoardColorDirectly(long accountId, long localBoardId) {
        return dataBaseAdapter.getBoardColorDirectly(accountId, localBoardId);
    }
}
