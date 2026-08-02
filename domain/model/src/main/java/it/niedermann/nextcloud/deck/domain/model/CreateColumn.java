package it.niedermann.nextcloud.deck.domain.model;

import java.util.Objects;

public record CreateColumn(Board.ID id,
                           String title,
                           int order) {

    public CreateColumn {
        Objects.requireNonNull(id);
        Objects.requireNonNull(title);
    }
}
