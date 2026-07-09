package it.niedermann.nextcloud.deck.domain.model;

import java.net.URL;

public record Account(
        Account.ID id,
        URL url,
        String username,
        String token,
        String accountName) {

    public record ID(long value) {
    }
}