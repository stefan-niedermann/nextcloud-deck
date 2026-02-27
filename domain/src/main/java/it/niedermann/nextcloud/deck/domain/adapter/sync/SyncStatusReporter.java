package it.niedermann.nextcloud.deck.domain.adapter.sync;

import java.util.function.Function;

import io.reactivex.rxjava3.annotations.NonNull;

@FunctionalInterface
public interface SyncStatusReporter {
    /// @return whether or not the report got reduced
    boolean report(@NonNull Reducer reducer);

    @FunctionalInterface
    interface Reducer extends Function<SyncStatus, SyncStatus> {

    }
}