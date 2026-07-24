package it.niedermann.nextcloud.deck.domain.usecases.labels;

import java.util.Set;
import java.util.concurrent.Flow;

import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.Label;
import it.niedermann.nextcloud.deck.domain.repository.LabelRepository;
import jakarta.inject.Inject;

public class ListLabelsUseCase {

    private final LabelRepository labelRepository;

    @Inject
    public ListLabelsUseCase(LabelRepository labelRepository) {
        this.labelRepository = labelRepository;
    }

    public Flow.Publisher<Set<Label>> execute(Board.ID boardId) {
        return labelRepository.getNotDeletedLabels(boardId);
    }
}
