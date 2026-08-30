package it.niedermann.nextcloud.deck.domain.usecases.labels;

import java.util.Collection;
import java.util.concurrent.Flow;

import it.niedermann.nextcloud.deck.domain.model.Label;
import it.niedermann.nextcloud.deck.domain.repository.LabelRepository;
import jakarta.inject.Inject;

public class SearchLabelsUseCase {

    private final LabelRepository labelRepository;

    @Inject
    public SearchLabelsUseCase(LabelRepository labelRepository) {
        this.labelRepository = labelRepository;
    }

    // TODO Add Board.ID argument as filter criteria
    public Flow.Publisher<Collection<Label>> execute(String query) {
        return labelRepository.find(query);
    }
}
