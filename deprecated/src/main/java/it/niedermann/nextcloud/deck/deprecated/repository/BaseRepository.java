package it.niedermann.nextcloud.deck.deprecated.repository;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import it.niedermann.android.reactivelivedata.ReactiveLiveData;
import it.niedermann.nextcloud.deck.database.DataBaseAdapter;
import it.niedermann.nextcloud.deck.model.AccessControl;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.enums.DBStatus;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.model.full.FullCardWithProjects;
import it.niedermann.nextcloud.deck.remote.api.LastSyncUtil;

/**
 * Allows basic local access to the {@link DataBaseAdapter} layer but also to some app states which are stored in {@link SharedPreferences}.
 * <p>
 * This repository does not know anything about remote synchronization.
 */
@SuppressWarnings("WeakerAccess")
public class BaseRepository extends AbstractRepository {

    @NonNull
    protected final ExecutorService executor;
    @NonNull
    protected final ReactiveLiveData<Long> currentAccountId$;


    protected BaseRepository(@NonNull Context context) {
        this(context, SharedExecutors.getLinkedBlockingQueueExecutor());
    }

    protected BaseRepository(@NonNull Context context,
                             @NonNull ExecutorService executor) {
        super(context);
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

    public LiveData<FullCardWithProjects> getFullCardWithProjectsByLocalId(long accountId, long cardLocalId) {
        return dataBaseAdapter.getCardWithProjectsByLocalId(accountId, cardLocalId);
    }

    @WorkerThread
    public AccessControl getAccessControlByRemoteIdDirectly(long accountId, Long id) {
        return dataBaseAdapter.getAccessControlByRemoteIdDirectly(accountId, id);
    }

    public LiveData<List<AccessControl>> getAccessControlByLocalBoardId(long accountId, Long id) {
        return dataBaseAdapter.getAccessControlByLocalBoardId(accountId, id);
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

}
