package it.niedermann.nextcloud.deck.domain.usecases.columns;

import java.util.concurrent.CompletableFuture;

import it.niedermann.nextcloud.deck.domain.model.Column;
import jakarta.inject.Inject;

public class DeleteColumnUseCase {

    @Inject
    public DeleteColumnUseCase() {
    }

    public CompletableFuture<Void> execute(Column.ID columnId) {
        // TODO: ColumnRepository does not have deleteColumn method yet.
        return CompletableFuture.completedFuture(null);
    }
}
