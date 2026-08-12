package it.niedermann.nextcloud.deck.data.local.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import it.niedermann.nextcloud.deck.data.local.entity.CommentEntity;
import it.niedermann.nextcloud.deck.domain.model.Comment;

@Mapper(uses = {CommonLocalMapper.class})
public interface CommentMapper extends GenericMapper<CommentEntity, Comment> {

    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    @Override
    @Mapping(target = "localId", source = "id")
    @Mapping(target = "remoteId", source = "remoteId")
    @Mapping(target = "createdAt", source = "created")
    @Mapping(target = "etag", ignore = true)
    CommentEntity toEntity(Comment comment);

    @Override
    @Mapping(target = "id", source = "localId")
    @Mapping(target = "created", source = "createdAt")
    @Mapping(target = "status", expression = "java(it.niedermann.nextcloud.deck.domain.model.DBStatus.findById(entity.getStatus()))")
    @Mapping(target = "lastModified", source = "lastModified")
    @Mapping(target = "author", expression = "java(new it.niedermann.nextcloud.deck.domain.model.User.ID(entity.getActorId() != null ? entity.getActorId() : \"\"))")
    Comment toTO(CommentEntity entity);
}
