package it.niedermann.nextcloud.deck.domain.model;

import java.awt.Color;
import java.util.Objects;

public record Label(
        Label.ID id,
        Board.ID boardId,
        String title,
        Color color
) {

    public Label {
        for (final var o : new Object[]{
                id,
                boardId,
                title,
                color,
        }) {
            Objects.requireNonNull(o);
        }
    }

    public record ID(long value) {
    }
}
