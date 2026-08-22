package it.niedermann.nextcloud.deck.data.repository;

import org.reactivestreams.FlowAdapters;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;

import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.schedulers.Schedulers;
import it.niedermann.nextcloud.deck.data.local.dao.BoardDao;
import it.niedermann.nextcloud.deck.data.local.entity.BoardEntity;
import it.niedermann.nextcloud.deck.data.local.mapper.BoardMapper;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.CreateBoard;
import it.niedermann.nextcloud.deck.domain.model.DBStatus;
import it.niedermann.nextcloud.deck.domain.repository.BoardRepository;
import jakarta.inject.Inject;

public class BoardRepositoryImpl implements BoardRepository {

    private final BoardDao boardDao;
    private final BoardMapper boardMapper;

    @Inject
    public BoardRepositoryImpl(BoardDao boardDao,
                               BoardMapper boardMapper) {
        this.boardDao = boardDao;
        this.boardMapper = boardMapper;
    }

    @Override
    public CompletableFuture<Board.ID> createBoard(CreateBoard board) {
        final var entity = new BoardEntity(
                0,
                board.accountId().value(),
                null,
                DBStatus.LOCAL_EDITED.getId(),
                null,
                OffsetDateTime.now(),
                null,
                board.title(),
                null,
                null,
                false,
                0,
                null,
                true,
                true,
                true,
                true,
                null
        );
        return boardDao.insertOrReplace(entity).thenApply(Board.ID::new);
    }

    @Override
    public CompletableFuture<Void> updateBoard(Board board) {
        return boardDao.updateRx(boardMapper.toEntity(board));
    }

    @Override
    public Flow.Publisher<Board> getBoard(Board.ID boardId) {
        return FlowAdapters.toFlowPublisher(
                Maybe.fromCompletionStage(boardDao.getBoardById(boardId.value()))
                        .toFlowable()
                        .map(boardMapper::toTO)
                        .subscribeOn(Schedulers.io())
        );
    }

    @Override
    public Flow.Publisher<List<Board>> getNotDeletedBoards(Account.ID accountId) {
        return FlowAdapters.toFlowPublisher(
                boardDao.getBoardsByAccount(accountId.value())
                        .map(boardMapper::toTOList)
                        .subscribeOn(Schedulers.io())
        );
    }
}
