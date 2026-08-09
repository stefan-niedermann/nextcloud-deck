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
    @Mapping(target = "columnId", source = "columnId")
    @Mapping(target = "remoteId", source = "remoteId")
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "etag", ignore = true)
    @Mapping(target = "accountId", ignore = true)
    CardEntity toEntity(Card card);

    @Override
    @Mapping(target = "columnId", source = "columnId")
    @Mapping(target = "remoteId", source = "remoteId")
    @Mapping(target = "status", expression = "java(it.niedermann.nextcloud.deck.domain.model.DBStatus.findById(cardEntity.getStatus()))")
    @Mapping(target = "lastModified", source = "lastModified")
    @Mapping(target = "color", ignore = true)
    @Mapping(target = "labels", ignore = true)
    @Mapping(target = "assignees", ignore = true)
    @Mapping(target = "dependents", ignore = true)
    Card toTO(CardEntity cardEntity);
}
