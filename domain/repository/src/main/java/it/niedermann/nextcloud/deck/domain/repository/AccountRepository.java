package it.niedermann.nextcloud.deck.domain.repository;

import java.net.URL;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;

import it.niedermann.nextcloud.deck.domain.model.Account;

public interface AccountRepository {

    Flow.Publisher<Boolean> accountExists(Account.ID id);

    Flow.Publisher<Account> getAccount(Account.ID id);

    CompletableFuture<Account.ID> findAccountId(String accountName);

    CompletableFuture<Account.ID> addAccount(URL url, String username, String token);

    CompletableFuture<Void> removeAccount(Account.ID id);

    Flow.Publisher<Collection<Account>> getAccounts();

    Flow.Publisher<Boolean> hasAccounts();
}