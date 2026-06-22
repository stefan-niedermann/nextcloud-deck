package it.niedermann.nextcloud.deck.domain.usecases.cards;

import java.util.concurrent.CompletableFuture;

import it.niedermann.nextcloud.deck.domain.repository.CardRepository;
import jakarta.inject.Inject;

public class DeleteCardUseCase {

    private final CardRepository cardRepository;

    @Inject
    public DeleteCardUseCase(
            CardRepository cardRepository
    ) {
        this.cardRepository = cardRepository;
    }

    public CompletableFuture<Void> execute(long cardId) {
        return cardRepository.deleteCard(cardId);
    }
}
