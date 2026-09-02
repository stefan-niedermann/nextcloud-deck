package it.niedermann.nextcloud.deck.app.shared.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import it.niedermann.nextcloud.auth.webloginflowv2.AuthenticatedAccount;
import it.niedermann.nextcloud.deck.domain.model.ImportAccount;

@Mapper
public interface AuthMapper {

    AuthMapper INSTANCE = Mappers.getMapper(AuthMapper.class);

    ImportAccount toImportAccount(AuthenticatedAccount account);
}
