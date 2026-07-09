package it.niedermann.nextcloud.deck.domain.usecases.state;

import static org.reactivestreams.FlowAdapters.toFlowPublisher;

import org.reactivestreams.FlowAdapters;

import java.util.Objects;
import java.util.concurrent.Flow;
import java.util.logging.Logger;

import io.reactivex.rxjava3.core.Flowable;
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

    public Flow.Publisher<Account> execute() {
        return execute("other");
    }

    public Flow.Publisher<Account> execute(String subscriber) {
        return execute(subscriber, false);
    }

    public Flow.Publisher<Account> execute(String subscriber, boolean ignore) {

        final var currentAccountId = Flowable.fromPublisher(FlowAdapters.toPublisher(stateRepository.getCurrentAccountId()))
                .distinctUntilChanged()
                .filter(Objects::nonNull)
                .filter(accountId -> accountId.value() >= 0);

        final var accountExists = currentAccountId
                .map(accountRepository::accountExists)
                .distinctUntilChanged()
                .map(FlowAdapters::toPublisher)
                .switchMap(Flowable::fromPublisher);

        final var account = Flowable.combineLatest(currentAccountId, accountExists, AccountIdAndExists::new)
                .distinctUntilChanged(Objects::equals)
                .map(args -> {

                    if (!args.accountExists()) {
                        throw new IllegalStateException("Subscriber: " + subscriber + "Account with cardId " + args.accountId() + " does not exist.");
                    }

                    return args.accountId();
                })

                .map(accountRepository::getAccount)

                .map(FlowAdapters::toPublisher)
                .switchMap(Flowable::fromPublisher);

        return toFlowPublisher(account);
    }

    record AccountIdAndExists(Account.ID accountId, boolean accountExists) {
    }
}
