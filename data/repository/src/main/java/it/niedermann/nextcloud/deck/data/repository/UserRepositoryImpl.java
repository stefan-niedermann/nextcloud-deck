package it.niedermann.nextcloud.deck.data.repository;

import org.reactivestreams.FlowAdapters;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.Flowable;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.User;
import it.niedermann.nextcloud.deck.domain.repository.UserRepository;
import jakarta.inject.Inject;

public class UserRepositoryImpl implements UserRepository {

    @Inject
    public UserRepositoryImpl() {

    }

    @Override
    public Flow.Publisher<Collection<?>> getNotDeletedUsers(long accountId) {
        System.out.println("[Mock][" + UserRepositoryImpl.class.getSimpleName() + "/getNotDeletedUsers]: " + accountId);
        return null;
    }

    @Override
    public Flow.Publisher<Collection<User>> getUser(String userId) {
        System.out.println("[Mock][" + UserRepositoryImpl.class.getSimpleName() + "/getUser]: " + userId);
        return null;
    }

    @Override
    public Flow.Publisher<User> getUserByAccountId(Account.ID accountId) {
        System.out.println("[Mock][" + UserRepositoryImpl.class.getSimpleName() + "/getUserByAccountId]: " + accountId);
        return null;
    }

    @Override
    public CompletableFuture<Account.ID> getAccountIdByUserId(User.ID userId) {
        System.out.println("[Mock][" + UserRepositoryImpl.class.getSimpleName() + "/getUserByAccountId]: " + userId);
        return CompletableFuture.completedFuture(new Account.ID(1L));
    }

    @Override
    public Flow.Publisher<Collection<User>> find(String userText) {
        System.out.println("[Mock][" + UserRepositoryImpl.class.getSimpleName() + "/find]: " + userText);
        return FlowAdapters.toFlowPublisher(Flowable.just(Arrays.stream(MockData.MOCK_USERS)
                .filter(user ->
                        user.displayName().toLowerCase().contains(userText.trim().toLowerCase()) ||
                        user.id().value().toLowerCase().contains(userText.trim().toLowerCase())
                )
                .collect(Collectors.toSet())));
    }
}