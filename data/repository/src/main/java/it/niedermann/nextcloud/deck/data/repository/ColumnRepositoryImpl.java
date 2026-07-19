package it.niedermann.nextcloud.deck.data.repository;

import org.reactivestreams.FlowAdapters;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;

import io.reactivex.rxjava3.core.Flowable;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.Column;
import it.niedermann.nextcloud.deck.domain.model.CreateColumn;
import it.niedermann.nextcloud.deck.domain.repository.ColumnRepository;

public class ColumnRepositoryImpl implements ColumnRepository {

    @Override
    public CompletableFuture<Void> createColumn(CreateColumn column) {
        System.out.println("Successfully added column: " + column);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> updateColumn(Column column) {
        System.out.println("Successfully updated column: " + column);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public Flow.Publisher<List<Column.ID>> getColumns(Board.ID boardId) {
        return FlowAdapters.toFlowPublisher(Flowable.just(
                Arrays.stream(MockData.MOCK_COLUMNS).filter(column -> Objects.equals(column.boardId(), boardId)).map(Column::id).toList()));
    }

    @Override
    public Flow.Publisher<Column> getColumn(Column.ID columnId) {
        return FlowAdapters.toFlowPublisher(Flowable.just(
                Arrays.stream(MockData.MOCK_COLUMNS).filter(column -> Objects.equals(column.id(), columnId)).findAny().get()));
    }
}