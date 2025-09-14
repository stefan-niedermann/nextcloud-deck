package it.niedermann.nextcloud.deck.repository.sync;

import android.content.Context;

import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Comparator;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import it.niedermann.nextcloud.deck.repository.sync.report.SyncStatusReporter;
import it.niedermann.nextcloud.deck.shared.model.Account;


public interface SyncScheduler {

    @AnyThread
    CompletableFuture<?> scheduleSynchronization(@NonNull Account account,
                                                 @NonNull Scope scope,
                                                 @Nullable Supplier<CompletableFuture<?>> singlePushTask,
                                                 @Nullable SyncStatusReporter reporter);

    @AnyThread
    default CompletableFuture<?> scheduleSynchronization(@NonNull Account account,
                                                         @NonNull Scope scope,
                                                         @Nullable SyncStatusReporter reporter) {
        return scheduleSynchronization(account, scope, null, reporter);
    }

    @AnyThread
    default CompletableFuture<?> scheduleSynchronization(@NonNull Account account,
                                                         @NonNull Scope scope) {
        return scheduleSynchronization(account, scope, null, null);
    }

    /// @noinspection unchecked
    @AnyThread
    default <T> CompletableFuture<T> pushSingleEntity(@NonNull Account account,
                                                      @NonNull Supplier<CompletableFuture<T>> singlePushTask) {
        return (CompletableFuture<T>) scheduleSynchronization(account, Scope.SINGLE_PUSH_ONLY, singlePushTask::get, null);
    }

    class Factory {

        private final SyncScheduler defaultSyncScheduler;

        public Factory(@NonNull Context context) {
            defaultSyncScheduler = new DefaultSyncScheduler(context.getApplicationContext());
        }

        @NonNull
        public SyncScheduler create() {
            return defaultSyncScheduler;
        }
    }

    enum Scope implements Comparator<Scope> {
        SINGLE_PUSH_ONLY,
        ALL_PUSH_ONLY,
        ALL_PUSH_AND_PULL;

        /// Compares the **level** of the given [Scope]s assuming both scope owning tasks have not been started yet:
        /// "Being equal to" refers to the same type of task, not necessarily the same task.
        /// If this method returns `0`, the passed [Scope]s are not necessarily exchangeable.
        /// Only if one [Scope] is greater or less than the other you can be *sure* that the lesser of both is completely covered by the upper.
        ///
        /// @noinspection SwitchStatementWithTooFewBranches
        @Override
        public int compare(@Nullable Scope o1, @Nullable Scope o2) {
            if (o1 == null && o2 == null) return 0;
            if (o1 == null) return 1;
            assert o2 != null;

            return switch (o1) {
                case SINGLE_PUSH_ONLY -> switch (o2) {
                    case SINGLE_PUSH_ONLY -> 0;
                    default -> -1;
                };
                case ALL_PUSH_ONLY -> switch (o2) {
                    case ALL_PUSH_AND_PULL -> -1;
                    default -> 1;
                };
                case ALL_PUSH_AND_PULL -> 1;
            };
        }

        /// Semantic alias for [#compare(Scope, Scope)]
        public boolean isCoveredBy(@NonNull Scope scope) {
            return compare(this, scope) > 0;
        }
    }
}
