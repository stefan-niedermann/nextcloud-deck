package it.niedermann.nextcloud.deck.data.repository;

import static org.reactivestreams.FlowAdapters.toFlowPublisher;

import org.reactivestreams.FlowAdapters;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;
import java.util.logging.Logger;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.processors.BehaviorProcessor;
import io.reactivex.rxjava3.processors.FlowableProcessor;
import io.reactivex.rxjava3.processors.ReplayProcessor;
import it.niedermann.nextcloud.deck.data.local.KeyValueStore;
import it.niedermann.nextcloud.deck.domain.repository.StateRepository;
import jakarta.inject.Inject;

public class StateRepositoryImpl implements StateRepository {

    private static final Logger logger = Logger.getLogger(StateRepositoryImpl.class.getName());

    private final KeyValueStore keyValueStore;

    private final Map<Long, ReplayProcessor<Long>> currentBoardMockStore = new HashMap<>();

    private final FlowableProcessor<Long> currentAccountIdProcessor = BehaviorProcessor.create();
    private final Flowable<Long> currentAccountIdFlowable = currentAccountIdProcessor.distinctUntilChanged();

    @Inject
    public StateRepositoryImpl(KeyValueStore keyValueStore) {
        this.keyValueStore = keyValueStore;
        init();
    }

    private void init() {
        this.keyValueStore.registerLongChangeListener("current.account", currentAccountId -> logger.info("Current account ID: " + currentAccountId));
        this.keyValueStore.registerLongChangeListener("current.account", currentAccountIdProcessor::onNext);
    }

    @Override
    public CompletableFuture<Long> setCurrentAccountId(long accountId) {
        keyValueStore.putLong("current.account", accountId);
        return currentAccountIdFlowable
                .filter(Long.valueOf(accountId)::equals)
                .firstOrErrorStage()
                .toCompletableFuture();
    }

    @Override
    public Flow.Publisher<Long> getCurrentAccountId() {
        return toFlowPublisher(currentAccountIdFlowable);
    }

    @Override
    public CompletableFuture<Long> setCurrentBoardId(long accountId, long boardId) {
        // TODO Implement
        this.currentBoardMockStore.putIfAbsent(accountId, ReplayProcessor.create());
        final var processor = this.currentBoardMockStore.get(accountId);
        processor.onNext(boardId);
        return Single.fromPublisher(processor).toCompletionStage().toCompletableFuture();
    }

    @Override
    public Flow.Publisher<Long> getCurrentBoardId(long accountId) {
        // TODO Implement
        final ReplayProcessor<Long> foo = ReplayProcessor.create();
        foo.onNext(1L);
        this.currentBoardMockStore.putIfAbsent(accountId, foo);
        return FlowAdapters.toFlowPublisher(this.currentBoardMockStore.get(accountId));
    }

    @Override
    public CompletableFuture<Void> reset() {
        keyValueStore.remove("current.account");
        return CompletableFuture.completedFuture(null);
    }
}