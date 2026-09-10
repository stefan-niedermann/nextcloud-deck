package it.niedermann.nextcloud.deck.domain.model;

import java.time.OffsetDateTime;
import java.util.Objects;

public record Board(
        Board.ID id,
        String title,
        Color color,
        boolean isOwner,
        boolean archived,
        Permissions permissions,
        Account.ID accountId,
        Board.RemoteID remoteId,
        DBStatus status,
        OffsetDateTime lastModified,
        String etag
) {

    public Board(Board.ID id, String title, Color color, Permissions permissions) {
        this(id, title, color, false, false, permissions, null, null, DBStatus.UP_TO_DATE, OffsetDateTime.now(), null);
    }

    public Board {
        Objects.requireNonNull(id);
        Objects.requireNonNull(title);
        Objects.requireNonNull(permissions);
        Objects.requireNonNull(status);
    }

    @Override
    public Permissions permissions() {
        if (isOwner) {
            return new Permissions(true, true, true, true);
        }
        return permissions;
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
