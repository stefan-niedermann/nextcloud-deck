package it.niedermann.nextcloud.deck.domain.repository;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;

public interface StateRepository {

    CompletableFuture<Long> setCurrentAccountId(long accountId);

    Flow.Publisher<Long> getCurrentAccountId();

    CompletableFuture<Long> setCurrentBoardId(long accountId, long boardId);

    Flow.Publisher<Long> getCurrentBoardId(long accountId);

    CompletableFuture<Void> reset();
}
