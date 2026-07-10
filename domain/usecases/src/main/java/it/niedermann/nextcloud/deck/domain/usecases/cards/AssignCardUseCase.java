package it.niedermann.nextcloud.deck.domain.usecases.cards;

import org.reactivestreams.FlowAdapters;

import java.util.concurrent.CompletableFuture;

import io.reactivex.rxjava3.core.Flowable;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.model.User;
import it.niedermann.nextcloud.deck.domain.repository.CardRepository;
import jakarta.inject.Inject;

public class AssignCardUseCase {

    private final CardRepository cardRepository;

    @Inject
    public AssignCardUseCase(
            CardRepository cardRepository
    ) {
        this.cardRepository = cardRepository;
    }

    public CompletableFuture<Void> execute(Card.ID cardId, User.ID userId) {
        return Flowable.fromPublisher(FlowAdapters.toPublisher(cardRepository.getCard(cardId)))
                .firstElement()
                .toCompletionStage()
                .toCompletableFuture()
                .thenApplyAsync(card -> card.assign(userId))
                .thenComposeAsync(cardRepository::updateCard);
    }
}
