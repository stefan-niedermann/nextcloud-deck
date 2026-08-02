package it.niedermann.nextcloud.deck.domain.model;

import java.time.OffsetDateTime;
import java.util.Objects;

public record User(
        User.ID id,
        User.RemoteID remoteId,
        String displayName,
        Account.ID accountId,
        DBStatus status,
        OffsetDateTime lastModified
) {

    public User(User.ID id, String displayName) {
        this(id, null, displayName, null, DBStatus.UP_TO_DATE, OffsetDateTime.now());
    }

    public User {
        Objects.requireNonNull(id);
        Objects.requireNonNull(displayName);
        Objects.requireNonNull(status);
    }

    public record ID(String value) {
        public ID {
            Objects.requireNonNull(value);
        }
    }

    public record RemoteID(String value) {
        public RemoteID {
            Objects.requireNonNull(value);
        }
    }
}
