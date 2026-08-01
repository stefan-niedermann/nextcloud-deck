package it.niedermann.nextcloud.deck.data.repository;

import org.reactivestreams.FlowAdapters;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

import io.reactivex.rxjava3.core.Flowable;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.repository.AccountRepository;
import it.niedermann.nextcloud.deck.domain.state.KeyValueStore;
import it.niedermann.nextcloud.deck.domain.state.StateRepository;
import jakarta.inject.Inject;

public class StateRepositoryImpl implements StateRepository {

    private static final Logger logger = Logger.getLogger(StateRepositoryImpl.class.getName());

    private final KeyValueStore keyValueStore;
    private final AccountRepository accountRepository;

    private final Map<Account.ID, Board.ID> currentBoardMockStore = new HashMap<>();

    @Inject
    public StateRepositoryImpl(KeyValueStore keyValueStore,
                               AccountRepository accountRepository) {
        this.keyValueStore = keyValueStore;
        this.accountRepository = accountRepository;
    }

    @Override
    public CompletableFuture<Account.ID> setCurrentAccountId(Account.ID accountId) {
        return keyValueStore.putLong("current.account", accountId.value())
                .thenCompose(v -> getCurrentAccountId());
    }

    @Override
    public CompletableFuture<Account.ID> getCurrentAccountId() {
        return Flowable.fromPublisher(FlowAdapters.toPublisher(keyValueStore.getLong("current.account")))
                .firstElement()
                .toCompletionStage()
                .toCompletableFuture()
                .thenComposeAsync(id -> {
                    if (id != -1L) {
                        return CompletableFuture.completedFuture(new Account.ID(id));
                    } else {
                        return accountRepository.getAnyAccount()
                                .thenComposeAsync(this::setCurrentAccountId);
                    }
                });
    }

    @Override
    public CompletableFuture<Board.ID> setCurrentBoardId(Account.ID accountId, Board.ID boardId) {
        this.currentBoardMockStore.put(accountId, boardId);
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
    public CompletableFuture<Void> removeCurrentAccountId() {
        return keyValueStore.remove("current.account");
    }

    @Override
    public CompletableFuture<Void> reset() {
        return keyValueStore.remove("current.account");
    }
}