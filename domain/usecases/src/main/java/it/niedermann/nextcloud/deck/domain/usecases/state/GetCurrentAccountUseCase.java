package it.niedermann.nextcloud.deck.domain.usecases.state;

import org.reactivestreams.FlowAdapters;

import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

import io.reactivex.rxjava3.core.Flowable;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.repository.AccountRepository;
import it.niedermann.nextcloud.deck.domain.state.StateRepository;
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
        return Flowable.fromPublisher(FlowAdapters.toPublisher(accountRepository.hasAccounts()))
                .firstElement()
                .toCompletionStage()
                .toCompletableFuture()
                .thenComposeAsync(hasAccounts -> {
                    if (hasAccounts) {
                        return stateRepository.getCurrentAccountId();
                    } else {
                        throw new NoSuchElementException();
                    }
                });
    }
}
