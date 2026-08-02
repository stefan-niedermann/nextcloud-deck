package it.niedermann.nextcloud.deck.domain.model.query;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.Set;

import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.model.Color;

/// @implSpec Excerpt is a shortened description (e.g. 300 characters)
public record PreviewCard(
        Card.ID id,
        Card.RemoteID remoteId,
        String title,
        String excerpt,
        Set<LabelPreview> labels,
        int commentCount,
        int attachmentCount,
        int assigneeCount,
        boolean assignedToMe,
        int checkboxDoneCount,
        int checkboxTotalCount,
        OffsetDateTime dueDate,
        Color color
) implements Serializable {

    public PreviewCard {
        for (final var o : new Object[]{
                id,
                title,
                excerpt,
                labels,
        }) {
            Objects.requireNonNull(o);
        }
    }

    public record LabelPreview(
            String title,
            Color color
    ) implements Serializable {
        public LabelPreview {
            Objects.requireNonNull(title);
            Objects.requireNonNull(color);
        }
    }
}
