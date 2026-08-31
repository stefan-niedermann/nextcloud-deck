package it.niedermann.nextcloud.remote.deck.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import it.niedermann.nextcloud.deck.domain.model.Activity;
import it.niedermann.nextcloud.remote.ocs.dto.ActivityDTO;

@Mapper(uses = {CommonRemoteMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ActivityRemoteMapper {

    ActivityRemoteMapper INSTANCE = Mappers.getMapper(ActivityRemoteMapper.class);

    @Mapping(target = "id", source = "activityId")
    @Mapping(target = "remoteId", source = "activityId")
    @Mapping(target = "cardId", source = "objectId")
    @Mapping(target = "subject", source = "subject")
    @Mapping(target = "type", expression = "java(0)") // TODO: Map type string to int
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "createdAt", source = "datetime")
    @Mapping(target = "status", expression = "java(it.niedermann.nextcloud.deck.domain.model.DBStatus.UP_TO_DATE)")
    @Mapping(target = "lastModified", expression = "java(java.time.OffsetDateTime.now())")
    Activity toTO(ActivityDTO dto);

    default OffsetDateTime mapDateTime(String value) {
        if (value == null || value.isEmpty()) return null;
        return OffsetDateTime.parse(value, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }
}
