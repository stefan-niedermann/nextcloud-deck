package it.niedermann.nextcloud.deck.repository.sync;

import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.concurrent.CompletableFuture;

import it.niedermann.nextcloud.deck.repository.sync.report.SyncStatusReporter;
import it.niedermann.nextcloud.deck.shared.model.Account;

public interface SyncScheduler {

    /// @implSpec Synchronization of multiple [Account]s may run parallel, but sequentially for each [Account].
    @AnyThread
    CompletableFuture<?> scheduleSynchronization(@NonNull Account account,
                                                 @Nullable SyncStatusReporter reporter);

    @AnyThread
    default CompletableFuture<?> scheduleSynchronization(@NonNull Account account) {
        return scheduleSynchronization(account, null);
    }
}
