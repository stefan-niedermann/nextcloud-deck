package it.niedermann.nextcloud.deck.domain.model;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.Optional;

public record Attachment(
        Attachment.ID id,
        Card.ID cardId,
        String title,
        OffsetDateTime createdAt,
        FileSize size,
        String mimetype,
        String dirname,
        String basename,

        User.ID createdBy,
        OffsetDateTime deletedAt,
        String extension,
        String filename,
        String localPath,
        Long fileId,

        Account.ID accountId,
        Attachment.RemoteID remoteId,
        DBStatus status,
        OffsetDateTime lastModified,
        OffsetDateTime lastModifiedLocal
) {

    public Attachment(Attachment.ID id, Card.ID cardId, String title, OffsetDateTime createdAt, User.ID createdBy, FileSize size, String mimetype) {
        this(id, cardId, title, createdAt, size, mimetype, null, null, createdBy, null, null, null, null, null, null, null, DBStatus.UP_TO_DATE, OffsetDateTime.now(), OffsetDateTime.now());
    }

    public Attachment {
        Objects.requireNonNull(id);
        Objects.requireNonNull(cardId);
        Objects.requireNonNull(title);
        Objects.requireNonNull(size);
        Objects.requireNonNull(status);
    }

    public FileSize fileSize() {
        return size;
    }

    public Optional<Path> localCachePath() {
        return localPath != null ? Optional.of(Paths.get(localPath)) : Optional.empty();
    }

    public Optional<Path> localFullPath() {
        // Just as a placeholder for compatibility if needed, or implement logic if available
        return Optional.empty();
    }

    public record ID(long value) {
    }

    public record RemoteID(long value) {
    }

    public record FileSize(long bytes) {
    }
}
