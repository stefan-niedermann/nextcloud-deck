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
    @Mapping(target = "accountId", source = "accountId")
    @Mapping(target = "remoteId", source = "id")
    @Mapping(target = "etag", ignore = true)
    @Mapping(target = "localId", ignore = true)
    UserEntity toEntity(User user);

    @Override
    @Mapping(target = "accountId", source = "accountId")
    @Mapping(target = "id", expression = "java(new it.niedermann.nextcloud.deck.domain.model.User.ID(entity.getRemoteId() != null ? entity.getRemoteId() : \"\"))")
    @Mapping(target = "displayName", expression = "java(entity.getDisplayName() != null ? entity.getDisplayName() : \"\")")
    @Mapping(target = "status", expression = "java(it.niedermann.nextcloud.deck.domain.model.DBStatus.findById(entity.getStatus()))")
    @Mapping(target = "lastModified", source = "lastModified")
    @Mapping(target = "remoteId", ignore = true)
    User toTO(UserEntity entity);
}
