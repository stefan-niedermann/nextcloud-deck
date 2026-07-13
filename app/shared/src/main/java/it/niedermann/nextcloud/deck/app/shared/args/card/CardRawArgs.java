package it.niedermann.nextcloud.deck.app.shared.args.card;

import java.net.URL;

import it.niedermann.nextcloud.deck.domain.model.Card;

public sealed interface CardRawArgs {
    record LocalCard(Card.ID cardId) implements CardRawArgs {
    }

    record RemoteCard(URL url) implements CardRawArgs {
    }
}