package it.niedermann.nextcloud.deck.domain.model;

import java.awt.Color;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record Card(
        long id,
        long accountId,
        long boardId,
        long columnId,
        LocalDateTime createdAt,
        LocalDateTime deletedAt,
        int order,
        String title,
        String description,
        Set<Label> labels,
        Set<User> assignees,
        List<Attachment> attachments,
        LocalDateTime startDate,
        LocalDateTime dueDate,
        LocalDateTime done,
        Color color,
        Set<Card> dependents,
        boolean archived,
        boolean notified,
        int overdue,
        int commentsUnread
) implements Serializable {
}
