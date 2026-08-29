package it.niedermann.nextcloud.deck.data.repository;

import org.reactivestreams.FlowAdapters;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.schedulers.Schedulers;
import it.niedermann.nextcloud.deck.data.local.dao.BoardDao;
import it.niedermann.nextcloud.deck.data.local.dao.ColumnDao;
import it.niedermann.nextcloud.deck.data.local.entity.ColumnEntity;
import it.niedermann.nextcloud.deck.data.local.mapper.ColumnMapper;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.Column;
import it.niedermann.nextcloud.deck.domain.model.CreateColumn;
import it.niedermann.nextcloud.deck.domain.model.DBStatus;
import it.niedermann.nextcloud.deck.domain.repository.ColumnRepository;
import jakarta.inject.Inject;

public class ColumnRepositoryImpl implements ColumnRepository {

    private final ColumnDao columnDao;
    private final BoardDao boardDao;
    private final ColumnMapper columnMapper;

    @Inject
    public ColumnRepositoryImpl(ColumnDao columnDao,
                                BoardDao boardDao,
                                ColumnMapper columnMapper) {
        this.columnDao = columnDao;
        this.boardDao = boardDao;
        this.columnMapper = columnMapper;
    }

    @Override
    public CompletableFuture<Void> createColumn(CreateColumn column) {
        return boardDao.getBoardById(column.id().value())
                .thenCompose(board -> {
                    if (board == null) {
                        throw new IllegalArgumentException("Board not found: " + column.id());
                    }
                    final var entity = new ColumnEntity(
                            0,
                            board.getAccountId(),
                            null,
                            DBStatus.LOCAL_EDITED.getId(),
                            null,
                            OffsetDateTime.now(),
                            null,
                            column.id().value(),
                            column.title(),
                            column.order(),
                            false,
                            null,
                            null
                    );
                    return columnDao.insert(entity).thenApply(v -> null);
                });
    }

    @Override
    public CompletableFuture<Void> updateColumn(Column column) {
        final var entity = columnMapper.toEntity(column);
        final var editedEntity = new ColumnEntity(
                entity.getLocalId(),
                entity.getAccountId(),
                entity.getRemoteId(),
                DBStatus.LOCAL_EDITED.getId(),
                entity.getLastModified(),
                OffsetDateTime.now(),
                entity.getEtag(),
                entity.getBoardId(),
                entity.getTitle(),
                entity.getOrder(),
                entity.getArchived(),
                entity.getDeletedAt(),
                entity.getConflictWithId()
        );
        return columnDao.updateRx(editedEntity);
    }

    @Override
    public CompletableFuture<Void> deleteColumn(Column.ID columnId) {
        return columnDao.getColumnById(columnId.value())
                .thenCompose(entity -> {
                    if (entity == null) {
                        return CompletableFuture.completedFuture(null);
                    }
                    if (entity.getRemoteId() == null) {
                        return columnDao.deleteRx(entity);
                    } else {
                        final var deletedEntity = new ColumnEntity(
                                entity.getLocalId(),
                                entity.getAccountId(),
                                entity.getRemoteId(),
                                DBStatus.LOCAL_DELETED.getId(),
                                entity.getLastModified(),
                                OffsetDateTime.now(),
                                entity.getEtag(),
                                entity.getBoardId(),
                                entity.getTitle(),
                                entity.getOrder(),
                                entity.getArchived(),
                                OffsetDateTime.now(),
                                entity.getConflictWithId()
                        );
                        return columnDao.updateRx(deletedEntity);
                    }
                });
    }

    @Override
    public Flow.Publisher<List<Column.ID>> getColumnIDs(Board.ID boardId) {
        return FlowAdapters.toFlowPublisher(
                columnDao.getColumnsByBoard(boardId.value())
                        .map(entities -> entities.stream()
                                .map(entity -> new Column.ID(entity.getLocalId()))
                                .collect(Collectors.toList()))
                        .subscribeOn(Schedulers.io())
        );
    }

    @Override
    public Flow.Publisher<List<Column>> getColumns(Board.ID boardId) {
        return FlowAdapters.toFlowPublisher(
                columnDao.getColumnsByBoard(boardId.value())
                        .map(columnMapper::toTOList)
                        .subscribeOn(Schedulers.io())
        );
    }

    @Override
    public Flow.Publisher<Column> getColumn(Column.ID columnId) {
        return FlowAdapters.toFlowPublisher(
                Maybe.fromCompletionStage(columnDao.getColumnById(columnId.value()))
                        .toFlowable()
                        .map(columnMapper::toTO)
                        .subscribeOn(Schedulers.io())
        );
    }
}
