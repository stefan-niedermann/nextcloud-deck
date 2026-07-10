package it.niedermann.nextcloud.deck.domain.usecases.columns;

import java.util.concurrent.CompletableFuture;

import it.niedermann.nextcloud.deck.domain.model.CreateColumn;
import it.niedermann.nextcloud.deck.domain.repository.ColumnRepository;
import jakarta.inject.Inject;

public class AddColumnUseCase {

    private final ColumnRepository columnRepository;

    @Inject
    public AddColumnUseCase(
            ColumnRepository columnRepository
    ) {
        this.columnRepository = columnRepository;
    }

    public CompletableFuture<Void> execute(CreateColumn column) {
        return columnRepository.createColumn(column);
    }
}
