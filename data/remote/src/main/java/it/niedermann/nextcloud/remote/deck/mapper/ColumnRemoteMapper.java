package it.niedermann.nextcloud.remote.deck.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import it.niedermann.nextcloud.deck.domain.model.Column;
import it.niedermann.nextcloud.remote.deck.dto.ColumnDTO;

@Mapper(uses = {CommonRemoteMapper.class})
public interface ColumnRemoteMapper extends GenericRemoteMapper<ColumnDTO, Column> {

    ColumnRemoteMapper INSTANCE = Mappers.getMapper(ColumnRemoteMapper.class);

    @Override
    @Mapping(target = "id", source = "id")
    @Mapping(target = "boardId", source = "boardId")
    @Mapping(target = "cards", ignore = true)
    @Mapping(target = "etag", source = "etag")
    ColumnDTO toDTO(Column column);

    @Override
    @Mapping(target = "id", source = "id")
    @Mapping(target = "boardId", source = "boardId")
    @Mapping(target = "remoteId", source = "id")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "lastModifiedLocal", ignore = true)
    @Mapping(target = "localId", ignore = true)
    @Mapping(target = "accountId", ignore = true)
    @Mapping(target = "etag", source = "etag")
    Column toTO(ColumnDTO columnDTO);
}
