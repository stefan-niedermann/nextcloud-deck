package it.niedermann.nextcloud.deck.data.local.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import it.niedermann.nextcloud.deck.data.local.entity.CapabilitiesEntity;
import it.niedermann.nextcloud.deck.domain.model.Capabilities;

@Mapper
public interface CapabilitiesMapper {

    CapabilitiesMapper INSTANCE = Mappers.getMapper(CapabilitiesMapper.class);

    CapabilitiesEntity toEntity(Capabilities capabilities);

    Capabilities toTO(CapabilitiesEntity entity);
}
