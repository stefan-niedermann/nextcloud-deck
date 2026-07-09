package it.niedermann.nextcloud.deck.domain.repository;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;

import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.Board;

public interface BoardRepository {

    CompletableFuture<Void> createBoard(Board board);

    Flow.Publisher<Board> getBoard(Board.ID boardId);

    Flow.Publisher<List<Board>> getNotDeletedBoards(Account.ID accountId);
}
