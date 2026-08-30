package it.niedermann.nextcloud.deck.data.local.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import it.niedermann.nextcloud.deck.data.local.entity.CardEntity;
import it.niedermann.nextcloud.deck.domain.model.Card;

@Mapper(uses = {CommonLocalMapper.class})
public interface CardMapper extends GenericMapper<CardEntity, Card> {

    CardMapper INSTANCE = Mappers.getMapper(CardMapper.class);

    @Override
    @Mapping(target = "localId", source = "id")
    @Mapping(target = "columnId", source = "columnId")
    @Mapping(target = "remoteId", source = "remoteId")
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "etag", source = "etag")
    @Mapping(target = "accountId", ignore = true)
    @Mapping(target = "startDate", source = "startDate")
    @Mapping(target = "color", source = "color")
    @Mapping(target = "status", expression = "java(card.status().getId())")
    CardEntity toEntity(Card card);

    @Override
    @Mapping(target = "id", source = "localId")
    @Mapping(target = "columnId", source = "columnId")
    @Mapping(target = "remoteId", source = "remoteId")
    @Mapping(target = "status", expression = "java(it.niedermann.nextcloud.deck.domain.model.DBStatus.findById(cardEntity.getStatus()))")
    @Mapping(target = "lastModified", source = "lastModified")
    @Mapping(target = "startDate", source = "startDate")
    @Mapping(target = "color", source = "color")
    @Mapping(target = "etag", source = "etag")
    @Mapping(target = "labels", expression = "java(java.util.Collections.emptySet())")
    @Mapping(target = "assignees", expression = "java(java.util.Collections.emptySet())")
    @Mapping(target = "dependents", expression = "java(java.util.Collections.emptyList())")
    Card toTO(CardEntity cardEntity);
}
