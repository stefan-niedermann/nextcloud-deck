package it.niedermann.nextcloud.deck.domain.usecases.columns;

import java.util.List;
import java.util.concurrent.Flow;

import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.Column;
import it.niedermann.nextcloud.deck.domain.repository.ColumnRepository;
import jakarta.inject.Inject;

public class ListColumnIDsUseCase {

    private final ColumnRepository columnRepository;

    @Inject
    public ListColumnIDsUseCase(
            ColumnRepository columnRepository
    ) {
        this.columnRepository = columnRepository;
    }

    /// @implSpec Result is ordered ascending by [Column#order()]
    public Flow.Publisher<List<Column.ID>> execute(Board.ID boardId) {
        return columnRepository.getColumnIDs(boardId);
    }
}
