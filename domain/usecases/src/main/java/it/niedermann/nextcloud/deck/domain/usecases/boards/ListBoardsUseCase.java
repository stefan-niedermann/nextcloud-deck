package it.niedermann.nextcloud.deck.domain.usecases.boards;

import java.util.List;
import java.util.concurrent.Flow;

import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.repository.BoardRepository;
import jakarta.inject.Inject;

public class ListBoardsUseCase {

    private final BoardRepository boardRepository;

    @Inject
    public ListBoardsUseCase(
            BoardRepository boardRepository
    ) {
        this.boardRepository = boardRepository;
    }

    public Flow.Publisher<List<Board>> execute(long accountId) {
        return boardRepository.getNotDeletedBoards(accountId);
    }
}
