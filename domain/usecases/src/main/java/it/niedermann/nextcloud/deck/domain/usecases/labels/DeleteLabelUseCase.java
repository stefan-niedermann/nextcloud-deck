package it.niedermann.nextcloud.deck.domain.usecases.labels;

import java.util.concurrent.CompletableFuture;

import it.niedermann.nextcloud.deck.domain.model.Label;
import it.niedermann.nextcloud.deck.domain.repository.LabelRepository;
import jakarta.inject.Inject;

public class DeleteLabelUseCase {

    private final LabelRepository labelRepository;

    @Inject
    public DeleteLabelUseCase(LabelRepository labelRepository) {
        this.labelRepository = labelRepository;
    }

    public CompletableFuture<Void> execute(Label.ID labelId) {
        return labelRepository.deleteLabel(labelId);
    }
}
