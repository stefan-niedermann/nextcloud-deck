package it.niedermann.nextcloud.deck.app.shared.args.card;

import java.util.concurrent.CompletableFuture;

import it.niedermann.nextcloud.deck.app.shared.args.ArgsResolver;
import it.niedermann.nextcloud.deck.domain.model.Card;
import jakarta.inject.Inject;

public class CardArgResolver implements ArgsResolver<CardRawArgs, Card.ID> {

    @Inject
    public CardArgResolver() {
    }

    @Override
    public CompletableFuture<Card.ID> resolve(CardRawArgs args) {
        if (args instanceof CardRawArgs.LocalCard localCard) {
            return CompletableFuture.completedFuture(localCard.cardId());
        } else {
            throw new UnsupportedOperationException("Not yet implemented.");
        }
    }
}
