package it.niedermann.nextcloud.deck.domain.usecases.state;

import java.util.concurrent.CompletableFuture;

import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.repository.StateRepository;
import jakarta.inject.Inject;

public class SetCurrentAccountUseCase {

    private final StateRepository stateRepository;

    @Inject
    public SetCurrentAccountUseCase(
            StateRepository stateRepository
    ) {
        this.stateRepository = stateRepository;
    }

    public CompletableFuture<Account.ID> execute(Account.ID id) {
        return this.stateRepository.setCurrentAccountId(id);
    }

}
