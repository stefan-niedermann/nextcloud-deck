package it.niedermann.nextcloud.deck.data.local.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import it.niedermann.nextcloud.deck.data.local.entity.AccessControlEntity;
import it.niedermann.nextcloud.deck.domain.model.AccessControl;
import it.niedermann.nextcloud.deck.domain.model.Board;

@Mapper(uses = {CommonLocalMapper.class})
public interface AccessControlMapper extends GenericMapper<AccessControlEntity, AccessControl> {

    AccessControlMapper INSTANCE = Mappers.getMapper(AccessControlMapper.class);

    @Override
    @Mapping(target = "remoteId", source = "remoteId")
    @Mapping(target = "permissionEdit", source = "permissions.permissionEdit")
    @Mapping(target = "permissionShare", source = "permissions.permissionShare")
    @Mapping(target = "permissionManage", source = "permissions.permissionManage")
    @Mapping(target = "etag", ignore = true)
    AccessControlEntity toEntity(AccessControl acl);

    @Override
    @Mapping(target = "remoteId", source = "remoteId")
    @Mapping(target = "permissions", expression = "java(mapPermissions(entity))")
    @Mapping(target = "status", expression = "java(it.niedermann.nextcloud.deck.domain.model.DBStatus.findById(entity.getStatus()))")
    @Mapping(target = "lastModified", source = "lastModified")
    AccessControl toTO(AccessControlEntity entity);

    @Mapping(target = "permissionRead", constant = "true")
    @Mapping(target = "permissionEdit", source = "permissionEdit")
    @Mapping(target = "permissionShare", source = "permissionShare")
    @Mapping(target = "permissionManage", source = "permissionManage")
    Board.Permissions mapPermissions(AccessControlEntity entity);
}
