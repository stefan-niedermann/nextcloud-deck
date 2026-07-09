package it.niedermann.nextcloud.deck.domain.model;

import java.util.Objects;

public record CreateCard(
        Column.ID columnId,
        String title
) {

    public CreateCard {
        for (final var o : new Object[]{
                columnId,
                title,
        }) {
            Objects.requireNonNull(o);
        }
    }

    public record ID(long value) {
    }
}
