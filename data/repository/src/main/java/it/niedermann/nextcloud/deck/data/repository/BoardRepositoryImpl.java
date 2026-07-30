package it.niedermann.nextcloud.deck.data.repository;

import org.reactivestreams.FlowAdapters;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.CreateBoard;
import it.niedermann.nextcloud.deck.domain.repository.BoardRepository;
import jakarta.inject.Inject;

public class BoardRepositoryImpl implements BoardRepository {

    @Inject
    public BoardRepositoryImpl(
    ) {
    }

    @Override
    public CompletableFuture<Board.ID> createBoard(CreateBoard board) {
        // TODO Implement
        return CompletableFuture.supplyAsync(() -> new Board.ID(1));
    }

    @Override
    public CompletableFuture<Void> updateBoard(Board board) {
        // TODO Implement
        return CompletableFuture.runAsync(() -> {
            // update logic
        });
    }

    @Override
    public Flow.Publisher<Board> getBoard(Board.ID boardId) {
        // TODO Implement
        return FlowAdapters.toFlowPublisher(
                Flowable.fromCallable(() -> MockData.MOCK_BOARDS[(int) boardId.value() - 1])
                        .subscribeOn(Schedulers.io())
        );
    }

    @SuppressWarnings("NewApi")
    @Override
    public Flow.Publisher<List<Board>> getNotDeletedBoards(Account.ID accountId) {
        // TODO Implement
        return FlowAdapters.toFlowPublisher(
                Flowable.fromCallable(() -> Arrays.stream(MockData.MOCK_BOARDS).toList())
                        .subscribeOn(Schedulers.io())
        );
    }
}