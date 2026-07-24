package it.niedermann.nextcloud.deck.domain.usecases.labels;

import java.util.concurrent.CompletableFuture;

import it.niedermann.nextcloud.deck.domain.model.Label;
import it.niedermann.nextcloud.deck.domain.repository.LabelRepository;
import jakarta.inject.Inject;

public class UpdateLabelUseCase {

    private final LabelRepository labelRepository;

    @Inject
    public UpdateLabelUseCase(LabelRepository labelRepository) {
        this.labelRepository = labelRepository;
    }

    public CompletableFuture<Void> execute(Label label) {
        return labelRepository.updateLabel(label);
    }
}
