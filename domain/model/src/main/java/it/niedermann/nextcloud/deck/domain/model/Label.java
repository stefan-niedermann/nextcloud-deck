package it.niedermann.nextcloud.deck.domain.model;

import java.time.OffsetDateTime;
import java.util.Objects;

public record Label(
        Label.ID id,
        Board.ID boardId,
        String title,
        Color color,

        Label.RemoteID remoteId,
        DBStatus status,
        OffsetDateTime lastModified,
        OffsetDateTime lastModifiedLocal,
        String etag
) {

    public Label(Label.ID id, Board.ID boardId, String title, Color color) {
        this(id, boardId, title, color, null, DBStatus.UP_TO_DATE, OffsetDateTime.now(), OffsetDateTime.now(), null);
    }

    public Label {
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
