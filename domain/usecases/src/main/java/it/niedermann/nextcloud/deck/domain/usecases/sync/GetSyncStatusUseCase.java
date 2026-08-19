package it.niedermann.nextcloud.deck.domain.usecases.sync;

import java.util.Optional;
import java.util.concurrent.Flow;

import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.state.SyncStatus;
import it.niedermann.nextcloud.deck.domain.sync.SyncScheduler;
import jakarta.inject.Inject;

public class GetSyncStatusUseCase {

    private final SyncScheduler syncScheduler;

    @Inject
    public GetSyncStatusUseCase(SyncScheduler syncScheduler) {
        this.syncScheduler = syncScheduler;
    }

    public Flow.Publisher<Optional<SyncStatus>> execute(Account.ID accountId) {
        return this.syncScheduler.getSyncStatus(accountId);
    }
}
