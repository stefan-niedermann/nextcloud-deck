package it.niedermann.nextcloud.deck.data.repository;

import org.reactivestreams.FlowAdapters;

import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;

import it.niedermann.nextcloud.deck.data.local.dao.AccountDao;
import it.niedermann.nextcloud.deck.data.local.entity.AccountEntity;
import it.niedermann.nextcloud.deck.data.local.mapper.AccountMapper;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.repository.AccountRepository;
import jakarta.inject.Inject;

public class AccountRepositoryImpl implements AccountRepository {

    private final AccountDao accountDao;
    private final AccountMapper accountMapper;

    private final Map<Long, Flow.Publisher<Account>> accounts = new HashMap<>();

    @Inject
    public AccountRepositoryImpl(
            AccountDao accountDao,
            AccountMapper accountMapper
    ) {
        this.accountDao = accountDao;
        this.accountMapper = accountMapper;
    }

    @Override
    public Flow.Publisher<Boolean> accountExists(Account.ID id) {
        final var result = accountDao.accountExists(id.value());

        return FlowAdapters.toFlowPublisher(result);
    }

    @Override
    public Flow.Publisher<Account> getAccount(Account.ID id) {
        final var result = accountDao
                .getAccount(id.value())
                .map(accountMapper::toTO);

        return FlowAdapters.toFlowPublisher(result);
    }

    @Override
    public CompletableFuture<Account.ID> findAccountId(String accountName) {
        return accountDao.findAccountId(accountName)
                .toCompletionStage()
                .toCompletableFuture()
                .thenApplyAsync(Account.ID::new);
    }

    @Override
    public CompletableFuture<Account.ID> addAccount(URL url, String username, String token) {
        final var accountEntity = new AccountEntity(0L, url, username, token, "");

        return accountDao.insert(accountEntity)
                .toCompletionStage()
                .toCompletableFuture()
                .thenApplyAsync(Account.ID::new);
    }

    @Override
    public CompletableFuture<Void> removeAccount(Account.ID id) {
        return accountDao.deleteAccount(id.value())
                .toCompletionStage()
                .toCompletableFuture();
    }

    @Override
    public Flow.Publisher<Collection<Account>> getAccounts() {
        final var result = accountDao.getAccounts()
                .map(accountMapper::toTOList);

        return FlowAdapters.toFlowPublisher(result);
    }

    @Override
    public Flow.Publisher<Boolean> hasAccounts() {
        final var result = accountDao.hasAccount()
                .distinctUntilChanged();

        return FlowAdapters.toFlowPublisher(result);
    }

}