package it.niedermann.nextcloud.deck.domain.model;

import java.awt.Color;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import io.soabase.recordbuilder.core.RecordBuilder;

@RecordBuilder
public record Card(
        Card.ID id,
        Account.ID accountId,
        Board.ID boardId,
        Column.ID columnId,
        LocalDateTime createdAt,
        int order,
        String title,
        String description,
        Set<Label.ID> labels,
        Set<User.ID> assignees,
        List<Comment.ID> comments,
        List<Attachment.ID> attachments,
        List<Card.ID> dependents,
        LocalDateTime startDate,
        LocalDateTime dueDate,
        LocalDateTime done,
        Color color,
        boolean archived,
        boolean notified,
        int overdue,
        int commentsUnread
) implements Serializable, CardBuilder.With {

    public Card {
        for (final var o : new Object[]{
                id,
                accountId,
                boardId,
                columnId,
                createdAt,
                title,
                description,
                labels,
                assignees,
                comments,
                attachments,
                dependents,
        }) {
            Objects.requireNonNull(o);
        }
    }

    public record ID(long value) {
    }
}
