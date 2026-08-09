package it.niedermann.nextcloud.deck.domain.model;

import java.io.Serializable;

public record Capabilities(
        NextcloudVersion nextcloudVersion,
        DeckVersion deckVersion,
        Color themingColor,
        Color themingTextColor,
        boolean tablesEnabled
) implements Serializable {
}
