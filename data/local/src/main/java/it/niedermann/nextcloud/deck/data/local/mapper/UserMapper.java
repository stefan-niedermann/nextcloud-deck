package it.niedermann.nextcloud.deck.data.local.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import it.niedermann.nextcloud.deck.data.local.entity.UserEntity;
import it.niedermann.nextcloud.deck.domain.model.User;

@Mapper(uses = {CommonLocalMapper.class})
public interface UserMapper extends GenericMapper<UserEntity, User> {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Override
    @Mapping(target = "localId", source = "id")
    @Mapping(target = "remoteId", source = "remoteId")
    @Mapping(target = "accountId", source = "accountId")
    @Mapping(target = "etag", ignore = true)
    UserEntity toEntity(User user);

    @Override
    @Mapping(target = "accountId", source = "accountId")
    @Mapping(target = "id", source = "localId")
    @Mapping(target = "remoteId", source = "remoteId")
    @Mapping(target = "displayName", expression = "java(commonLocalMapper.mapUserDisplayName(entity.getDisplayName()))")
    @Mapping(target = "status", expression = "java(it.niedermann.nextcloud.deck.domain.model.DBStatus.findById(entity.getStatus()))")
    @Mapping(target = "lastModified", source = "lastModified")
    User toTO(UserEntity entity);
}
