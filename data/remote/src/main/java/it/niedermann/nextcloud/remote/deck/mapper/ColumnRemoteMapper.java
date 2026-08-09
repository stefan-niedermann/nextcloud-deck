package it.niedermann.nextcloud.remote.deck.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.OffsetDateTime;

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
    @Mapping(target = "id", expression = "java(mapId(columnDTO.getId()))")
    @Mapping(target = "boardId", expression = "java(mapBoardId(columnDTO.getBoardId()))")
    @Mapping(target = "remoteId", source = "id")
    @Mapping(target = "status", expression = "java(it.niedermann.nextcloud.deck.domain.model.DBStatus.UP_TO_DATE)")
    @Mapping(target = "lastModified", expression = "java(mapTimestamp(columnDTO.getLastModified()))")
    @Mapping(target = "lastModifiedLocal", ignore = true)
    @Mapping(target = "localId", ignore = true)
    @Mapping(target = "accountId", ignore = true)
    @Mapping(target = "etag", source = "etag")
    @Mapping(target = "title", expression = "java(columnDTO.getTitle() != null ? columnDTO.getTitle() : \"Untitled\")")
    @Mapping(target = "deletedAt", expression = "java(mapTimestamp(columnDTO.getDeletedAt()))")
    Column toTO(ColumnDTO columnDTO);

    default OffsetDateTime mapTimestamp(Long timestamp) {
        if (timestamp == null || timestamp == 0) {
            return null;
        }
        return new CommonRemoteMapper().toOffsetDateTime(timestamp);
    }

    default it.niedermann.nextcloud.deck.domain.model.Column.ID mapId(Long id) {
        return new it.niedermann.nextcloud.deck.domain.model.Column.ID(id != null ? id : 0L);
    }

    default it.niedermann.nextcloud.deck.domain.model.Board.ID mapBoardId(Long id) {
        return new it.niedermann.nextcloud.deck.domain.model.Board.ID(id != null ? id : 0L);
    }
}
