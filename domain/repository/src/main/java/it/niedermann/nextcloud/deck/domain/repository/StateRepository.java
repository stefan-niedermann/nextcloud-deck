package it.niedermann.nextcloud.deck.domain.repository;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;

import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.Board;

public interface StateRepository {

    CompletableFuture<Account.ID> setCurrentAccountId(Account.ID id);

    Flow.Publisher<Account.ID> getCurrentAccountId();

    CompletableFuture<Board.ID> setCurrentBoardId(Account.ID id, Board.ID boardId);

    Flow.Publisher<Board.ID> getCurrentBoardId(Account.ID id);

    CompletableFuture<Void> reset();
}
