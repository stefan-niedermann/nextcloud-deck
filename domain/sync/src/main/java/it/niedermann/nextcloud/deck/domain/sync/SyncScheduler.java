package it.niedermann.nextcloud.deck.domain.sync;

import java.time.Instant;
import java.util.concurrent.Flow;

import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.state.SyncStatus;

public interface SyncScheduler {

    /// @implSpec The implementation must ensure that calling this API multiple times must never cause conflicting synchronization issues
    ///   for example by queuing or rejecting multiple schedule requests
    Flow.Publisher<SyncStatus> scheduleSynchronization(Account.ID accountId);

    Flow.Publisher<Instant> getLastSuccessfulSynchronizationDate(Account.ID accountId);
}
