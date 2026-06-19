package it.niedermann.nextcloud.deck.domain.model;

import java.io.Serializable;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Optional;

public record Attachment(
        long cardId,
        String filename,
        LocalDateTime createdAt,
        User createdBy,
        Optional<LocalDateTime> deletedAt,
        long filesize,
        String mimetype,
        Optional<Path> localCachePath,
        Optional<Path> localFullPath
) implements Serializable {
}
