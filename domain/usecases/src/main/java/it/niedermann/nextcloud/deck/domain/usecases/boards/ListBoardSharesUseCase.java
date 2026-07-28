package it.niedermann.nextcloud.deck.domain.usecases.boards;

import java.util.List;
import java.util.concurrent.Flow;

import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.BoardShare;
import it.niedermann.nextcloud.deck.domain.repository.ShareRepository;
import jakarta.inject.Inject;

public class ListBoardSharesUseCase {

    private final ShareRepository shareRepository;

    @Inject
    public ListBoardSharesUseCase(ShareRepository shareRepository) {
        this.shareRepository = shareRepository;
    }

    public Flow.Publisher<List<BoardShare>> execute(Board.ID boardId) {
        return shareRepository.getShares(boardId);
    }
}
