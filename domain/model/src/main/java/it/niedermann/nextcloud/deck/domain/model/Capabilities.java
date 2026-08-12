package it.niedermann.nextcloud.deck.domain.model;

import java.io.Serializable;

public record Capabilities(
        NextcloudVersion nextcloudVersion,
        DeckVersion deckVersion,
        Color themingColor,
        Color themingTextColor,
        boolean tablesEnabled
) implements Serializable {

    public static final Capabilities DEFAULT = new Capabilities(
            new NextcloudVersion("27.0.0", 27, 0, 0),
            new DeckVersion("1.11.0", 1, 11, 0),
            Color.decode("#0082c9"),
            Color.decode("#ffffff"),
            true
    );
}
