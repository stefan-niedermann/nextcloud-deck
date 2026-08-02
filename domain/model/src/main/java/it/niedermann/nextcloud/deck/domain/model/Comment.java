package it.niedermann.nextcloud.deck.domain.model;

import java.time.OffsetDateTime;
import java.util.Objects;

public record Comment(
        Comment.ID id,
        Card.ID cardId,
        User.ID author,
        OffsetDateTime created,
        String message,
        Long parentId,

        User.ID actorId,
        String actorDisplayName,

        Comment.RemoteID remoteId,
        DBStatus status,
        OffsetDateTime lastModified,
        OffsetDateTime lastModifiedLocal
) {

    public Comment(Comment.ID id, Card.ID cardId, User.ID author, OffsetDateTime created, String message) {
        this(id, cardId, author, created, message, null, null, null, null, DBStatus.UP_TO_DATE, OffsetDateTime.now(), OffsetDateTime.now());
    }

    public Comment {
        Objects.requireNonNull(id);
        Objects.requireNonNull(cardId);
        Objects.requireNonNull(author);
        Objects.requireNonNull(message);
        Objects.requireNonNull(status);
    }

    public record ID(long value) {
    }

    public record RemoteID(long value) {
    }
}
