package it.niedermann.nextcloud.deck.domain.repository;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;

import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.BoardShare;
import it.niedermann.nextcloud.deck.domain.model.User;

public interface ShareRepository {
    Flow.Publisher<List<BoardShare>> getShares(Board.ID boardId);

    CompletableFuture<Void> addShare(Board.ID boardId, User.ID userId, Board.Permissions permissions);

    CompletableFuture<Void> updateShare(Board.ID boardId, User.ID userId, Board.Permissions permissions);

    CompletableFuture<Void> removeShare(Board.ID boardId, User.ID userId);
}
