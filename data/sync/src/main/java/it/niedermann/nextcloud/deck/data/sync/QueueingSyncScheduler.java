package it.niedermann.nextcloud.deck.data.sync;

import org.reactivestreams.FlowAdapters;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.processors.BehaviorProcessor;
import io.reactivex.rxjava3.processors.ReplayProcessor;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.repository.AccountRepository;
import it.niedermann.nextcloud.deck.domain.state.SyncStatus;
import it.niedermann.nextcloud.deck.domain.sync.SyncScheduler;
import jakarta.inject.Inject;

/// Synchronization is executed parallelly in general but sequentially per [Account].
public class QueueingSyncScheduler implements SyncScheduler {

    private static final Logger logger = Logger.getLogger(QueueingSyncScheduler.class.getName());

    private final Map<Account.ID, Flowable<SyncStatus>> currentSyncs = new HashMap<>(1);
    private final Map<Account.ID, Flowable<SyncStatus>> scheduledSyncs = new HashMap<>(1);

    private final Map<Account.ID, BehaviorProcessor<Optional<SyncStatus>>> accountToSyncStatus = new HashMap<>(1);

    private final SyncManager syncManager;
    private final AccountRepository accountRepository;

    @Inject
    public QueueingSyncScheduler(SyncManager syncManager,
                                 AccountRepository accountRepository) {
        this.syncManager = syncManager;
        this.accountRepository = accountRepository;
    }

    @Override
    public Flow.Publisher<SyncStatus> scheduleSynchronization(Account.ID accountId) {
        synchronized (QueueingSyncScheduler.this) {

            final boolean currentSyncActive = currentSyncs.containsKey(accountId);
            final boolean nextSyncScheduled = scheduledSyncs.containsKey(accountId);
            final boolean noSyncActive = !currentSyncActive && !nextSyncScheduled;
            final boolean currentSyncActiveButNoSyncScheduled = currentSyncActive && !nextSyncScheduled;
            final boolean currentSyncActiveAndAnotherSyncIsScheduled = currentSyncActive && nextSyncScheduled;

            if (noSyncActive) {

                // Currently no sync is active. Let's start one!

                logger.info("Scheduled (currently none active)");
                final var sync = synchronize(accountId).share().doOnTerminate(() -> {

                    synchronized (QueueingSyncScheduler.this) {

                        logger.info("Current sync finished.");
                        currentSyncs.remove(accountId);

                    }

                });

                currentSyncs.put(accountId, sync);

                return FlowAdapters.toFlowPublisher(sync);

            } else if (currentSyncActiveButNoSyncScheduled) {

                // There is a sync in progress, but no scheduled sync.
                // Let's schedule a sync that waits for the current sync being done and then
                // switches the scheduledSync to the current

                logger.info("Scheduled to the end of the current one.");
                final var sync = currentSyncs.get(accountId)
                        .ignoreElements()
                        .doOnTerminate(() -> {

                            synchronized (QueueingSyncScheduler.this) {

                                logger.info("Scheduled now becomes current one.");
                                currentSyncs.put(accountId, scheduledSyncs.get(accountId));
                                scheduledSyncs.remove(accountId);

                            }

                        })
                        .andThen(Flowable.defer(() -> synchronize(accountId)))
                        .share();

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

    @Override
    public CompletableFuture<Void> scheduleSynchronization() {
        return Maybe.fromPublisher(FlowAdapters.toPublisher(accountRepository.getAccounts()))
                .flatMapCompletable(accounts -> {
                    @SuppressWarnings("NewApi") final var synchronizations = accounts.stream()
                            .map(Account::id)
                            .map(this::scheduleSynchronization)
                            .map(FlowAdapters::toPublisher)
                            .map(Completable::fromPublisher)
                            .toList();

                    return Completable.mergeDelayError(synchronizations);
                })
                .toCompletionStage((Void) null)
                .toCompletableFuture();
    }

    @Override
    public Flow.Publisher<Optional<SyncStatus>> getSyncStatus(Account.ID accountId) {
        return FlowAdapters.toFlowPublisher(accountToSyncStatus.computeIfAbsent(accountId, id -> BehaviorProcessor.createDefault(Optional.empty())));
    }

    private Flowable<SyncStatus> synchronize(Account.ID accountId) {
        return Flowable.fromCompletionStage(accountRepository.getAccountSync(accountId))
                .flatMap(this::synchronize);
    }

    private Flowable<SyncStatus> synchronize(Account account) {
        final var accountSyncStatus = accountToSyncStatus.computeIfAbsent(account.id(), accountId -> BehaviorProcessor.createDefault(Optional.empty()));
        final var reporter = ReplayProcessor.<SyncStatus>createWithSize(1);

        logger.info("Start " + account.accountName() + ": " + Instant.now());
        final var future = syncManager.synchronize(account, reporter::onNext);

        future.handle((v, throwable) -> {
            if (throwable != null) {
                logger.log(Level.SEVERE, "[ERROR] " + account.accountName() + ": " + Instant.now(), throwable);
                reporter.onError(throwable);
            } else {
                logger.info("End " + account.accountName() + ": " + Instant.now());
                reporter.onComplete();
            }
            return null;
        });

        // We don't cancel the future anymore, as it's better to let the sync finish
        return reporter
                .doOnNext(syncStatus -> accountSyncStatus.onNext(Optional.of(syncStatus)))
                .doOnTerminate(() -> accountSyncStatus.onNext(Optional.empty()));
    }

    @Override
    public Flow.Publisher<Instant> getLastSuccessfulSynchronizationDate(Account.ID accountId) {
        // TODO Implement
        return FlowAdapters.toFlowPublisher(Flowable.just(Instant.now()));
    }
}
