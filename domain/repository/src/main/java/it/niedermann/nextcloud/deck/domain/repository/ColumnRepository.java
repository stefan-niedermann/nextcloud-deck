package it.niedermann.nextcloud.deck.domain.repository;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;

import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.Column;
import it.niedermann.nextcloud.deck.domain.model.CreateColumn;

public interface ColumnRepository {

    CompletableFuture<Void> createColumn(CreateColumn column);

    CompletableFuture<Void> updateColumn(Column column);

    /// @implSpec Result is ordered ascending by [Column#order()]
    Flow.Publisher<List<Column.ID>> getColumns(Board.ID boardId);

    Flow.Publisher<Column> getColumn(Column.ID columnId);
}
