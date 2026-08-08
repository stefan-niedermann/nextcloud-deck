package it.niedermann.nextcloud.remote.ocs.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import it.niedermann.nextcloud.deck.domain.model.OcsAutocompleteResult;
import it.niedermann.nextcloud.deck.domain.model.OcsSearchProvider;
import it.niedermann.nextcloud.deck.domain.model.OcsSearchResult;
import it.niedermann.nextcloud.remote.deck.mapper.CommonRemoteMapper;
import it.niedermann.nextcloud.remote.ocs.dto.OcsAutocompleteResultDTO;
import it.niedermann.nextcloud.remote.ocs.dto.OcsSearchProviderDTO;
import it.niedermann.nextcloud.remote.ocs.dto.OcsSearchResultDTO;
import it.niedermann.nextcloud.remote.ocs.dto.OcsSearchResultEntryDTO;

@Mapper(uses = {CommonRemoteMapper.class})
public interface OcsSearchRemoteMapper {

    OcsSearchRemoteMapper INSTANCE = Mappers.getMapper(OcsSearchRemoteMapper.class);

    OcsAutocompleteResult toAutocompleteTO(OcsAutocompleteResultDTO dto);

    @Mapping(target = "remoteId", source = "id")
    OcsSearchProvider toProviderTO(OcsSearchProviderDTO dto);

    OcsSearchResult toResultTO(OcsSearchResultDTO dto);

    OcsSearchResult.Entry toEntryTO(OcsSearchResultEntryDTO dto);
}
