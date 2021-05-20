package it.niedermann.nextcloud.deck.ui.upcomingcards;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.WrappedLiveData;

@SuppressWarnings("WeakerAccess")
public class UpcomingCardsViewModel extends AndroidViewModel {

    private final SyncManager syncManager;

    public UpcomingCardsViewModel(@NonNull Application application) {
        super(application);
        this.syncManager = new SyncManager(application);
    }

    public LiveData<List<UpcomingCardsAdapterItem>> getUpcomingCards() {
        return this.syncManager.getCardsForUpcomingCards();
    }

    public void assignUser(@NonNull Account account, @NonNull Card card) {
        new Thread(() -> syncManager.assignUserToCard(syncManager.getUserByUidDirectly(card.getAccountId(), account.getUserName()), card)).start();
    }

    public void unassignUser(@NonNull Account account, @NonNull Card card) {
        new Thread(() -> syncManager.unassignUserFromCard(syncManager.getUserByUidDirectly(card.getAccountId(), account.getUserName()), card)).start();
    }

    public void archiveCard(@NonNull FullCard card, @NonNull IResponseCallback<FullCard> callback) {
        syncManager.archiveCard(card, callback);
    }

    public void deleteCard(@NonNull Card card, @NonNull IResponseCallback<Void> callback) {
        syncManager.deleteCard(card, callback);
    }

    public WrappedLiveData<Void> moveCard(long originAccountId, long originCardLocalId, long targetAccountId, long targetBoardLocalId, long targetStackLocalId) {
        return syncManager.moveCard(originAccountId, originCardLocalId, targetAccountId, targetBoardLocalId, targetStackLocalId);
    }
}
