package it.niedermann.nextcloud.deck.domain.model;

import java.net.URL;
import java.time.LocalDateTime;

public record Activity(long id,
                       long cardId,
                       String subject,
                       User author,
                       URL icon,
                       LocalDateTime datetime
) {
}
