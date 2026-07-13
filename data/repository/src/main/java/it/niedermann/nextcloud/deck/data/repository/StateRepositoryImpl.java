package it.niedermann.nextcloud.deck.data.repository;

import org.reactivestreams.FlowAdapters;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

import io.reactivex.rxjava3.core.Flowable;
import it.niedermann.nextcloud.deck.data.local.KeyValueStore;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.repository.StateRepository;
import jakarta.inject.Inject;

public class StateRepositoryImpl implements StateRepository {

    private static final Logger logger = Logger.getLogger(StateRepositoryImpl.class.getName());

    private final KeyValueStore keyValueStore;

    private final Map<Account.ID, Board.ID> currentBoardMockStore = new HashMap<>();

    @Inject
    public StateRepositoryImpl(KeyValueStore keyValueStore) {
        this.keyValueStore = keyValueStore;
    }

    @Override
    public CompletableFuture<Account.ID> setCurrentAccountId(Account.ID accountId) {
        keyValueStore.putLong("current.account", accountId.value());
        return getCurrentAccountId();
    }

    @Override
    public CompletableFuture<Account.ID> getCurrentAccountId() {
        return Flowable.fromPublisher(FlowAdapters.toPublisher(keyValueStore.getLong("current.account")))
                .distinctUntilChanged()
                .doOnNext(id -> {
                    if (id == -1L) {
                        throw new NoSuchElementException();
                    }
                })
                .map(Account.ID::new)
                .firstElement()
                .toCompletionStage()
                .toCompletableFuture();
    }

    @Override
    public CompletableFuture<Board.ID> setCurrentBoardId(Account.ID accountId, Board.ID boardId) {
        // TODO Implement
        this.currentBoardMockStore.putIfAbsent(accountId, boardId);
        return getCurrentBoardId(accountId);
    }

    @Override
    public CompletableFuture<Board.ID> getCurrentBoardId(Account.ID accountId) {
        // TODO Implement and throw NoSuchElementException in case no currentBoardId is set
        this.currentBoardMockStore.putIfAbsent(accountId, new Board.ID(1L));
        return CompletableFuture.completedFuture(this.currentBoardMockStore.get(accountId));
//        final var boardIdFuture = new CompletableFuture<Board.ID>();
//        boardIdFuture.completeExceptionally(new NoSuchElementException());
//        return boardIdFuture;
    }

    @Override
    public CompletableFuture<Void> reset() {
        keyValueStore.remove("current.account");
        return CompletableFuture.completedFuture(null);
    }
}