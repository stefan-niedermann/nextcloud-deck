package it.niedermann.nextcloud.deck.domain.usecases.accounts;

import java.util.concurrent.Flow;

import it.niedermann.nextcloud.deck.domain.repository.AccountRepository;
import jakarta.inject.Inject;

public class HasAccountsUseCase {

    private final AccountRepository accountRepository;

    @Inject
    public HasAccountsUseCase(
            AccountRepository accountRepository
    ) {
        this.accountRepository = accountRepository;
    }

    public Flow.Publisher<Boolean> execute() {
        return accountRepository.hasAccounts();
    }
}