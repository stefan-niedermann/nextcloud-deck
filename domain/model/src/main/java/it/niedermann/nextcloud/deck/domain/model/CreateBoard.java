package it.niedermann.nextcloud.deck.domain.model;

import java.util.Objects;

public record CreateBoard(
        Account.ID accountId,
        String title
) {

    public CreateBoard {
        Objects.requireNonNull(accountId);
        Objects.requireNonNull(title);
    }
}
