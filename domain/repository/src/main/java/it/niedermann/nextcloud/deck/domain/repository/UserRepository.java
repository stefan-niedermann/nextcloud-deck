package it.niedermann.nextcloud.deck.domain.repository;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;

import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.Avatar;
import it.niedermann.nextcloud.deck.domain.model.User;

public interface UserRepository {

    CompletableFuture<Avatar> getAvatar(Account account, User.ID userId, int sizeInPx);

    CompletableFuture<Avatar> getAvatar(Account account, int sizeInPx);

    CompletableFuture<Avatar> getAvatar(User.ID userId, int sizeInPx);

    Flow.Publisher<List<User>> getNotDeletedUsers(Account.ID accountId);

    Flow.Publisher<Collection<User>> getUser(String userId);

    Flow.Publisher<User> getUserByAccountId(Account.ID accountId);

    CompletableFuture<Account.ID> getAccountIdByUserId(User.ID userId);

    Flow.Publisher<Collection<User>> find(String userText);
}
