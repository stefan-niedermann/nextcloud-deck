package it.niedermann.nextcloud.deck.domain.usecases.columns;

import java.util.concurrent.CompletableFuture;

import it.niedermann.nextcloud.deck.domain.model.Column;
import it.niedermann.nextcloud.deck.domain.repository.ColumnRepository;
import jakarta.inject.Inject;

public class UpdateColumnUseCase {

    private final ColumnRepository columnRepository;

    @Inject
    public UpdateColumnUseCase(ColumnRepository columnRepository) {
        this.columnRepository = columnRepository;
    }

    public CompletableFuture<Void> execute(Column column) {
        return columnRepository.updateColumn(column);
    }
}
