package it.niedermann.nextcloud.remote.deck.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.remote.deck.dto.CardDTO;

@Mapper(uses = {CommonRemoteMapper.class})
public interface CardRemoteMapper extends GenericRemoteMapper<CardDTO, Card> {

    CardRemoteMapper INSTANCE = Mappers.getMapper(CardRemoteMapper.class);

    @Override
    @Mapping(target = "id", source = "id")
    @Mapping(target = "stackId", source = "columnId")
    @Mapping(target = "labels", ignore = true)
    @Mapping(target = "assignedUsers", ignore = true)
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "etag", ignore = true)
    CardDTO toDTO(Card card);

    @Override
    @Mapping(target = "id", source = "id")
    @Mapping(target = "columnId", source = "stackId")
    @Mapping(target = "remoteId", source = "id")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "labels", ignore = true)
    @Mapping(target = "assignees", ignore = true)
    @Mapping(target = "dependents", ignore = true)
    @Mapping(target = "color", ignore = true)
    @Mapping(target = "archived", source = "archived")
    @Mapping(target = "startDate", ignore = true)
    Card toTO(CardDTO cardDTO);
}
