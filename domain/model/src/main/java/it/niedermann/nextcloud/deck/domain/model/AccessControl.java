package it.niedermann.nextcloud.deck.domain.model;

import java.time.OffsetDateTime;
import java.util.Objects;

public record AccessControl(
        Long type,
        Board.ID boardId,
        boolean owner,
        Board.Permissions permissions,
        User.ID userId,

        Long localId,
        Account.ID accountId,
        AccessControl.RemoteID remoteId,
        DBStatus status,
        OffsetDateTime lastModified,
        OffsetDateTime lastModifiedLocal
) {

    public AccessControl {
        Objects.requireNonNull(boardId);
        Objects.requireNonNull(status);
    }

    public record RemoteID(long value) {
    }
}
