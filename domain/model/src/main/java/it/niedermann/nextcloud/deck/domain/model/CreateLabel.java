package it.niedermann.nextcloud.deck.domain.model;

import java.util.Objects;

public record CreateLabel(
        Board.ID boardId,
        String title,
        Color color
) {

    public CreateLabel {
        Objects.requireNonNull(boardId);
        Objects.requireNonNull(title);
        Objects.requireNonNull(color);
    }
}
