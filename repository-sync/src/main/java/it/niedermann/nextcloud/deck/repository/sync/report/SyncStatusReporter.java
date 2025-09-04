package it.niedermann.nextcloud.deck.repository.sync.report;

import androidx.annotation.NonNull;

import java.util.function.Function;

@FunctionalInterface
public interface SyncStatusReporter {
    /// @return whether or not the report got reduced
    boolean report(@NonNull Reducer reducer);

    @FunctionalInterface
    interface Reducer extends Function<SyncStatus, SyncStatus> {

    }
}