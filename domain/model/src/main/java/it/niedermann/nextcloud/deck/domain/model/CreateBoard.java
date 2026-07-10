package it.niedermann.nextcloud.deck.domain.model;

import java.util.Objects;

public record CreateBoard(
        Account.ID accountId,
        String title
) {

    public CreateBoard {
        for (final var o : new Object[]{
                accountId,
                title,
        }) {
            Objects.requireNonNull(o);
        }
    }
}
