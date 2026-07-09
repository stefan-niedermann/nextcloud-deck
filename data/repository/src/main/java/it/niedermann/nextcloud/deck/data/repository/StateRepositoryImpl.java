package it.niedermann.nextcloud.deck.data.repository;

import static org.reactivestreams.FlowAdapters.toFlowPublisher;

import org.reactivestreams.FlowAdapters;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;
import java.util.logging.Logger;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.processors.BehaviorProcessor;
import io.reactivex.rxjava3.processors.FlowableProcessor;
import io.reactivex.rxjava3.processors.ReplayProcessor;
import it.niedermann.nextcloud.deck.data.local.KeyValueStore;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.repository.StateRepository;
import jakarta.inject.Inject;

public class StateRepositoryImpl implements StateRepository {

    private static final Logger logger = Logger.getLogger(StateRepositoryImpl.class.getName());

    private final KeyValueStore keyValueStore;

    private final Map<Account.ID, ReplayProcessor<Board.ID>> currentBoardMockStore = new HashMap<>();

    private final FlowableProcessor<Account.ID> currentAccountIdProcessor = BehaviorProcessor.create();
    private final Flowable<Account.ID> currentAccountIdFlowable = currentAccountIdProcessor.distinctUntilChanged();

    @Inject
    public StateRepositoryImpl(KeyValueStore keyValueStore) {
        this.keyValueStore = keyValueStore;
        init();
    }

    private void init() {
        this.keyValueStore.registerLongChangeListener("current.account", currentAccountId -> logger.info("Current account cardId: " + currentAccountId));
        this.keyValueStore.registerLongChangeListener("current.account", currentAccountId -> currentAccountIdProcessor.onNext(new Account.ID(currentAccountId)));
    }

    @Override
    public CompletableFuture<Account.ID> setCurrentAccountId(Account.ID accountId) {
        keyValueStore.putLong("current.account", accountId.value());
        return currentAccountIdFlowable
                .filter(currentAccountId -> Objects.equals(currentAccountId, accountId))
                .firstOrErrorStage()
                .toCompletableFuture();
    }

    @Override
    public Flow.Publisher<Account.ID> getCurrentAccountId() {
        return toFlowPublisher(currentAccountIdFlowable);
    }

    @Override
    public CompletableFuture<Board.ID> setCurrentBoardId(Account.ID accountId, Board.ID boardId) {
        // TODO Implement
        this.currentBoardMockStore.putIfAbsent(accountId, ReplayProcessor.create());
        final var processor = this.currentBoardMockStore.get(accountId);
        processor.onNext(boardId);
        return Single.fromPublisher(processor)
                .toCompletionStage()
                .toCompletableFuture();
    }

    @Override
    public Flow.Publisher<Board.ID> getCurrentBoardId(Account.ID accountId) {
        // TODO Implement
        final ReplayProcessor<Board.ID> foo = ReplayProcessor.create();
        foo.onNext(new Board.ID(1L));
        this.currentBoardMockStore.putIfAbsent(accountId, foo);
        return FlowAdapters.toFlowPublisher(this.currentBoardMockStore.get(accountId));
    }

    @Override
    public CompletableFuture<Void> reset() {
        keyValueStore.remove("current.account");
        return CompletableFuture.completedFuture(null);
    }
}