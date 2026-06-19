package it.niedermann.nextcloud.deck.domain.repository;

import java.util.Collection;
import java.util.concurrent.Flow;

import it.niedermann.nextcloud.deck.domain.model.Board;

public interface UserRepository {

    Flow.Publisher<Collection<?>> getNotDeletedUsers(long accountId);

    Flow.Publisher<Collection<Board>> getUser(long userId);

    Flow.Publisher<Collection<Board>> getUser(long accountId, String username);
}
