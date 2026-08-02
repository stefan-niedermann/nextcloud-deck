package it.niedermann.nextcloud.deck.domain.model;

import java.time.OffsetDateTime;
import java.util.Objects;

public record Board(
        Board.ID id,
        String title,
        Color color,
        User.ID ownerId,
        boolean archived,
        Permissions permissions,
        Account.ID accountId,
        Board.RemoteID remoteId,
        DBStatus status,
        OffsetDateTime lastModified
) {

    public Board(Board.ID id, String title, Color color, Permissions permissions) {
        this(id, title, color, null, false, permissions, null, null, DBStatus.UP_TO_DATE, OffsetDateTime.now());
    }

    public Board {
        Objects.requireNonNull(id);
        Objects.requireNonNull(title);
        Objects.requireNonNull(permissions);
        Objects.requireNonNull(status);
    }

    public record ID(long value) {
    }

    public record RemoteID(long value) {
    }

    public record Permissions(
            boolean permissionRead,
            boolean permissionEdit,
            boolean permissionManage,
            boolean permissionShare
    ) {
    }
}
