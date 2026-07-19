package it.niedermann.nextcloud.deck.domain.model;

import java.util.Objects;

public record Column(Column.ID id,
                     Board.ID boardId,
                     String title,
                     int order) {

    public Column {
        for (final var o : new Object[]{
                id,
                boardId,
                title,
        }) {
            Objects.requireNonNull(o);
        }
    }

    public record ID(long value) {
    }
}
