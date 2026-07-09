package it.niedermann.nextcloud.deck.domain.model;

public record User(
        User.ID id,
        String displayName
) {

    public record ID(String value) {
    }
}
