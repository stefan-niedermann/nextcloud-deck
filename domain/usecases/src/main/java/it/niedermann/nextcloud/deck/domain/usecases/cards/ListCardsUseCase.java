package it.niedermann.nextcloud.deck.domain.usecases.cards;

import java.util.List;
import java.util.concurrent.Flow;

import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.repository.CardRepository;
import jakarta.inject.Inject;

public class ListCardsUseCase {

    private final CardRepository cardRepository;

    @Inject
    public ListCardsUseCase(
            CardRepository cardRepository
    ) {
        this.cardRepository = cardRepository;
    }

    public Flow.Publisher<List<Card>> execute(long columnId) {
        return cardRepository.getNotDeletedCards(columnId);
    }
}
