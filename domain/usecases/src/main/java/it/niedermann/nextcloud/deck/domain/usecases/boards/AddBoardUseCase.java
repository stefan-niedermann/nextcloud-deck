package it.niedermann.nextcloud.deck.domain.usecases.boards;

import java.util.concurrent.CompletableFuture;

import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.repository.BoardRepository;
import jakarta.inject.Inject;

public class AddBoardUseCase {

    private final BoardRepository boardRepository;

    @Inject
    public AddBoardUseCase(
            BoardRepository boardRepository
    ) {
        this.boardRepository = boardRepository;
    }

    public CompletableFuture<Void> addBoard(Board board) {
        return boardRepository.createBoard(board);
    }
}
