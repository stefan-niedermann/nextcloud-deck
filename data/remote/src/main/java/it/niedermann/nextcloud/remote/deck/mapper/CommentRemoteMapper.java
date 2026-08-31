package it.niedermann.nextcloud.remote.deck.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import it.niedermann.nextcloud.deck.domain.model.Comment;
import it.niedermann.nextcloud.remote.deck.dto.CommentDTO;

@Mapper(uses = {CommonRemoteMapper.class})
public interface CommentRemoteMapper extends GenericRemoteMapper<CommentDTO, Comment> {

    CommentRemoteMapper INSTANCE = Mappers.getMapper(CommentRemoteMapper.class);

    @Override
    @Mapping(target = "id", source = "id")
    @Mapping(target = "actorId", source = "author")
    @Mapping(target = "replyTo", ignore = true)
    @Mapping(target = "mentions", ignore = true)
    @Mapping(target = "objectId", source = "cardId")
    @Mapping(target = "creationDateTime", source = "created")
    @Mapping(target = "actorDisplayName", ignore = true)
    @Mapping(target = "actorType", ignore = true)
    @Mapping(target = "message", source = "message")
    CommentDTO toDTO(Comment comment);

    @Override
    @Mapping(target = "id", source = "id")
    @Mapping(target = "status", expression = "java(it.niedermann.nextcloud.deck.domain.model.DBStatus.UP_TO_DATE)")
    @Mapping(target = "lastModified", ignore = true)
    @Mapping(target = "cardId", source = "objectId")
    @Mapping(target = "created", source = "creationDateTime")
    @Mapping(target = "author", source = "actorId")
    @Mapping(target = "remoteId", source = "id")
    @Mapping(target = "parentId", source = "replyTo.id")
    @Mapping(target = "message", source = "message")
    Comment toTO(CommentDTO commentDTO);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "status", expression = "java(it.niedermann.nextcloud.deck.domain.model.DBStatus.UP_TO_DATE)")
    @Mapping(target = "lastModified", ignore = true)
    @Mapping(target = "cardId", source = "objectId")
    @Mapping(target = "created", source = "creationDateTime")
    @Mapping(target = "author", source = "actorId")
    @Mapping(target = "remoteId", source = "id")
    @Mapping(target = "parentId", source = "replyTo.id")
    @Mapping(target = "message", source = "message")
    Comment toTOFromOcs(it.niedermann.nextcloud.remote.ocs.dto.CommentDTO commentDTO);
}
