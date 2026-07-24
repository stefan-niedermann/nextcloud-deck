package it.niedermann.nextcloud.deck.domain.usecases.boards;

import java.util.concurrent.CompletableFuture;

import it.niedermann.nextcloud.deck.domain.model.Board;
import jakarta.inject.Inject;

public class DeleteBoardUseCase {

    @Inject
    public DeleteBoardUseCase() {
    }

    public CompletableFuture<Void> execute(Board.ID boardId) {
        // TODO: BoardRepository does not have deleteBoard method yet.
        return CompletableFuture.completedFuture(null);
    }
}
