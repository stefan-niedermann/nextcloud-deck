package it.niedermann.nextcloud.deck.domain.usecases.sync;

import java.util.concurrent.Flow;

import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.SyncStatus;
import it.niedermann.nextcloud.deck.domain.sync.SyncScheduler;
import jakarta.inject.Inject;

public class ScheduleSyncUseCase {

    private final SyncScheduler syncScheduler;

    @Inject
    public ScheduleSyncUseCase(SyncScheduler syncScheduler) {
        this.syncScheduler = syncScheduler;
    }

    public Flow.Publisher<SyncStatus> execute(Account.ID accountId) {
        return this.syncScheduler.scheduleSynchronization(accountId);
    }
}
