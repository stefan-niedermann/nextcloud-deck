package it.niedermann.nextcloud.deck.data.local.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import it.niedermann.nextcloud.deck.data.local.entity.BoardEntity;
import it.niedermann.nextcloud.deck.domain.model.Board;

@Mapper(uses = {CommonLocalMapper.class})
public interface BoardMapper extends GenericMapper<BoardEntity, Board> {

    BoardMapper INSTANCE = Mappers.getMapper(BoardMapper.class);

    @Override
    @Mapping(target = "localId", source = "id")
    @Mapping(target = "ownerId", source = "ownerId")
    @Mapping(target = "accountId", source = "accountId")
    @Mapping(target = "remoteId", source = "remoteId")
    @Mapping(target = "permissionRead", source = "permissions.permissionRead")
    @Mapping(target = "permissionEdit", source = "permissions.permissionEdit")
    @Mapping(target = "permissionManage", source = "permissions.permissionManage")
    @Mapping(target = "permissionShare", source = "permissions.permissionShare")
    @Mapping(target = "etag", ignore = true)
    BoardEntity toEntity(Board board);

    @Override
    @Mapping(target = "id", source = "localId")
    @Mapping(target = "ownerId", source = "ownerId")
    @Mapping(target = "accountId", source = "accountId")
    @Mapping(target = "remoteId", source = "remoteId")
    @Mapping(target = "permissions", source = ".")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "lastModified", source = "lastModified")
    Board toTO(BoardEntity entity);

    @Mapping(target = "permissionRead", source = "permissionRead")
    @Mapping(target = "permissionEdit", source = "permissionEdit")
    @Mapping(target = "permissionManage", source = "permissionManage")
    @Mapping(target = "permissionShare", source = "permissionShare")
    Board.Permissions mapPermissions(BoardEntity entity);
}
