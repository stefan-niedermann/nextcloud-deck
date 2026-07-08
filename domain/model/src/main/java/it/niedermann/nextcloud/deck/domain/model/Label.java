package it.niedermann.nextcloud.deck.domain.model;

import java.awt.Color;

public record Label(
        long id,
        String title,
        Color color
) {
}
