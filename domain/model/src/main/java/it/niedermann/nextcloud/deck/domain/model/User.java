package it.niedermann.nextcloud.deck.domain.model;

import java.util.Objects;

public record User(
        User.ID id,
        String displayName
) {

    public User(User.ID id) {
        this(id, id.value());
    }

    public User {
        for (final var o : new Object[]{
                id,
                displayName,
        }) {
            Objects.requireNonNull(o);
        }

        if (displayName.isBlank()) {
            throw new IllegalArgumentException("displayName must not be blank");
        }
    }

    public record ID(String value) {
    }
}
