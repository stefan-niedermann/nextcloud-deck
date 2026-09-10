package it.niedermann.nextcloud.deck.app.shared.args.card;

import java.net.URL;

import it.niedermann.nextcloud.deck.domain.model.Card;

public sealed interface CardRawArgs {
    record LocalCard(Card.ID cardId) implements CardRawArgs {
    }

    record RemoteCard(Card.RemoteID cardRemoteId) implements CardRawArgs {
    }

    record RemoteAccount(String accountName, Card.RemoteID cardRemoteId) implements CardRawArgs {
    }

    record RemoteServer(URL server, Card.RemoteID cardRemoteId) implements CardRawArgs {
    }
}
