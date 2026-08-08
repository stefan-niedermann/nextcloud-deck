package it.niedermann.nextcloud.deck.domain.model;

import java.io.Serializable;

public record OcsAutocompleteResult(
        String id,
        String label,
        String icon,
        String source,
        String subline,
        String shareWithDisplayNameUnique
) implements Serializable {
}
