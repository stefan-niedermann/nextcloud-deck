package it.niedermann.nextcloud.deck.domain.usecases.accounts;

import java.util.concurrent.CompletableFuture;

import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.repository.AccountRepository;
import jakarta.inject.Inject;

public class RemoveAccountUseCase {

    private final AccountRepository accountRepository;

    @Inject
    public RemoveAccountUseCase(
            AccountRepository accountRepository
    ) {
        this.accountRepository = accountRepository;
    }

    public CompletableFuture<Void> execute(Account.ID accountId) {
        return accountRepository.removeAccount(accountId);
    }

    public CompletableFuture<Void> execute(String accountName) {
        return this.accountRepository.findAccountId(accountName)
                .thenComposeAsync(accountRepository::removeAccount);
    }
}
