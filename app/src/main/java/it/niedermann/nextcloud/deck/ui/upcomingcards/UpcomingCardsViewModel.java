package it.niedermann.nextcloud.deck.ui.upcomingcards;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.stream.Collectors;

import it.niedermann.nextcloud.deck.api.ResponseCallback;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.WrappedLiveData;

import static androidx.lifecycle.Transformations.map;

@SuppressWarnings("WeakerAccess")
public class UpcomingCardsViewModel extends AndroidViewModel {

    private final SyncManager syncManager;

    public UpcomingCardsViewModel(@NonNull Application application) {
        super(application);
        this.syncManager = new SyncManager(application);
    }

    public LiveData<List<UpcomingCardsAdapterItem>> getUpcomingCards() {
        // FIXME remove mapping after https://github.com/stefan-niedermann/nextcloud-deck/issues/923
        return map(this.syncManager.getCardsForUpcomingCards(), (cards) ->
                cards.stream().filter(card -> card.getAccount() != null).collect(Collectors.toList())
        );
    }

    public void assignUser(@NonNull Account account, @NonNull Card card) {
        new Thread(() -> syncManager.assignUserToCard(syncManager.getUserByUidDirectly(card.getAccountId(), account.getUserName()), card)).start();
    }

    public void unassignUser(@NonNull Account account, @NonNull Card card) {
        new Thread(() -> syncManager.unassignUserFromCard(syncManager.getUserByUidDirectly(card.getAccountId(), account.getUserName()), card)).start();
    }

    public void archiveCard(@NonNull FullCard card, @NonNull ResponseCallback<FullCard> callback) {
        syncManager.archiveCard(card, callback);
    }

    public void deleteCard(@NonNull Card card, @NonNull ResponseCallback<Void> callback) {
        syncManager.deleteCard(card, callback);
    }

    public WrappedLiveData<Void> moveCard(long originAccountId, long originCardLocalId, long targetAccountId, long targetBoardLocalId, long targetStackLocalId) {
        return syncManager.moveCard(originAccountId, originCardLocalId, targetAccountId, targetBoardLocalId, targetStackLocalId);
    }
}
