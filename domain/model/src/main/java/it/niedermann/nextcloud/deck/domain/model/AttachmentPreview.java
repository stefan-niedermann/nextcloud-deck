package it.niedermann.nextcloud.deck.domain.model;

import java.io.Serializable;

/**
 * Actual image content of an attachment preview
 */
public record AttachmentPreview(
        String mimeType,
        String eTag,
        int sizeInPx,
        byte[] content
) implements Serializable {
}
