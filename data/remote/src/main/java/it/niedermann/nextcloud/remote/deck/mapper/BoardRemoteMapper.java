package it.niedermann.nextcloud.remote.deck.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.remote.deck.dto.BoardDTO;
import it.niedermann.nextcloud.remote.deck.dto.PermissionsDTO;

import java.time.OffsetDateTime;

@Mapper(uses = {CommonRemoteMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BoardRemoteMapper extends GenericRemoteMapper<BoardDTO, Board> {

    BoardRemoteMapper INSTANCE = Mappers.getMapper(BoardRemoteMapper.class);

    @Override
    @Mapping(target = "id", source = "id")
    @Mapping(target = "lastModified", source = "lastModified")
    @Mapping(target = "permissions", source = "permissions")
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "labels", ignore = true)
    @Mapping(target = "stacks", ignore = true)
    @Mapping(target = "acl", ignore = true)
    @Mapping(target = "users", ignore = true)
    @Mapping(target = "etag", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    BoardDTO toDTO(Board board);

    @Override
    @Mapping(target = "id", expression = "java(mapId(boardDTO.getId()))")
    @Mapping(target = "remoteId", source = "id")
    @Mapping(target = "lastModified", expression = "java(mapTimestamp(boardDTO.getLastModified()))")
    @Mapping(target = "permissions", expression = "java(mapPermissions(boardDTO.getPermissions()))")
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "accountId", ignore = true)
    @Mapping(target = "status", expression = "java(it.niedermann.nextcloud.deck.domain.model.DBStatus.UP_TO_DATE)")
    @Mapping(target = "color", source = "color")
    @Mapping(target = "title", expression = "java(boardDTO.getTitle() != null ? boardDTO.getTitle() : \"Untitled\")")
    Board toTO(BoardDTO boardDTO);

    @Mapping(target = "permissionRead", source = "permissionRead")
    @Mapping(target = "permissionEdit", source = "permissionEdit")
    @Mapping(target = "permissionManage", source = "permissionManage")
    @Mapping(target = "permissionShare", source = "permissionShare")
    Board.Permissions toPermissions(PermissionsDTO dto);

    default Board.Permissions mapPermissions(PermissionsDTO dto) {
        if (dto == null) {
            return new Board.Permissions(true, false, false, false);
        }
        return toPermissions(dto);
    }

    default OffsetDateTime mapTimestamp(Long timestamp) {
        if (timestamp == null || timestamp == 0) {
            return OffsetDateTime.now();
        }
        return new CommonRemoteMapper().toOffsetDateTime(timestamp);
    }

    default Board.ID mapId(Long id) {
        return new Board.ID(id != null ? id : 0L);
    }

    @Mapping(target = "permissionRead", source = "permissionRead")
    @Mapping(target = "permissionEdit", source = "permissionEdit")
    @Mapping(target = "permissionManage", source = "permissionManage")
    @Mapping(target = "permissionShare", source = "permissionShare")
    PermissionsDTO toPermissionsDTO(Board.Permissions permissions);
}
