package it.niedermann.nextcloud.deck.repository.sync;


import static java.util.Objects.requireNonNull;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.runAsync;

import android.content.Context;

import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.logging.Logger;

import it.niedermann.nextcloud.deck.repository.sync.report.SyncStatus;
import it.niedermann.nextcloud.deck.repository.sync.report.SyncStatusReporter;
import it.niedermann.nextcloud.deck.shared.model.Account;

/// Synchronization is executed parallel in general but sequentially per [Account].
public class DefaultSyncScheduler implements SyncScheduler {

    private static final Logger logger = Logger.getLogger(DefaultSyncScheduler.class.getSimpleName());

    private static final Map<Long, SyncTask> currentSyncs = new HashMap<>(1);
    private static final Map<Long, SyncTask> scheduledSyncs = new HashMap<>(1);

    private final Context context;

    public DefaultSyncScheduler(@NonNull Context context) {
        this.context = context.getApplicationContext();
    }

    @AnyThread
    public CompletableFuture<?> scheduleSynchronization(@NonNull Account account,
                                                        @NonNull Scope scope,
                                                        @Nullable Supplier<CompletableFuture<?>> singlePushTask,
                                                        @Nullable SyncStatusReporter reporter) {
        if (scope == Scope.SINGLE_PUSH_ONLY) {
            final var result = new CompletableFuture<Void>();
            final var exception = new IllegalArgumentException(Scope.SINGLE_PUSH_ONLY + " must not be used directly. Call scheduleSinglePush(…) instead.");

            result.completeExceptionally(exception);

            if (reporter != null) {
                reporter.report(status -> status.withError(exception));
            }

            return result;
        }

        synchronized (DefaultSyncScheduler.this) {

            final long accountId = account.getId();
            final boolean currentSyncActive = currentSyncs.containsKey(accountId);
            final boolean nextSyncScheduled = scheduledSyncs.containsKey(accountId);
            final boolean noSyncActive = !currentSyncActive && !nextSyncScheduled;
            final boolean currentSyncActiveButNoSyncScheduled = currentSyncActive && !nextSyncScheduled;
            final boolean currentSyncActiveAndAnotherSyncIsScheduled = currentSyncActive && nextSyncScheduled;

            if (noSyncActive) {

                // Currently no sync is active. Let's start one!

                logger.info("Scheduled (currently none active)");

                currentSyncs.put(accountId, new SyncTask(synchronize(this.context, account, scope, singlePushTask, reporter)
                        .whenCompleteAsync((result, exception) -> {

                            synchronized (DefaultSyncScheduler.this) {

                                logger.info("Current sync finished.");
                                currentSyncs.remove(accountId);

                            }

                        }), scope));

                return requireNonNull(currentSyncs.get(accountId)).future;

            } else if (currentSyncActiveButNoSyncScheduled) {

                // There is a sync in progress, but no scheduled sync.
                // Let's schedule a sync that waits for the current sync being done and then
                // switches the scheduledSync to the current

                logger.info("Scheduled to the end of the current one.");

                scheduledSyncs.put(accountId, new SyncTask(requireNonNull(currentSyncs.get(accountId)).future
                        .whenCompleteAsync((result, exception) -> {

                            synchronized (DefaultSyncScheduler.this) {

                                logger.info("Scheduled now becomes current one.");
                                currentSyncs.put(accountId, scheduledSyncs.get(accountId));
                                scheduledSyncs.remove(accountId);

                            }

                        })
                        .thenComposeAsync(v -> synchronize(this.context, account, scope, singlePushTask, reporter))
                        .whenCompleteAsync((result, exception) -> {

                            synchronized (DefaultSyncScheduler.this) {

                                logger.info("Current sync finished.");
                                currentSyncs.remove(accountId);

                            }

                        }), scope));

                return requireNonNull(scheduledSyncs.get(accountId)).future;

            } else if (currentSyncActiveAndAnotherSyncIsScheduled) {

                // We already have a scheduled sync, but we need to make sure that the scheduled sync covers the scope of the requested sync.

                if (!requireNonNull(scheduledSyncs.get(accountId)).scope.isCoveredBy(scope)) {

                    logger.info("Scheduled sync to the end of the currently scheduled push only sync.");

                    // We can not simply replace the scheduled sync as some clients may rely on the execution and wait for it to get finished.
                    // Therefore we attach a higher scope sync to the end of the scheduled sync and use this as new scheduled sync.
                    // The scheduled sync cycle then includes the former scheduled sync and the new higher scope sync.
                    // We replace the scheduled sync instead of attaching the next one to ensure the comparison works the next time a client schedules a sync as expected.

                    scheduledSyncs.put(accountId, new SyncTask(requireNonNull(scheduledSyncs.get(accountId)).future
                            .thenComposeAsync(v -> synchronize(this.context, account, scope, singlePushTask, reporter)), scope));

                }

                logger.info("Returned scheduled sync future");

                return requireNonNull(scheduledSyncs.get(accountId)).future;

            }

        }

        // It should not be possible to have a scheduled sync but no actively running one

        final var future = new CompletableFuture<>();
        future.completeExceptionally(new IllegalStateException("currentSync is null but scheduledSync is not null."));
        return future;

    }

    /**
     * @noinspection DuplicateBranchesInSwitch
     */
    private CompletableFuture<?> synchronize(@NonNull Context context,
                                             @NonNull Account account,
                                             @NonNull Scope scope,
                                             @Nullable Supplier<CompletableFuture<?>> singlePushTask,
                                             @Nullable SyncStatusReporter reporter) {

        return runAsync(() -> logger.info("Start " + account.getAccountName() + " [Scope: " + scope + "]"))
                .thenComposeAsync(v -> switch (scope) {
                    case SINGLE_PUSH_ONLY -> requireNonNull(singlePushTask).get();
                    case ALL_PUSH_ONLY -> {

                        // TODO Implement
                        yield completedFuture(null);

                    }
                    case ALL_PUSH_AND_PULL -> {

                        // TODO Implement
                        yield completedFuture(null);

                    }
                })
                .handleAsync((result, exception) -> {
                    logger.info("End " + account.getAccountName());
                    if (reporter != null)
                        reporter.report(SyncStatus::markAsFinished);
                    return result;
                });
    }

    private record SyncTask(@NonNull CompletableFuture<?> future,
                            @NonNull Scope scope) {
    }
}
