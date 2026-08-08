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
    @Mapping(target = "ownerId", source = "ownerId")
    @Mapping(target = "accountId", source = "accountId")
    @Mapping(target = "remoteId", source = "remoteId")
    @Mapping(target = "permissions.permissionRead", source = "permissionRead")
    @Mapping(target = "permissions.permissionEdit", source = "permissionEdit")
    @Mapping(target = "permissions.permissionManage", source = "permissionManage")
    @Mapping(target = "permissions.permissionShare", source = "permissionShare")
    @Mapping(target = "status", ignore = true)
    Board toTO(BoardEntity boardEntity);
}
