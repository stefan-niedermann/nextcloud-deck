package it.niedermann.nextcloud.deck.domain.model;

import java.util.Objects;

public record BoardShare(User user, Board.Permissions permissions) {
    public BoardShare {
        Objects.requireNonNull(user);
        Objects.requireNonNull(permissions);
    }
}
