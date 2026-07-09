package it.niedermann.nextcloud.deck.data.local.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import it.niedermann.nextcloud.deck.data.local.entity.AccountEntity;
import it.niedermann.nextcloud.deck.domain.model.Account;

@Mapper
public interface AccountMapper extends GenericMapper<AccountEntity, Account> {

    AccountMapper INSTANCE = Mappers.getMapper(AccountMapper.class);

    @Override
    AccountEntity toEntity(Account account);

    @Override
    Account toTO(AccountEntity accountEntity);

    default Account.ID toAccountId(long value) {
        return new Account.ID(value);
    }

    default Long toValue(Account.ID accountId) {
        return accountId.value();
    }
}
