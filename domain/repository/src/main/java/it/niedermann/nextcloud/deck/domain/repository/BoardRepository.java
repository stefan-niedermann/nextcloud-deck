package it.niedermann.nextcloud.deck.domain.repository;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;

import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.CreateBoard;

public interface BoardRepository {

    CompletableFuture<Void> createBoard(CreateBoard board);

    CompletableFuture<Void> updateBoard(Board board);

    Flow.Publisher<Board> getBoard(Board.ID boardId);

    Flow.Publisher<List<Board>> getNotDeletedBoards(Account.ID accountId);
}
