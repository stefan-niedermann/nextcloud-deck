package it.niedermann.nextcloud.deck.domain.usecases.boards;

import java.util.concurrent.CompletableFuture;

import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.User;
import it.niedermann.nextcloud.deck.domain.repository.ShareRepository;
import jakarta.inject.Inject;

public class UpdateBoardShareUseCase {

    private final ShareRepository shareRepository;

    @Inject
    public UpdateBoardShareUseCase(ShareRepository shareRepository) {
        this.shareRepository = shareRepository;
    }

    public CompletableFuture<Void> execute(Board.ID boardId, User.ID userId, Board.Permissions permissions) {
        return shareRepository.updateShare(boardId, userId, permissions);
    }
}
