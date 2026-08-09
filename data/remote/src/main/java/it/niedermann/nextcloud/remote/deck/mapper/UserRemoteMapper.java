package it.niedermann.nextcloud.remote.deck.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import it.niedermann.nextcloud.deck.domain.model.User;
import it.niedermann.nextcloud.remote.deck.dto.UserDTO;

@Mapper(uses = {CommonRemoteMapper.class})
public interface UserRemoteMapper extends GenericRemoteMapper<UserDTO, User> {

    UserRemoteMapper INSTANCE = Mappers.getMapper(UserRemoteMapper.class);

    @Override
    @Mapping(target = "uid", source = "id")
    @Mapping(target = "displayname", source = "displayName")
    @Mapping(target = "primaryKey", source = "id")
    @Mapping(target = "type", ignore = true)
    UserDTO toDTO(User user);

    @Override
    @Mapping(target = "id", source = "uid")
    @Mapping(target = "displayName", source = "displayname")
    @Mapping(target = "status", expression = "java(it.niedermann.nextcloud.deck.domain.model.DBStatus.UP_TO_DATE)")
    @Mapping(target = "lastModified", ignore = true)
    @Mapping(target = "accountId", ignore = true)
    @Mapping(target = "remoteId", ignore = true)
    User toTO(UserDTO userDTO);
}
