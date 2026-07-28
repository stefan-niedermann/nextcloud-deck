package it.niedermann.nextcloud.deck.data.repository;

import org.reactivestreams.FlowAdapters;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;

import io.reactivex.rxjava3.core.Flowable;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.BoardShare;
import it.niedermann.nextcloud.deck.domain.model.User;
import it.niedermann.nextcloud.deck.domain.repository.ShareRepository;
import jakarta.inject.Inject;

public class ShareRepositoryImpl implements ShareRepository {

    @Inject
    public ShareRepositoryImpl() {
    }

    @Override
    public Flow.Publisher<List<BoardShare>> getShares(Board.ID boardId) {
        // TODO Implement
        return FlowAdapters.toFlowPublisher(Flowable.just(Collections.emptyList()));
    }

    @Override
    public CompletableFuture<Void> addShare(Board.ID boardId, User.ID userId, Board.Permissions permissions) {
        // TODO Implement
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> updateShare(Board.ID boardId, User.ID userId, Board.Permissions permissions) {
        // TODO Implement
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> removeShare(Board.ID boardId, User.ID userId) {
        // TODO Implement
        return CompletableFuture.completedFuture(null);
    }
}
