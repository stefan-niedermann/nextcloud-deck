package it.niedermann.nextcloud.deck.domain.model;

import java.awt.Color;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import io.soabase.recordbuilder.core.RecordBuilder;

@RecordBuilder
public record Card(
        Card.ID id,
        Column.ID columnId,
        LocalDateTime createdAt,
        int order,
        String title,
        String description,
        Set<Label.ID> labels,
        Set<User.ID> assignees,
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
                columnId,
                createdAt,
                title,
                description,
                labels,
                assignees,
                dependents,
        }) {
            Objects.requireNonNull(o);
        }
    }

    public Card assign(User.ID userId) {
        final var newAssignees = new HashSet<>(assignees());
        newAssignees.add(userId);
        return withAssignees(newAssignees);
    }

    public Card unassign(User.ID userId) {
        return withAssignees(assignees().stream()
                .filter(id -> Objects.equals(id, userId))
                .collect(Collectors.toSet()));
    }

    public record ID(long value) {
    }
}
