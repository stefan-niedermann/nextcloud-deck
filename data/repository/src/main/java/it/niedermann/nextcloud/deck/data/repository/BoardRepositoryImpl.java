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
import it.niedermann.nextcloud.deck.domain.model.Color;
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
                new Color(0, 103, 158),
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
        return boardDao.getBoardById(board.id().value())
                .thenCompose(oldEntity -> {
                    if (oldEntity == null) {
                        final var future = new CompletableFuture<Void>();
                        future.completeExceptionally(new IllegalArgumentException("Board not found: " + board.id().value()));
                        return future;
                    }
                    final var entity = boardMapper.toEntity(board);
                    final var editedEntity = new BoardEntity(
                            entity.getLocalId(),
                            entity.getAccountId() != 0 ? entity.getAccountId() : oldEntity.getAccountId(),
                            entity.getRemoteId() != null ? entity.getRemoteId() : oldEntity.getRemoteId(),
                            DBStatus.LOCAL_EDITED.getId(),
                            entity.getLastModified(),
                            OffsetDateTime.now(),
                            (entity.getEtag() != null && !entity.getEtag().isBlank()) ? entity.getEtag() : oldEntity.getEtag(),
                            entity.getTitle(),
                            entity.getOwnerId(),
                            entity.getColor(),
                            entity.getArchived(),
                            entity.getShared(),
                            entity.getDeletedAt(),
                            entity.getPermissionRead(),
                            entity.getPermissionEdit(),
                            entity.getPermissionManage(),
                            entity.getPermissionShare(),
                            entity.getConflictWithId()
                    );
                    return boardDao.updateRx(editedEntity).thenApply(v -> null);
                });
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

    @Override
    public CompletableFuture<Void> deleteBoard(Board.ID boardId) {
        return boardDao.getBoardById(boardId.value())
                .thenCompose(entity -> {
                    if (entity == null) {
                        return CompletableFuture.completedFuture(null);
                    }
                    if (entity.getRemoteId() == null) {
                        return boardDao.deleteRx(entity);
                    } else {
                        final var deletedEntity = new BoardEntity(
                                entity.getLocalId(),
                                entity.getAccountId(),
                                entity.getRemoteId(),
                                DBStatus.LOCAL_DELETED.getId(),
                                entity.getLastModified(),
                                OffsetDateTime.now(),
                                entity.getEtag(),
                                entity.getTitle(),
                                entity.getOwnerId(),
                                entity.getColor(),
                                entity.getArchived(),
                                entity.getShared(),
                                OffsetDateTime.now(),
                                entity.getPermissionRead(),
                                entity.getPermissionEdit(),
                                entity.getPermissionManage(),
                                entity.getPermissionShare(),
                                entity.getConflictWithId()
                        );
                        return boardDao.updateRx(deletedEntity);
                    }
                });
    }
}
