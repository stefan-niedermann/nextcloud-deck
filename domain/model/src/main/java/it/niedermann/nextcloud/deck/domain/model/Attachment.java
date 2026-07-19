package it.niedermann.nextcloud.deck.domain.model;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public record Attachment(
        Attachment.ID id,
        Card.ID cardId,
        String filename,
        LocalDateTime createdAt,
        User.ID createdBy,
        Optional<LocalDateTime> deletedAt,
        FileSize fileSize,
        String mimetype,
        Optional<Path> localCachePath,
        Optional<Path> localFullPath
) {

    public Attachment {
        for (final var o : new Object[]{
                id,
                cardId,
                filename,
                createdAt,
                createdBy,
                fileSize,
        }) {
            Objects.requireNonNull(o);
        }
    }

    public record ID(long value) {
    }

    public record FileSize(long bytes) {
    }
}
