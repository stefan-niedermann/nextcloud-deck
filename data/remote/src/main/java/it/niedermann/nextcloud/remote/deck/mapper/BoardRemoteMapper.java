package it.niedermann.nextcloud.remote.deck.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.remote.deck.dto.BoardDTO;
import it.niedermann.nextcloud.remote.deck.dto.PermissionsDTO;

@Mapper(uses = {CommonRemoteMapper.class})
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
    BoardDTO toDTO(Board board);

    @Override
    @Mapping(target = "id", source = "id")
    @Mapping(target = "remoteId", source = "id")
    @Mapping(target = "lastModified", source = "lastModified")
    @Mapping(target = "permissions", source = "permissions")
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "accountId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "color", source = "color")
    Board toTO(BoardDTO boardDTO);

    @Mapping(target = "permissionRead", source = "permissionRead")
    @Mapping(target = "permissionEdit", source = "permissionEdit")
    @Mapping(target = "permissionManage", source = "permissionManage")
    @Mapping(target = "permissionShare", source = "permissionShare")
    Board.Permissions toPermissions(PermissionsDTO dto);

    @Mapping(target = "permissionRead", source = "permissionRead")
    @Mapping(target = "permissionEdit", source = "permissionEdit")
    @Mapping(target = "permissionManage", source = "permissionManage")
    @Mapping(target = "permissionShare", source = "permissionShare")
    PermissionsDTO toPermissionsDTO(Board.Permissions permissions);
}
