package it.niedermann.nextcloud.deck.domain.sync;

import java.time.Instant;
import java.util.concurrent.Flow;

import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.SyncStatus;

public interface SyncScheduler {

    Flow.Publisher<SyncStatus> scheduleSynchronization(Account.ID accountId);

    Flow.Publisher<Instant> getLastSuccessfulSynchronizationDate(Account.ID accountId);
}
