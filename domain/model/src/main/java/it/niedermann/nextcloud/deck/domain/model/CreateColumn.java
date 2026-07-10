package it.niedermann.nextcloud.deck.domain.model;

import java.util.Objects;

public record CreateColumn(Board.ID id,
                           String title,
                           int order) {

    public CreateColumn {
        for (final var o : new Object[]{
                id,
                title,
        }) {
            Objects.requireNonNull(o);
        }
    }
}
