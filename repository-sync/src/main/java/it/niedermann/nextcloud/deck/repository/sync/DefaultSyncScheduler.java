package it.niedermann.nextcloud.deck.repository.sync;


import static java.util.Objects.requireNonNull;
import static java.util.concurrent.CompletableFuture.runAsync;

import android.content.Context;

import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

import it.niedermann.nextcloud.deck.repository.sync.report.SyncStatusReporter;
import it.niedermann.nextcloud.deck.shared.model.Account;

public class DefaultSyncScheduler implements SyncScheduler {

    private static final Logger logger = Logger.getLogger(DefaultSyncScheduler.class.getName());

    private static volatile SyncScheduler instance;

    private static final Map<Long, CompletableFuture<Void>> currentSyncs = new HashMap<>(1);
    private static final Map<Long, CompletableFuture<Void>> scheduledSyncs = new HashMap<>(1);

    private final Context context;

    protected DefaultSyncScheduler(@NonNull Context context) {
        this.context = context.getApplicationContext();
    }

    @NonNull
    public static SyncScheduler getInstance(@NonNull Context context) {
        if (instance == null) {
            synchronized (SyncScheduler.class) {
                if (instance == null) {
                    instance = new DefaultSyncScheduler(context.getApplicationContext());
                }
            }
        }

        return instance;
    }

    @AnyThread
    public CompletableFuture<Void> scheduleSynchronization(@NonNull Account account,
                                                           @Nullable SyncStatusReporter reporter) {

        synchronized (DefaultSyncScheduler.this) {

            final long accountId = account.getId();
            final boolean currentSyncActive = currentSyncs.containsKey(accountId);
            final boolean nextSyncScheduled = scheduledSyncs.containsKey(accountId);
            final boolean noSyncActive = !currentSyncActive && !nextSyncScheduled;
            final boolean currentSyncActiveButNoSyncScheduled = currentSyncActive && !nextSyncScheduled;
            final boolean currentSyncActiveAndAnotherSyncIsScheduled = currentSyncActive && nextSyncScheduled;

            if (noSyncActive) {

                // Currently no sync is active. let's start one!

                logger.info("Scheduled (currently none active)");

                currentSyncs.put(accountId, synchronize(this.context, account, reporter)
                        .whenCompleteAsync((result, exception) -> {

                            synchronized (DefaultSyncScheduler.this) {

                                logger.info("Current sync finished.");
                                currentSyncs.remove(accountId);

                            }

                        }));

                return currentSyncs.get(accountId);

            } else if (currentSyncActiveButNoSyncScheduled) {

                // There is a sync in progress, but no scheduled sync.
                // Let's schedule a sync that waits for the current sync being done and then
                // switches the scheduledSync to the current

                logger.info("Scheduled to the end of the current one.");

                scheduledSyncs.put(accountId, requireNonNull(currentSyncs.get(accountId))
                        .whenCompleteAsync((result, exception) -> {

                            synchronized (DefaultSyncScheduler.this) {

                                logger.info("Scheduled now becomes current one.");
                                currentSyncs.put(accountId, scheduledSyncs.get(accountId));
                                scheduledSyncs.remove(accountId);

                            }

                        })
                        .thenComposeAsync(v -> synchronize(this.context, account, reporter))
                        .whenCompleteAsync((result, exception) -> {

                            synchronized (DefaultSyncScheduler.this) {

                                logger.info("Current sync finished.");
                                currentSyncs.remove(accountId);

                            }

                        }));

                return scheduledSyncs.get(accountId);

            } else if (currentSyncActiveAndAnotherSyncIsScheduled) {

                // There is a sync in progress and a scheduled one. It is safe to simply return the scheduled one.

                logger.info("Returned scheduled one");

                return scheduledSyncs.get(accountId);

            }

        }

        // It should not be possible to have a scheduled sync but no actively running one

        final var future = new CompletableFuture<Void>();
        future.completeExceptionally(new IllegalStateException("currentSync is null but scheduledSync is not null."));
        return future;

    }

    private CompletableFuture<Void> synchronize(@NonNull Context context,
                                                @NonNull Account account,
                                                @Nullable SyncStatusReporter reporter) {
        return runAsync(() -> logger.info("Start " + account.getAccountName()))

//                        .thenComposeAsync(v -> pushLocalChanges( /* … */ )) // May be omitted in case account#deckVersion or account#nextcloudVersion is null for the first import
//                        .thenComposeAsync(v -> pullRemoteChanges( /* … */ ))

                .whenCompleteAsync((result, exception) -> logger.info("End " + account.getAccountName()));
    }
}
