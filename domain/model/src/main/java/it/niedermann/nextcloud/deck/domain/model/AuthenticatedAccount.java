package it.niedermann.nextcloud.deck.domain.model;

import java.net.URL;

public record AuthenticatedAccount(URL url, String username, String token) {
}
