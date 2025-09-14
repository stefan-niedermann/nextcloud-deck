package it.niedermann.nextcloud.deck.deprecated.ui.upcomingcards;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.nextcloud.android.sso.api.EmptyResponse;
import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;

import java.util.List;

import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.remote.api.IResponseCallback;
import it.niedermann.nextcloud.deck.repository.CardRepository;
import it.niedermann.nextcloud.deck.repository.SyncRepository;
import it.niedermann.nextcloud.deck.repository.WidgetRepository;
import it.niedermann.nextcloud.deck.deprecated.ui.viewmodel.BaseViewModel;

@SuppressWarnings("WeakerAccess")
public class UpcomingCardsViewModel extends BaseViewModel {

    private final CardRepository cardRepository;
    private final WidgetRepository widgetRepository;

    public UpcomingCardsViewModel(@NonNull Application application) {
        super(application);
        this.cardRepository = new CardRepository(application);
        this.widgetRepository = new WidgetRepository(application);
    }

    public LiveData<List<UpcomingCardsAdapterItem>> getUpcomingCards() {
        return this.widgetRepository.getCardsForUpcomingCards();
    }

    public void assignUser(@NonNull Account account, @NonNull Card card) throws NextcloudFilesAppAccountNotFoundException {
        final var syncManager = new SyncRepository(getApplication(), account);
        executor.submit(() -> syncManager.assignUserToCard(userRepository.getUserByUidDirectly(card.getAccountId(), account.getUserName()), card));
    }

    public void unassignUser(@NonNull Account account, @NonNull Card card) throws NextcloudFilesAppAccountNotFoundException {
        final var syncManager = new SyncRepository(getApplication(), account);
        executor.submit(() -> syncManager.unassignUserFromCard(userRepository.getUserByUidDirectly(card.getAccountId(), account.getUserName()), card));
    }

    public void archiveCard(@NonNull FullCard card, @NonNull IResponseCallback<FullCard> callback) {
        cardRepository.archiveCard(card, callback);
    }

    public void deleteCard(@NonNull Card card, @NonNull IResponseCallback<EmptyResponse> callback) {
        cardRepository.deleteCard(card, callback);
    }

    public void moveCard(long originAccountId, long originCardLocalId, long targetAccountId, long targetBoardLocalId, long targetStackLocalId, @NonNull IResponseCallback<EmptyResponse> callback) {
        executor.submit(() -> {
            final var account = accountRepository.readAccountDirectly(originAccountId);
            try {
                final var syncManager = new SyncRepository(getApplication(), account);
                syncManager.moveCard(originAccountId, originCardLocalId, targetAccountId, targetBoardLocalId, targetStackLocalId, callback);
            } catch (NextcloudFilesAppAccountNotFoundException e) {
                callback.onError(e);
            }
        });
    }
}
