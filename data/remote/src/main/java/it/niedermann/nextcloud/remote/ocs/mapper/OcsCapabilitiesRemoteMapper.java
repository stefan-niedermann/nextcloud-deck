package it.niedermann.nextcloud.remote.ocs.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import it.niedermann.nextcloud.deck.domain.model.Capabilities;
import it.niedermann.nextcloud.deck.domain.model.NextcloudVersion;
import it.niedermann.nextcloud.remote.deck.mapper.CommonRemoteMapper;
import it.niedermann.nextcloud.remote.ocs.dto.CapabilitiesDTO;

@Mapper(uses = {CommonRemoteMapper.class})
public interface OcsCapabilitiesRemoteMapper {

    OcsCapabilitiesRemoteMapper INSTANCE = Mappers.getMapper(OcsCapabilitiesRemoteMapper.class);

    @Mapping(target = "nextcloudVersion", source = "version")
    @Mapping(target = "deckVersion", ignore = true)
    @Mapping(target = "themingColor", source = "capabilities.theming.color")
    @Mapping(target = "themingTextColor", source = "capabilities.theming.colorText")
    @Mapping(target = "tablesEnabled", source = "capabilities.tables.enabled")
    Capabilities toTO(CapabilitiesDTO dto);

    default NextcloudVersion toNextcloudVersion(it.niedermann.nextcloud.remote.ocs.dto.OcsVersionDTO dto) {
        if (dto == null || dto.getString() == null) return null;
        return NextcloudVersion.parse(dto.getString());
    }
}
