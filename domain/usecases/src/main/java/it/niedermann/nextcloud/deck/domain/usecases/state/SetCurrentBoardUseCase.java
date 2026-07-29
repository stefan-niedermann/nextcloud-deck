package it.niedermann.nextcloud.deck.domain.usecases.state;

import java.util.concurrent.CompletableFuture;

import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.state.StateRepository;
import jakarta.inject.Inject;

public class SetCurrentBoardUseCase {

    private final StateRepository stateRepository;

    @Inject
    public SetCurrentBoardUseCase(
            StateRepository stateRepository
    ) {
        this.stateRepository = stateRepository;
    }

    public CompletableFuture<Board.ID> execute(Account.ID accountId, Board.ID boardId) {
        return this.stateRepository.setCurrentBoardId(accountId, boardId);
    }

}
