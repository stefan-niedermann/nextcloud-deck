package it.niedermann.nextcloud.deck.domain.usecases.boards;

import java.util.concurrent.CompletableFuture;

import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.repository.BoardRepository;
import jakarta.inject.Inject;

public class DeleteBoardUseCase {

    private final BoardRepository boardRepository;

    @Inject
    public DeleteBoardUseCase(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public CompletableFuture<Void> execute(Board.ID boardId) {
        return boardRepository.deleteBoard(boardId);
    }
}
