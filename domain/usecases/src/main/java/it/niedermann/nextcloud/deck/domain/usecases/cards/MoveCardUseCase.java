package it.niedermann.nextcloud.deck.domain.usecases.cards;

import java.util.concurrent.CompletableFuture;

import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.model.Column;
import it.niedermann.nextcloud.deck.domain.repository.CardRepository;
import jakarta.inject.Inject;

public class MoveCardUseCase {

    private final CardRepository cardRepository;

    @Inject
    public MoveCardUseCase(
            CardRepository cardRepository
    ) {
        this.cardRepository = cardRepository;
    }

    public CompletableFuture<Void> execute(Card card, Column.ID targetColumnId, int targetOrder) {
        final var movedCard = card
                .withColumnId(targetColumnId)
                .withOrder(targetOrder);

        return cardRepository.updateCard(movedCard);
    }
}
