package it.niedermann.nextcloud.deck.domain.usecases.state;

import org.reactivestreams.FlowAdapters;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.repository.AccountRepository;
import it.niedermann.nextcloud.deck.domain.repository.StateRepository;
import jakarta.inject.Inject;

public class GetCurrentAccountUseCase {

    private static final Logger logger = Logger.getLogger(GetCurrentAccountUseCase.class.getName());

    private final StateRepository stateRepository;
    private final AccountRepository accountRepository;

    @Inject
    public GetCurrentAccountUseCase(
            StateRepository stateRepository,
            AccountRepository accountRepository
    ) {
        this.stateRepository = stateRepository;
        this.accountRepository = accountRepository;
    }

    public CompletableFuture<Account.ID> execute() {
        final var currentAccountId = Single.fromCompletionStage(stateRepository.getCurrentAccountId());
        final Flowable<Boolean> accountExists = currentAccountId.flatMapPublisher(accountId -> FlowAdapters.toPublisher(accountRepository.accountExists(accountId)));

        return Single.zip(currentAccountId, accountExists.firstOrError(), AccountIdAndExists::new)
                .flatMap(pair -> {
                    if (pair.accountExists()) {
                        return Single.just(pair.accountId());
                    } else {
                        return Single.fromCompletionStage(accountRepository.getAnyAccount())
                                .doOnSuccess(this.stateRepository::setCurrentAccountId)
                                .doOnError(exception -> this.stateRepository.removeCurrentAccountId());
                    }
                })
                .toCompletionStage()
                .toCompletableFuture();
    }

    record AccountIdAndExists(Account.ID accountId, boolean accountExists) {
    }
}
