package it.niedermann.nextcloud.deck.domain.usecases.state;

import it.niedermann.nextcloud.deck.domain.repository.StateRepository;
import jakarta.inject.Inject;

public class SetCurrentBoardUseCase {

    private final StateRepository stateRepository;

    @Inject
    public SetCurrentBoardUseCase(
            StateRepository stateRepository
    ) {
        this.stateRepository = stateRepository;
    }

    public void execute(long accountId, long boardId) {
        this.stateRepository.setCurrentBoardId(accountId, boardId);
    }

}
