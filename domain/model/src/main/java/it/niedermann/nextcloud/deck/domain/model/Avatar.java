package it.niedermann.nextcloud.deck.domain.model;

public record Avatar(
        String mimeType,
        String eTag,
        int sizeInPx,
        byte[] content
) {
}
