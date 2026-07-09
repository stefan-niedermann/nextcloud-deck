package it.niedermann.nextcloud.deck.domain.repository;

import java.util.Collection;
import java.util.concurrent.Flow;

import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.User;

public interface UserRepository {

    Flow.Publisher<Collection<?>> getNotDeletedUsers(long accountId);

    Flow.Publisher<Collection<User>> getUser(String userId);

    Flow.Publisher<User> getUserByAccountId(Account.ID accountId);

    Flow.Publisher<Collection<User>> find(String userText);
}
