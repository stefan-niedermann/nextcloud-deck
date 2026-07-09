package it.niedermann.nextcloud.deck.domain.model;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.Objects;

public record Activity(
        Activity.ID id,
        Card.ID cardId,
        String subject,
        User author,
        URL icon,
        LocalDateTime createdAt
) {

    public Activity {
        for (final var o : new Object[]{
                id,
                cardId,
                subject,
                author,
                icon,
                createdAt,
        }) {
            Objects.requireNonNull(o);
        }
    }

    public record ID(long value) {
    }
}
