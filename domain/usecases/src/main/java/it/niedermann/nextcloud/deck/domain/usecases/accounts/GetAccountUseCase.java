package it.niedermann.nextcloud.deck.domain.usecases.accounts;

import java.util.concurrent.Flow;

import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.repository.AccountRepository;
import jakarta.inject.Inject;

public class GetAccountUseCase {

    private final AccountRepository accountRepository;

    @Inject
    public GetAccountUseCase(
            AccountRepository accountRepository
    ) {
        this.accountRepository = accountRepository;
    }

    public Flow.Publisher<Account> execute(long id) {
        return accountRepository.getAccount(id);
    }
}