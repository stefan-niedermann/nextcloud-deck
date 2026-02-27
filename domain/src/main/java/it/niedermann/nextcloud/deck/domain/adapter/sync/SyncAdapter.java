package it.niedermann.nextcloud.deck.domain.adapter.sync;

import java.util.concurrent.CompletableFuture;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.annotations.Nullable;
import it.niedermann.nextcloud.deck.domain.model.Account;


public interface SyncAdapter {

    /// @implSpec Synchronization of multiple [Account]s may run parallel, but sequentially for each [Account].
    CompletableFuture<?> scheduleSynchronization(@NonNull Account account,
                                                 @Nullable SyncStatusReporter reporter);

    default CompletableFuture<?> scheduleSynchronization(@NonNull Account account) {
        return scheduleSynchronization(account, null);
    }
}
