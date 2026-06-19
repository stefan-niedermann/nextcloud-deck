package it.niedermann.nextcloud.deck.domain.usecases.cards;

import java.util.concurrent.CompletableFuture;

import it.niedermann.nextcloud.deck.domain.repository.CardRepository;
import jakarta.inject.Inject;

public class UnassignCardUseCase {

    private final CardRepository cardRepository;

    @Inject
    public UnassignCardUseCase(
            CardRepository cardRepository
    ) {
        this.cardRepository = cardRepository;
    }

    public CompletableFuture<Void> execute(long cardId, long userId) {
        return CompletableFuture.completedFuture(null);
    }
}
