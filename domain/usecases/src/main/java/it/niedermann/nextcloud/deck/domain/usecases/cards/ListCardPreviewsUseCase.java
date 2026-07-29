package it.niedermann.nextcloud.deck.domain.usecases.cards;

import java.util.List;
import java.util.concurrent.Flow;

import it.niedermann.nextcloud.deck.domain.model.Column;
import it.niedermann.nextcloud.deck.domain.model.query.PreviewCard;
import it.niedermann.nextcloud.deck.domain.repository.CardRepository;
import jakarta.inject.Inject;

public class ListCardPreviewsUseCase {

    private final CardRepository cardRepository;

    @Inject
    public ListCardPreviewsUseCase(
            CardRepository cardRepository
    ) {
        this.cardRepository = cardRepository;
    }

    public Flow.Publisher<List<PreviewCard>> execute(Column.ID columnId) {
        return cardRepository.getNotDeletedCardPreviews(columnId);
    }
}
