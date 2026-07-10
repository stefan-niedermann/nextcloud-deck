package it.niedermann.nextcloud.deck.domain.model;

import java.util.Objects;

public record CreateComment(
        Card.ID cardId,
        String message,
        Comment.ID parentId
) {

    public CreateComment {
        for (final var o : new Object[]{
                cardId,
                message,
        }) {
            Objects.requireNonNull(o);
        }
    }

    public CreateComment(Card.ID cardId, String message) {
        this(cardId, message, null);
    }
}
