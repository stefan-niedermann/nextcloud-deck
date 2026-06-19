package it.niedermann.nextcloud.deck.domain.sync;

import java.time.Instant;
import java.util.concurrent.Flow;

import it.niedermann.nextcloud.deck.domain.model.SyncStatus;

public interface SyncScheduler {

    Flow.Publisher<SyncStatus> scheduleSynchronization(long accountId);

    Flow.Publisher<Instant> getLastSuccessfulSynchronizationDate(long accountId);
}
