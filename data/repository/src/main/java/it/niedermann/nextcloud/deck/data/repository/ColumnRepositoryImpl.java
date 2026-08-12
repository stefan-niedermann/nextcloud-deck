package it.niedermann.nextcloud.deck.data.repository;

import org.reactivestreams.FlowAdapters;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.schedulers.Schedulers;
import it.niedermann.nextcloud.deck.data.local.dao.ColumnDao;
import it.niedermann.nextcloud.deck.data.local.mapper.ColumnMapper;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.Column;
import it.niedermann.nextcloud.deck.domain.model.CreateColumn;
import it.niedermann.nextcloud.deck.domain.repository.ColumnRepository;
import jakarta.inject.Inject;

public class ColumnRepositoryImpl implements ColumnRepository {

    private final ColumnDao columnDao;
    private final ColumnMapper columnMapper;

    @Inject
    public ColumnRepositoryImpl(ColumnDao columnDao,
                                ColumnMapper columnMapper) {
        this.columnDao = columnDao;
        this.columnMapper = columnMapper;
    }

    @Override
    public CompletableFuture<Void> createColumn(CreateColumn column) {
        // TODO: Local-first or Sync?
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> updateColumn(Column column) {
        return columnDao.updateRx(columnMapper.toEntity(column));
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
        return FlowAdapters.toFlowPublisher(Flowable.just(
                Arrays.stream(MockData.MOCK_COLUMNS).filter(column -> Objects.equals(column.boardId(), boardId)).toList()));
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
