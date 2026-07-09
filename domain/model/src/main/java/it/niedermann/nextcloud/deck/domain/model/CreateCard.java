package it.niedermann.nextcloud.deck.domain.model;

import java.io.Serializable;

public record CreateCard(
        Column.ID columnId,
        String title
) implements Serializable {

    public record ID(long value) {
    }
}
