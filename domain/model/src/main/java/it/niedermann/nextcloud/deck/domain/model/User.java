package it.niedermann.nextcloud.deck.domain.model;

import java.time.OffsetDateTime;
import java.util.Objects;

public record User(
        User.ID id,
        String displayName,

        Long localId,
        Account.ID accountId,
        User.RemoteID remoteId,
        DBStatus status,
        OffsetDateTime lastModified,
        OffsetDateTime lastModifiedLocal
) {

    public User(User.ID id, String displayName) {
        this(id, displayName, null, null, null, DBStatus.UP_TO_DATE, OffsetDateTime.now(), OffsetDateTime.now());
    }

    public User {
        Objects.requireNonNull(id);
        Objects.requireNonNull(displayName);
        Objects.requireNonNull(status);
    }

    public record ID(String value) {
        public ID {
            for (final var o : new Object[]{
                   value,
            }) {
                Objects.requireNonNull(o);
            }
        }
    }

    public record RemoteID(String value) {
    }
}
