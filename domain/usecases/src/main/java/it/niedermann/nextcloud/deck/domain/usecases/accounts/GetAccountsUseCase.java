package it.niedermann.nextcloud.deck.domain.usecases.accounts;

import java.util.Collection;
import java.util.concurrent.Flow;

import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.repository.AccountRepository;
import jakarta.inject.Inject;

public class GetAccountsUseCase {

    private final AccountRepository accountRepository;

    @Inject
    public GetAccountsUseCase(
            AccountRepository accountRepository
    ) {
        this.accountRepository = accountRepository;
    }

    public Flow.Publisher<Collection<Account>> execute() {
        return accountRepository.getAccounts();
    }
}