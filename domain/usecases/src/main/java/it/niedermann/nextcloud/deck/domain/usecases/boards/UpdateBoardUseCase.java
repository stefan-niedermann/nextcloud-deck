package it.niedermann.nextcloud.deck.domain.usecases.boards;

import java.util.concurrent.CompletableFuture;

import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.repository.BoardRepository;
import jakarta.inject.Inject;

public class UpdateBoardUseCase {

    private final BoardRepository boardRepository;

    @Inject
    public UpdateBoardUseCase(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public CompletableFuture<Void> execute(Board board) {
        return boardRepository.updateBoard(board);
    }
}
