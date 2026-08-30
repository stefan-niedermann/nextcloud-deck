package it.niedermann.nextcloud.deck.domain.model;

import java.time.OffsetDateTime;
import java.util.Objects;

/// One comment is attached to a [Card] and can optionally have a parent comment on the same card linked by [#parentId].
public record Comment(
        Comment.ID id,
        Card.ID cardId,
        User.ID author,
        OffsetDateTime created,
        String message,
        Comment.ID parentId,
        Comment.RemoteID remoteId,
        DBStatus status,
        OffsetDateTime lastModified
) {

    public Comment(Comment.ID id, Card.ID cardId, User.ID author, OffsetDateTime created, String message) {
        this(id, cardId, author, created, message, null, null, DBStatus.UP_TO_DATE, OffsetDateTime.now());
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
