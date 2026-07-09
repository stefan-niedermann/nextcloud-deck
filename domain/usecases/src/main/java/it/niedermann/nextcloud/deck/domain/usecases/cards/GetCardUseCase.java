package it.niedermann.nextcloud.deck.domain.usecases.cards;

import java.util.concurrent.Flow;

import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.repository.CardRepository;
import jakarta.inject.Inject;

public class GetCardUseCase {

    private final CardRepository cardRepository;

    @Inject
    public GetCardUseCase(
            CardRepository cardRepository
    ) {
        this.cardRepository = cardRepository;
    }

    public Flow.Publisher<Card> execute(Card.ID cardId) {
        return cardRepository.getCard(cardId);
    }

//    public Flow.Publisher<List<Card>> execute(long cardRemoteId, String host) {
//        return cardRepository.getCard(cardId);
//    }
//
//    public Flow.Publisher<List<Card>> execute(long cardRemoteId, String host, String accountName) {
//        return cardRepository.getCard(cardId);
//    }
}
