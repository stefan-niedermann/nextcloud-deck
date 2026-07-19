package it.niedermann.nextcloud.deck.domain.usecases.columns;

import java.util.concurrent.Flow;

import it.niedermann.nextcloud.deck.domain.model.Column;
import it.niedermann.nextcloud.deck.domain.repository.ColumnRepository;
import jakarta.inject.Inject;

public class GetColumnUseCase {

    private final ColumnRepository columnRepository;

    @Inject
    public GetColumnUseCase(
            ColumnRepository columnRepository
    ) {
        this.columnRepository = columnRepository;
    }

    public Flow.Publisher<Column> execute(Column.ID columnId) {
        return columnRepository.getColumn(columnId);
    }
}
