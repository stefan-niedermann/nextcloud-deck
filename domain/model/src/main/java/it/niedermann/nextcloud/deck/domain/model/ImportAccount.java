package it.niedermann.nextcloud.deck.domain.model;

import java.net.URL;

public record ImportAccount(URL url, String username, String token) {
}
