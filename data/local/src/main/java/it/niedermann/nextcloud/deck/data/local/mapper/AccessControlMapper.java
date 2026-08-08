package it.niedermann.nextcloud.deck.data.local.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import it.niedermann.nextcloud.deck.data.local.entity.AccessControlEntity;
import it.niedermann.nextcloud.deck.domain.model.AccessControl;

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
    @Mapping(target = "permissions.permissionRead", constant = "true")
    @Mapping(target = "permissions.permissionEdit", source = "permissionEdit")
    @Mapping(target = "permissions.permissionShare", source = "permissionShare")
    @Mapping(target = "permissions.permissionManage", source = "permissionManage")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    AccessControl toTO(AccessControlEntity entity);
}
