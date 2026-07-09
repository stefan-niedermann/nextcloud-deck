package it.niedermann.nextcloud.deck.domain.usecases.cards;

import org.reactivestreams.FlowAdapters;

import java.util.concurrent.CompletableFuture;

import io.reactivex.rxjava3.core.Maybe;
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

    public CompletableFuture<Void> execute(Card.ID cardId, Column.ID targetColumnId, int targetOrder) {
        return Maybe.fromPublisher(FlowAdapters.toPublisher(cardRepository.getCard(cardId)))
                .toCompletionStage()
                .toCompletableFuture()
                .thenApplyAsync(card -> card
                        .withColumnId(targetColumnId)
                        .withOrder(targetOrder))
                .thenComposeAsync(cardRepository::updateCard);
    }
}
