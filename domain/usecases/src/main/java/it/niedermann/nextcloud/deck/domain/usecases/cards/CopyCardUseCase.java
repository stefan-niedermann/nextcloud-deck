package it.niedermann.nextcloud.deck.domain.usecases.cards;

import org.reactivestreams.FlowAdapters;

import java.util.concurrent.CompletableFuture;

import io.reactivex.rxjava3.core.Maybe;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.model.Column;
import it.niedermann.nextcloud.deck.domain.model.CreateCard;
import it.niedermann.nextcloud.deck.domain.repository.CardRepository;
import jakarta.inject.Inject;

public class CopyCardUseCase {

    private final CardRepository cardRepository;

    @Inject
    public CopyCardUseCase(
            CardRepository cardRepository
    ) {
        this.cardRepository = cardRepository;
    }

    public CompletableFuture<Void> execute(Card.ID cardId, Column.ID targetColumnId, int targetOrder) {
        // TODO Ensure that assigned users, labels etc exist for the targetColumnId.
        //  Create if permissions are available before copying, ignore if permissions are not enough
        return Maybe.fromPublisher(FlowAdapters.toPublisher(cardRepository.getCard(cardId)))
                .toCompletionStage()
                .toCompletableFuture()
                .thenComposeAsync(card -> cardRepository.createCard(new CreateCard(targetColumnId, card.title())));
    }
}
