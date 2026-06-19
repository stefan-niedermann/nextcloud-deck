package it.niedermann.nextcloud.deck.domain.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Optional;

public record Comment(
        long id,
        User author,
        LocalDateTime created,
        String message,
        Optional<Long> parentId
//                List<Mention>mentions = new ArrayList<>();
) implements Serializable {
}
