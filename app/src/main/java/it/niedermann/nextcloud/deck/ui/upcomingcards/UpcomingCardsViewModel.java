package it.niedermann.nextcloud.deck.ui.upcomingcards;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;

import java.util.List;

import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.viewmodel.BaseViewModel;

@SuppressWarnings("WeakerAccess")
public class UpcomingCardsViewModel extends BaseViewModel {

    public UpcomingCardsViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<UpcomingCardsAdapterItem>> getUpcomingCards() {
        return this.baseRepository.getCardsForUpcomingCards();
    }

    public void assignUser(@NonNull Account account, @NonNull Card card) throws NextcloudFilesAppAccountNotFoundException {
        final var syncManager = new SyncManager(getApplication(), account);
        executor.submit(() -> syncManager.assignUserToCard(baseRepository.getUserByUidDirectly(card.getAccountId(), account.getUserName()), card));
    }

    public void unassignUser(@NonNull Account account, @NonNull Card card) throws NextcloudFilesAppAccountNotFoundException {
        final var syncManager = new SyncManager(getApplication(), account);
        executor.submit(() -> syncManager.unassignUserFromCard(baseRepository.getUserByUidDirectly(card.getAccountId(), account.getUserName()), card));
    }

    public void archiveCard(@NonNull FullCard card, @NonNull IResponseCallback<FullCard> callback) {
        executor.submit(() -> {
            final var account = baseRepository.readAccountDirectly(card.getAccountId());
            try {
                final var syncManager = new SyncManager(getApplication(), account);
                syncManager.archiveCard(card, callback);
            } catch (NextcloudFilesAppAccountNotFoundException e) {
                callback.onError(e);
            }
        });
    }

    public void deleteCard(@NonNull Card card, @NonNull IResponseCallback<Void> callback) {
        executor.submit(() -> {
            final var account = baseRepository.readAccountDirectly(card.getAccountId());
            try {
                final var syncManager = new SyncManager(getApplication(), account);
                syncManager.deleteCard(card, callback);
            } catch (NextcloudFilesAppAccountNotFoundException e) {
                callback.onError(e);
            }
        });
    }

    public void moveCard(long originAccountId, long originCardLocalId, long targetAccountId, long targetBoardLocalId, long targetStackLocalId, @NonNull IResponseCallback<Void> callback) {
        executor.submit(() -> {
            final var account = baseRepository.readAccountDirectly(originAccountId);
            try {
                final var syncManager = new SyncManager(getApplication(), account);
                syncManager.moveCard(originAccountId, originCardLocalId, targetAccountId, targetBoardLocalId, targetStackLocalId, callback);
            } catch (NextcloudFilesAppAccountNotFoundException e) {
                callback.onError(e);
            }
        });
    }
}
