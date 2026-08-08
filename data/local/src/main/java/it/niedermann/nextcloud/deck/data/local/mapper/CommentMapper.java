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
    @Mapping(target = "remoteId", source = "id")
    @Mapping(target = "createdAt", source = "created")
    @Mapping(target = "etag", ignore = true)
    CommentEntity toEntity(Comment comment);

    @Override
    @Mapping(target = "id", source = "remoteId")
    @Mapping(target = "created", source = "createdAt")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "author", ignore = true)
    Comment toTO(CommentEntity entity);
}
