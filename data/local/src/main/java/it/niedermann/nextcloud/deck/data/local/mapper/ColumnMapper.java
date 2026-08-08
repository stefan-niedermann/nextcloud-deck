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
    @Mapping(target = "boardId", source = "boardId")
    @Mapping(target = "accountId", source = "accountId")
    @Mapping(target = "remoteId", source = "remoteId")
    ColumnEntity toEntity(Column column);

    @Override
    @Mapping(target = "boardId", source = "boardId")
    @Mapping(target = "accountId", source = "accountId")
    @Mapping(target = "remoteId", source = "remoteId")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "lastModifiedLocal", ignore = true)
    Column toTO(ColumnEntity columnEntity);
}
