package it.niedermann.nextcloud.remote.deck.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import it.niedermann.nextcloud.deck.domain.model.Label;
import it.niedermann.nextcloud.remote.deck.dto.LabelDTO;

@Mapper(uses = {CommonRemoteMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LabelRemoteMapper extends GenericRemoteMapper<LabelDTO, Label> {

    LabelRemoteMapper INSTANCE = Mappers.getMapper(LabelRemoteMapper.class);

    @Override
    @Mapping(target = "id", source = "id")
    @Mapping(target = "color", source = "color")
    @Mapping(target = "etag", ignore = true)
    LabelDTO toDTO(Label label);

    @Override
    @Mapping(target = "id", source = "id")
    @Mapping(target = "boardId", expression = "java(new it.niedermann.nextcloud.deck.domain.model.Board.ID(0L))")
    @Mapping(target = "remoteId", source = "id")
    @Mapping(target = "status", expression = "java(it.niedermann.nextcloud.deck.domain.model.DBStatus.UP_TO_DATE)")
    @Mapping(target = "lastModified", ignore = true)
    @Mapping(target = "lastModifiedLocal", ignore = true)
    @Mapping(target = "color", source = "color")
    Label toTO(LabelDTO labelDTO);
}
