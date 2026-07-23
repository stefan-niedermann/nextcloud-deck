package it.niedermann.nextcloud.deck.data.repository;

import org.reactivestreams.FlowAdapters;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
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
        return CompletableFuture.completedFuture(new Board.ID(1));
    }

    @Override
    public CompletableFuture<Void> updateBoard(Board board) {
        // TODO Implement
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public Flow.Publisher<Board> getBoard(Board.ID boardId) {
        // TODO Implement
        return FlowAdapters.toFlowPublisher(Flowable.just(MockData.MOCK_BOARDS[(int) boardId.value() - 1]));
    }

    @SuppressWarnings("NewApi")
    @Override
    public Flow.Publisher<List<Board>> getNotDeletedBoards(Account.ID accountId) {
        // TODO Implement
        return FlowAdapters.toFlowPublisher(Single.just(Arrays.stream(MockData.MOCK_BOARDS).toList()).toFlowable());
    }
}