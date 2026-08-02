package it.niedermann.nextcloud.deck.domain.model;

import java.time.OffsetDateTime;
import java.util.Objects;

public record Column(
        Column.ID id,
        Board.ID boardId,
        String title,
        int order,
        boolean archived,
        OffsetDateTime deletedAt,

        Long localId,
        Account.ID accountId,
        Column.RemoteID remoteId,
        DBStatus status,
        OffsetDateTime lastModified,
        OffsetDateTime lastModifiedLocal,
        String etag
) {

    public Column(Column.ID id, Board.ID boardId, String title, int order) {
        this(id, boardId, title, order, false, null, null, null, null, DBStatus.UP_TO_DATE, OffsetDateTime.now(), OffsetDateTime.now(), null);
    }

    public Column {
        Objects.requireNonNull(id);
        Objects.requireNonNull(boardId);
        Objects.requireNonNull(title);
        Objects.requireNonNull(status);
    }

    public record ID(long value) {
    }

    public record RemoteID(long value) {
    }
}
