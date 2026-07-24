package it.niedermann.nextcloud.deck.domain.usecases.cards;

import java.util.concurrent.CompletableFuture;

import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.repository.CardRepository;
import jakarta.inject.Inject;

public class UpdateCardUseCase {

    private final CardRepository cardRepository;

    @Inject
    public UpdateCardUseCase(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public CompletableFuture<Void> execute(Card card) {
        return cardRepository.updateCard(card);
    }
}
