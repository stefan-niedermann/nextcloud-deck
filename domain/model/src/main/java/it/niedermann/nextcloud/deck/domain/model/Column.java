package it.niedermann.nextcloud.deck.domain.model;

import java.util.Objects;

public record Column(Column.ID id,
                     String title) {

    public Column {
        for (final var o : new Object[]{
                id,
                title,
        }) {
            Objects.requireNonNull(o);
        }
    }

    public record ID(long value) {
    }
}
