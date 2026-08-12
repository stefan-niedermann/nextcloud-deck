package it.niedermann.nextcloud.deck.data.repository;

import org.reactivestreams.FlowAdapters;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;

import io.reactivex.rxjava3.schedulers.Schedulers;
import it.niedermann.nextcloud.deck.data.local.dao.AccessControlDao;
import it.niedermann.nextcloud.deck.data.local.mapper.AccessControlMapper;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.BoardShare;
import it.niedermann.nextcloud.deck.domain.model.User;
import it.niedermann.nextcloud.deck.domain.repository.ShareRepository;
import jakarta.inject.Inject;

public class ShareRepositoryImpl implements ShareRepository {

    private final AccessControlDao accessControlDao;
    private final AccessControlMapper accessControlMapper;

    @Inject
    public ShareRepositoryImpl(AccessControlDao accessControlDao,
                               AccessControlMapper accessControlMapper) {
        this.accessControlDao = accessControlDao;
        this.accessControlMapper = accessControlMapper;
    }

    @Override
    public Flow.Publisher<List<BoardShare>> getShares(Board.ID boardId) {
        // TODO: Implement real mapping to BoardShare which includes User object
        return FlowAdapters.toFlowPublisher(
                accessControlDao.getAclByBoard(boardId.value())
                        .map(entities -> Collections.<BoardShare>emptyList())
                        .subscribeOn(Schedulers.io())
        );
    }

    @Override
    public CompletableFuture<Void> addShare(Board.ID boardId, User.ID userId, Board.Permissions permissions) {
        // TODO: Implement
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> updateShare(Board.ID boardId, User.ID userId, Board.Permissions permissions) {
        // TODO: Implement
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> removeShare(Board.ID boardId, User.ID userId) {
        // TODO: Implement
        return CompletableFuture.completedFuture(null);
    }
}
