package it.niedermann.nextcloud.deck.domain.model;

import java.time.OffsetDateTime;
import java.util.Objects;

public record Activity(
        Activity.ID id,
        Card.ID cardId,
        String subject,
        int type,
        User author,
        OffsetDateTime createdAt,
        Activity.RemoteID remoteId,
        DBStatus status,
        OffsetDateTime lastModified
) {

    public Activity(Activity.ID id, Card.ID cardId, String subject, int type, User author, OffsetDateTime createdAt) {
        this(id, cardId, subject, type, author, createdAt, null, DBStatus.UP_TO_DATE, OffsetDateTime.now());
    }

    public Activity {
        Objects.requireNonNull(id);
        Objects.requireNonNull(cardId);
        Objects.requireNonNull(subject);
        Objects.requireNonNull(status);
    }

    public String actorDisplayName() {
        return author != null ? author.displayName() : null;
    }

    public record ID(long value) {
    }

    public record RemoteID(long value) {
    }
}
