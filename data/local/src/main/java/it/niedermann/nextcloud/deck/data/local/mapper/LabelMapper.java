package it.niedermann.nextcloud.deck.data.local.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import it.niedermann.nextcloud.deck.data.local.entity.LabelEntity;
import it.niedermann.nextcloud.deck.domain.model.Label;

@Mapper(uses = {CommonLocalMapper.class})
public interface LabelMapper extends GenericMapper<LabelEntity, Label> {

    LabelMapper INSTANCE = Mappers.getMapper(LabelMapper.class);

    @Override
    @Mapping(target = "boardId", source = "boardId")
    @Mapping(target = "remoteId", source = "remoteId")
    @Mapping(target = "etag", ignore = true)
    @Mapping(target = "accountId", ignore = true)
    LabelEntity toEntity(Label label);

    @Override
    @Mapping(target = "boardId", source = "boardId")
    @Mapping(target = "remoteId", source = "remoteId")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    @Mapping(target = "lastModifiedLocal", ignore = true)
    Label toTO(LabelEntity labelEntity);
}
