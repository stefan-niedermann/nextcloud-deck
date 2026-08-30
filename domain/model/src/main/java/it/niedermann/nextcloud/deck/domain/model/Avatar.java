package it.niedermann.nextcloud.deck.domain.model;

/// Actual image content of a user Avatar or account Avatar
public record Avatar(
        String mimeType,
        String eTag,
        int sizeInPx,
        byte[] content
) {
}
