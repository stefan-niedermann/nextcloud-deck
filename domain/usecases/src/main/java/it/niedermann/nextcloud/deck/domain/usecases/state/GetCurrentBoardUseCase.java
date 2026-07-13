package it.niedermann.nextcloud.deck.domain.usecases.state;

import java.util.concurrent.CompletableFuture;

import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.repository.StateRepository;
import jakarta.inject.Inject;

public class GetCurrentBoardUseCase {

    private final StateRepository stateRepository;

    @Inject
    public GetCurrentBoardUseCase(
            StateRepository stateRepository
    ) {
        this.stateRepository = stateRepository;
    }

    public CompletableFuture<Board.ID> execute(Account.ID id) {
        return stateRepository.getCurrentBoardId(id);
    }

}
