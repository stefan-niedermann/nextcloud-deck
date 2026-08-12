package it.niedermann.nextcloud.remote.deck.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.remote.deck.dto.CardDTO;

import java.time.OffsetDateTime;

@Mapper(uses = {CommonRemoteMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CardRemoteMapper extends GenericRemoteMapper<CardDTO, Card> {

    CardRemoteMapper INSTANCE = Mappers.getMapper(CardRemoteMapper.class);

    @Override
    @Mapping(target = "id", source = "id")
    @Mapping(target = "stackId", source = "columnId")
    @Mapping(target = "labels", ignore = true)
    @Mapping(target = "assignedUsers", ignore = true)
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "etag", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "duedate", source = "dueDate")
    @Mapping(target = "attachmentCount", ignore = true)
    CardDTO toDTO(Card card);

    @Override
    @Mapping(target = "id", expression = "java(mapId(cardDTO.getId()))")
    @Mapping(target = "columnId", expression = "java(mapColumnId(cardDTO.getStackId()))")
    @Mapping(target = "remoteId", source = "id")
    @Mapping(target = "status", expression = "java(it.niedermann.nextcloud.deck.domain.model.DBStatus.UP_TO_DATE)")
    @Mapping(target = "createdAt", expression = "java(mapTimestamp(cardDTO.getCreatedAt()))")
    @Mapping(target = "lastModified", expression = "java(mapTimestamp(cardDTO.getLastModified()))")
    @Mapping(target = "labels", expression = "java(java.util.Collections.emptySet())")
    @Mapping(target = "assignees", expression = "java(java.util.Collections.emptySet())")
    @Mapping(target = "dependents", expression = "java(java.util.Collections.emptyList())")
    @Mapping(target = "color", ignore = true)
    @Mapping(target = "archived", source = "archived")
    @Mapping(target = "startDate", ignore = true)
    @Mapping(target = "title", expression = "java(cardDTO.getTitle() != null ? cardDTO.getTitle() : \"Untitled\")")
    @Mapping(target = "dueDate", source = "duedate")
    @Mapping(target = "notified", ignore = true)
    @Mapping(target = "with", ignore = true)
    @Mapping(target = "withId", ignore = true)
    @Mapping(target = "withRemoteId", ignore = true)
    @Mapping(target = "withColumnId", ignore = true)
    @Mapping(target = "withCreatedAt", ignore = true)
    @Mapping(target = "withOrder", ignore = true)
    @Mapping(target = "withTitle", ignore = true)
    @Mapping(target = "withDescription", ignore = true)
    @Mapping(target = "withLabels", ignore = true)
    @Mapping(target = "withAssignees", ignore = true)
    @Mapping(target = "withDependents", ignore = true)
    @Mapping(target = "withStartDate", ignore = true)
    @Mapping(target = "withDueDate", ignore = true)
    @Mapping(target = "withDone", ignore = true)
    @Mapping(target = "withColor", ignore = true)
    @Mapping(target = "withArchived", ignore = true)
    @Mapping(target = "withNotified", ignore = true)
    @Mapping(target = "withOverdue", ignore = true)
    @Mapping(target = "withCommentsUnread", ignore = true)
    @Mapping(target = "withStatus", ignore = true)
    @Mapping(target = "withLastModified", ignore = true)
    @Mapping(target = "assign", ignore = true)
    @Mapping(target = "unassign", ignore = true)
    Card toTO(CardDTO cardDTO);

    default OffsetDateTime mapTimestamp(Long timestamp) {
        if (timestamp == null || timestamp == 0) {
            return OffsetDateTime.now();
        }
        return new CommonRemoteMapper().toOffsetDateTime(timestamp);
    }

    default it.niedermann.nextcloud.deck.domain.model.Card.ID mapId(Long id) {
        return new it.niedermann.nextcloud.deck.domain.model.Card.ID(id != null ? id : 0L);
    }

    default it.niedermann.nextcloud.deck.domain.model.Column.ID mapColumnId(Long id) {
        return new it.niedermann.nextcloud.deck.domain.model.Column.ID(id != null ? id : 0L);
    }
}
