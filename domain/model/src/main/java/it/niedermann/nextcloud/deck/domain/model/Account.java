package it.niedermann.nextcloud.deck.domain.model;

import java.net.URL;
import java.util.Objects;

/// Representation of one account that is bound to a Nextcloud User Account on a specific server instance
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