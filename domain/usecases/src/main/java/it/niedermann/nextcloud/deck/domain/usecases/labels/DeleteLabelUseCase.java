package it.niedermann.nextcloud.deck.domain.usecases.labels;

import java.util.concurrent.CompletableFuture;

import it.niedermann.nextcloud.deck.domain.model.Label;
import jakarta.inject.Inject;

public class DeleteLabelUseCase {

    @Inject
    public DeleteLabelUseCase() {
    }

    public CompletableFuture<Void> execute(Label.ID labelId) {
        // TODO: LabelRepository does not have deleteLabel method yet.
        return CompletableFuture.completedFuture(null);
    }
}
