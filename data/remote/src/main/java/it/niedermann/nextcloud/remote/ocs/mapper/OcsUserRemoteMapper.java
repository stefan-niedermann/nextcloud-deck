package it.niedermann.nextcloud.remote.ocs.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import it.niedermann.nextcloud.deck.domain.model.User;
import it.niedermann.nextcloud.remote.deck.mapper.CommonRemoteMapper;
import it.niedermann.nextcloud.remote.deck.mapper.GenericRemoteMapper;
import it.niedermann.nextcloud.remote.ocs.dto.OcsUserDTO;

@Mapper(uses = {CommonRemoteMapper.class})
public interface OcsUserRemoteMapper extends GenericRemoteMapper<OcsUserDTO, User> {

    OcsUserRemoteMapper INSTANCE = Mappers.getMapper(OcsUserRemoteMapper.class);

    @Override
    @Mapping(target = "id", source = "id")
    @Mapping(target = "displayname", source = "displayName")
    @Mapping(target = "lastLogin", ignore = true)
    @Mapping(target = "quota", ignore = true)
    @Mapping(target = "groups", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "phone", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "website", ignore = true)
    @Mapping(target = "twitter", ignore = true)
    @Mapping(target = "language", ignore = true)
    @Mapping(target = "locale", ignore = true)
    OcsUserDTO toDTO(User user);

    @Override
    @Mapping(target = "id", source = "id")
    @Mapping(target = "displayName", source = "displayname")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    @Mapping(target = "accountId", ignore = true)
    @Mapping(target = "remoteId", ignore = true)
    User toTO(OcsUserDTO dto);
}
