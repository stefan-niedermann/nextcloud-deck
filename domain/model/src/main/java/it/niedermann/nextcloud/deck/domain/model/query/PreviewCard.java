package it.niedermann.nextcloud.deck.domain.model.query;

import java.awt.Color;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

import it.niedermann.nextcloud.deck.domain.model.Card;

/// @implSpec Excerpt is a shortened description (e.g. 300 characters)
public record PreviewCard(
        Card.ID id,
        String title,
        String excerpt,
        Set<LabelPreview> labels,
        int commentCount,
        int attachmentCount,
        int assigneeCount,
        int checkboxDoneCount,
        int checkboxTotalCount,
        LocalDateTime dueDate,
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

    public int labelCount() {
        return labels().size();
    }
}
