package it.niedermann.nextcloud.deck.domain.usecases.boards;

import java.util.concurrent.Flow;

import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.repository.BoardRepository;
import jakarta.inject.Inject;

public class GetBoardUseCase {

    private final BoardRepository boardRepository;

    @Inject
    public GetBoardUseCase(
            BoardRepository boardRepository
    ) {
        this.boardRepository = boardRepository;
    }

    public Flow.Publisher<Board> execute(long boardId) {
        return boardRepository.getBoard(boardId);
    }
}
