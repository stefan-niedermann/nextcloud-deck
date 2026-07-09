package it.niedermann.nextcloud.deck.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

public record Comment(
        Comment.ID id,
        Card.ID cardId,
        User author,
        LocalDateTime created,
        String message,
        Comment.ID parentId
        // List<Mention>mentions = new ArrayList<>();
) {

    public Comment {
        for (final var o : new Object[]{
                id,
                cardId,
                author,
                created,
                message,
        }) {
            Objects.requireNonNull(o);
        }
    }

    public record ID(long value) {
    }
}
