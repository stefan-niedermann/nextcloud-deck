package it.niedermann.nextcloud.deck.domain.model;

import java.net.URL;
import java.time.LocalDateTime;

public record Activity(
        Activity.ID id,
        Card.ID cardId,
        String subject,
        User author,
        URL icon,
        LocalDateTime datetime
) {

    public record ID(long value) {
    }
}
