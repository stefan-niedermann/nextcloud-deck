package it.niedermann.nextcloud.deck.domain.model;

import java.util.Objects;

public record CreateCard(
        Column.ID columnId,
        String title
) {

    public CreateCard {
        Objects.requireNonNull(columnId);
        Objects.requireNonNull(title);
    }

    public record ID(long value) {
    }
}
