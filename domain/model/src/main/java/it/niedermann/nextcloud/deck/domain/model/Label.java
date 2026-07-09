package it.niedermann.nextcloud.deck.domain.model;

import java.awt.Color;

public record Label(
        Label.ID id,
        String title,
        Color color
) {
    public record ID(long value) {
    }
}
