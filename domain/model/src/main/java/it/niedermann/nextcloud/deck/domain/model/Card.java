package it.niedermann.nextcloud.deck.domain.model;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import io.soabase.recordbuilder.core.RecordBuilder;

@RecordBuilder
public record Card(
        Card.ID id,
        Card.RemoteID remoteId,
        Column.ID columnId,
        OffsetDateTime createdAt,
        int order,
        String title,
        String description,
        String type,
        User.ID ownerId,
        Set<Label.ID> labels,
        Set<User.ID> assignees,
        List<Card.ID> dependents,
        OffsetDateTime startDate,
        OffsetDateTime dueDate,
        OffsetDateTime done,
        Color color,
        boolean archived,
        boolean notified,
        int overdue,
        int commentsUnread,
        DBStatus status,
        OffsetDateTime lastModified,
        String etag
) implements Serializable, CardBuilder.With {

    public Card(Card.ID id, Card.RemoteID remoteId, Column.ID columnId, OffsetDateTime createdAt, int order, String title, String description, String type, User.ID ownerId, Set<Label.ID> labels, Set<User.ID> assignees, List<Card.ID> dependents, boolean archived, boolean notified, int overdue, int commentsUnread) {
        this(id, remoteId, columnId, createdAt, order, title, description, type, ownerId, labels, assignees, dependents, null, null, null, null, archived, notified, overdue, commentsUnread, DBStatus.UP_TO_DATE, OffsetDateTime.now(), null);
    }

    public Card {
        Objects.requireNonNull(id);
        Objects.requireNonNull(columnId);
        Objects.requireNonNull(title);
        Objects.requireNonNull(type);
        Objects.requireNonNull(labels);
        Objects.requireNonNull(assignees);
        Objects.requireNonNull(dependents);
        Objects.requireNonNull(status);
    }

    public Card assign(User.ID userId) {
        final var newAssignees = new HashSet<>(assignees());
        newAssignees.add(userId);
        return withAssignees(newAssignees);
    }

    public Card unassign(User.ID userId) {
        return withAssignees(assignees().stream()
                .filter(id -> !Objects.equals(id, userId))
                .collect(Collectors.toSet()));
    }

    public record ID(long value) {
    }

    public record RemoteID(long value) {
    }
}
