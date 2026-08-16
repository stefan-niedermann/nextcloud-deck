package it.niedermann.nextcloud.deck.app.shared.di.model;

import java.net.URI;

public record BuildConfig(URI helpUri) {

    @Override
    public URI helpUri() {
        return helpUri;
    }
}
