package it.niedermann.nextcloud.deck.domain.model;

import java.net.URL;
import java.util.Objects;

public record Account(
        Account.ID id,
        URL url,
        String username,
        String token,
        String accountName) {

    public Account {
        for (final var o : new Object[]{
                id,
                url,
                username,
                accountName,
        }) {
            Objects.requireNonNull(o);
        }
    }

    public record ID(long value) {
    }
}