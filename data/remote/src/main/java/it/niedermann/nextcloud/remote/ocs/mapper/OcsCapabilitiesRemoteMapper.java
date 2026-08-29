package it.niedermann.nextcloud.remote.ocs.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import it.niedermann.nextcloud.deck.domain.model.Capabilities;
import it.niedermann.nextcloud.deck.domain.model.Version;
import it.niedermann.nextcloud.remote.deck.mapper.CommonRemoteMapper;
import it.niedermann.nextcloud.remote.ocs.dto.CapabilitiesDTO;

@Mapper(uses = {CommonRemoteMapper.class})
public interface OcsCapabilitiesRemoteMapper {

    OcsCapabilitiesRemoteMapper INSTANCE = Mappers.getMapper(OcsCapabilitiesRemoteMapper.class);

    @Mapping(target = "serverVersion", source = "version")
    @Mapping(target = "themingColor", source = "capabilities.theming.color")
    @Mapping(target = "commentsEnabled", ignore = true)
    @Mapping(target = "activityEnabled", ignore = true)
    Capabilities toTO(CapabilitiesDTO dto);

    default Version toVersion(it.niedermann.nextcloud.remote.ocs.dto.OcsVersionDTO dto) {
        if (dto == null || dto.getString() == null) return null;
        return Version.parse(dto.getString());
    }
}
