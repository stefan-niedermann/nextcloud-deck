package it.niedermann.nextcloud.deck.data.shared;

import java.time.OffsetDateTime;
import java.util.Objects;

import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.model.DBStatus;
import it.niedermann.nextcloud.deck.domain.model.User;

public record Attachment(
        Attachment.ID id,
        Card.ID cardId,
        AttachmentType type,
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
        OffsetDateTime lastModified
) {

    public Attachment(Attachment.ID id, Card.ID cardId, AttachmentType type, String title, OffsetDateTime createdAt, User.ID createdBy, FileSize size, String mimetype) {
        this(id, cardId, type, title, createdAt, size, mimetype, null, null, createdBy, null, null, null, null, null, null, null, DBStatus.UP_TO_DATE, OffsetDateTime.now());
    }

    public Attachment {
        Objects.requireNonNull(id);
        Objects.requireNonNull(cardId);
        Objects.requireNonNull(type);
        Objects.requireNonNull(status);
        Objects.requireNonNull(lastModified);
    }

    public record ID(long value) {
        public static ID from(long value) {
            return new ID(value);
        }
    }

    public record RemoteID(long value) {
        public static RemoteID from(long value) {
            return new RemoteID(value);
        }
    }

    public record FileSize(long bytes) {
    }
}
