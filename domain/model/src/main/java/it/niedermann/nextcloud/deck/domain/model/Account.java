package it.niedermann.nextcloud.deck.domain.model;

import java.net.URL;
import java.util.Objects;

public record Account(
        Account.ID id,
        URL url,
        String username,
        String token,
        String accountName,
        Capabilities capabilities) {

    public Account {
        Objects.requireNonNull(id);
        Objects.requireNonNull(url);
        Objects.requireNonNull(username);
        Objects.requireNonNull(accountName);
        Objects.requireNonNull(capabilities);
    }

    public record ID(long value) {
    }
}