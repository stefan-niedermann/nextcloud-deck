package it.niedermann.nextcloud.deck.domain.usecases.cards;

import java.util.Collection;
import java.util.concurrent.Flow;

import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.repository.CardRepository;
import jakarta.inject.Inject;

public class SearchCardsUseCase {

    private final CardRepository cardRepository;

    @Inject
    public SearchCardsUseCase(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public Flow.Publisher<Collection<Card>> execute(String query) {
        return execute(query, null);
    }

    public Flow.Publisher<Collection<Card>> execute(String query, Card.ID excludeId) {
        return cardRepository.find(query, excludeId);
    }

    public Flow.Publisher<Collection<Card>> execute(Board.ID boardId, String query, Card.ID excludeId) {
        return cardRepository.find(boardId, query, excludeId);
    }
}
