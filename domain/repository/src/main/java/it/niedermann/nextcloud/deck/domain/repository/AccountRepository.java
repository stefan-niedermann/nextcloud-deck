package it.niedermann.nextcloud.deck.domain.repository;

import java.net.URL;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;

import it.niedermann.nextcloud.deck.domain.model.Account;

public interface AccountRepository {

    Flow.Publisher<Boolean> accountExists(long id);

    Flow.Publisher<Account> getAccount(long id);

    CompletableFuture<Long> findAccountId(String accountName);

    CompletableFuture<Long> addAccount(URL url, String username, String token);

    CompletableFuture<Void> removeAccount(Long id);

    Flow.Publisher<Collection<Account>> getAccounts();

    Flow.Publisher<Boolean> hasAccounts();
}