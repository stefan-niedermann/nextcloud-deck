package it.niedermann.nextcloud.remote.deck.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import it.niedermann.nextcloud.deck.domain.model.AccessControl;
import it.niedermann.nextcloud.remote.deck.dto.AccessControlDTO;

@Mapper(uses = {UserRemoteMapper.class, CommonRemoteMapper.class})
public interface AccessControlRemoteMapper extends GenericRemoteMapper<AccessControlDTO, AccessControl> {

    AccessControlRemoteMapper INSTANCE = Mappers.getMapper(AccessControlRemoteMapper.class);

    @Override
    @Mapping(target = "id", source = "remoteId")
    @Mapping(target = "boardId", source = "boardId")
    @Mapping(target = "participant", ignore = true)
    @Mapping(target = "permissionEdit", source = "permissions.permissionEdit")
    @Mapping(target = "permissionShare", source = "permissions.permissionShare")
    @Mapping(target = "permissionManage", source = "permissions.permissionManage")
    AccessControlDTO toDTO(AccessControl acl);

    @Override
    @Mapping(target = "remoteId", source = "id")
    @Mapping(target = "boardId", source = "boardId")
    @Mapping(target = "permissions.permissionRead", constant = "true")
    @Mapping(target = "permissions.permissionEdit", source = "permissionEdit")
    @Mapping(target = "permissions.permissionShare", source = "permissionShare")
    @Mapping(target = "permissions.permissionManage", source = "permissionManage")
    @Mapping(target = "localId", ignore = true)
    @Mapping(target = "accountId", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "status", ignore = true)
    AccessControl toTO(AccessControlDTO aclDTO);
}
