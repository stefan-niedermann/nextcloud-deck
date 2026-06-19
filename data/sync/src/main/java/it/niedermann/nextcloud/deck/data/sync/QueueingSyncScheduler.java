package it.niedermann.nextcloud.deck.data.sync;

import org.reactivestreams.FlowAdapters;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Flow;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.processors.ReplayProcessor;
import io.reactivex.rxjava3.schedulers.Schedulers;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.SyncStatus;
import it.niedermann.nextcloud.deck.domain.repository.AccountRepository;
import it.niedermann.nextcloud.deck.domain.sync.SyncScheduler;
import it.niedermann.nextcloud.remote.ApiProvider;
import jakarta.inject.Inject;

/// Synchronization is executed parallelly in general but sequentially per [Account].
public class QueueingSyncScheduler implements SyncScheduler {

    private static final Logger logger = Logger.getLogger(QueueingSyncScheduler.class.getName());

    private final Map<Long, Flowable<SyncStatus>> currentSyncs = new HashMap<>(1);
    private final Map<Long, Flowable<SyncStatus>> scheduledSyncs = new HashMap<>(1);

    private final SyncManager syncManager;
    private final AccountRepository accountRepository;

    @Inject
    public QueueingSyncScheduler(ApiProvider.Factory apiProviderFactory,
                                 AccountRepository accountRepository) {
        // TODO SyncManager should be injected directly in favor of instantiating it in the constructor
        this.syncManager = new SyncManager(apiProviderFactory);
        this.accountRepository = accountRepository;
    }

    @Override
    public Flow.Publisher<SyncStatus> scheduleSynchronization(long accountId) {
        synchronized (QueueingSyncScheduler.this) {

            final boolean currentSyncActive = currentSyncs.containsKey(accountId);
            final boolean nextSyncScheduled = scheduledSyncs.containsKey(accountId);
            final boolean noSyncActive = !currentSyncActive && !nextSyncScheduled;
            final boolean currentSyncActiveButNoSyncScheduled = currentSyncActive && !nextSyncScheduled;
            final boolean currentSyncActiveAndAnotherSyncIsScheduled = currentSyncActive && nextSyncScheduled;

            if (noSyncActive) {

                // Currently no sync is active. Let's start one!

                logger.info("Scheduled (currently none active)");
                final var sync = synchronize(accountId);

                currentSyncs.put(accountId, sync.doOnTerminate(() -> {

                    synchronized (QueueingSyncScheduler.this) {

                        logger.info("Current sync finished.");
                        currentSyncs.remove(accountId);

                    }

                }));

                return FlowAdapters.toFlowPublisher(sync);

            } else if (currentSyncActiveButNoSyncScheduled) {

                // There is a sync in progress, but no scheduled sync.
                // Let's schedule a sync that waits for the current sync being done and then
                // switches the scheduledSync to the current

                logger.info("Scheduled to the end of the current one.");
                final var sync = currentSyncs.get(accountId)
                        .doOnTerminate(() -> {

                            synchronized (QueueingSyncScheduler.this) {

                                logger.info("Scheduled now becomes current one.");
                                currentSyncs.put(accountId, scheduledSyncs.get(accountId));
                                scheduledSyncs.remove(accountId);

                            }

                        })
                        .switchMap((oldSyncStatus) -> synchronize(accountId));

                scheduledSyncs.put(accountId, sync.doOnTerminate(() -> {

                    synchronized (QueueingSyncScheduler.this) {

                        logger.info("Current sync finished.");
                        currentSyncs.remove(accountId);

                    }

                }));

                return FlowAdapters.toFlowPublisher(sync);

            } else if (currentSyncActiveAndAnotherSyncIsScheduled) {

                logger.info("Returned scheduled sync Observable");
                return FlowAdapters.toFlowPublisher(scheduledSyncs.get(accountId));

            }

        }

        // It should not be possible to have a scheduled sync but no actively running one

        return subscriber -> subscriber.onError(new IllegalStateException("currentSync is null but scheduledSync is not null."));

    }

    private Flowable<SyncStatus> synchronize(long accountId) {
        final var accountFlowPublisher = accountRepository.getAccount(accountId);
        final var accountPublisher = FlowAdapters.toPublisher(accountFlowPublisher);

        return Flowable.fromPublisher(accountPublisher)
                .firstElement()
                .flatMapPublisher(this::synchronize);
    }

    private Flowable<SyncStatus> synchronize(Account account) {

        final var reporter = ReplayProcessor.<SyncStatus>createWithSize(1);

        return reporter.doOnSubscribe(subscription -> {
            Schedulers.single().createWorker().schedule(() -> {

                try {

                    logger.info("Start " + account.accountName() + ": " + Instant.now());
                    syncManager.synchronize(account, reporter::onNext);
                    reporter.onComplete();

                } catch (Throwable t) {

                    logger.log(Level.SEVERE, "[ERROR] " + account.accountName() + ": " + Instant.now(), t);
                    reporter.onError(t);

                } finally {

                    logger.info("End " + account.accountName() + ": " + Instant.now());

                }

            });
        });
    }

    @Override
    public Flow.Publisher<Instant> getLastSuccessfulSynchronizationDate(long accountId) {
        // TODO Implement
        return FlowAdapters.toFlowPublisher(Flowable.just(Instant.now()));
    }
}
