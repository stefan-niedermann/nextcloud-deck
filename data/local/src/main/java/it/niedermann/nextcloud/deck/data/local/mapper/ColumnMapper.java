package it.niedermann.nextcloud.deck.data.local.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import it.niedermann.nextcloud.deck.data.local.entity.ColumnEntity;
import it.niedermann.nextcloud.deck.domain.model.Column;

@Mapper(uses = {CommonLocalMapper.class})
public interface ColumnMapper extends GenericMapper<ColumnEntity, Column> {

    ColumnMapper INSTANCE = Mappers.getMapper(ColumnMapper.class);

    @Override
    @Mapping(target = "localId", source = "id")
    @Mapping(target = "boardId", source = "boardId")
    @Mapping(target = "accountId", source = "accountId")
    @Mapping(target = "remoteId", source = "remoteId")
    @Mapping(target = "status", expression = "java(column.status().getId())")
    ColumnEntity toEntity(Column column);

    @Override
    @Mapping(target = "id", source = "localId")
    @Mapping(target = "boardId", source = "boardId")
    @Mapping(target = "accountId", source = "accountId")
    @Mapping(target = "remoteId", source = "remoteId")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "lastModified", source = "lastModified")
    @Mapping(target = "lastModifiedLocal", source = "lastModifiedLocal")
    Column toTO(ColumnEntity entity);
}
