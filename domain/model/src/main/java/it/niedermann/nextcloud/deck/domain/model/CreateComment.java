package it.niedermann.nextcloud.deck.domain.model;

import java.util.Objects;

public record CreateComment(
        Card.ID cardId,
        String message,
        Comment.ID parentId
) {

    public CreateComment {
        Objects.requireNonNull(cardId);
        Objects.requireNonNull(message);
    }

    public CreateComment(Card.ID cardId, String message) {
        this(cardId, message, null);
    }
}
