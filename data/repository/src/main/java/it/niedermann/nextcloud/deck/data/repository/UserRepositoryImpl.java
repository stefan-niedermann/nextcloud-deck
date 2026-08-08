package it.niedermann.nextcloud.deck.data.repository;

import org.reactivestreams.FlowAdapters;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.Flowable;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.Avatar;
import it.niedermann.nextcloud.deck.domain.model.User;
import it.niedermann.nextcloud.deck.domain.repository.AccountRepository;
import it.niedermann.nextcloud.deck.domain.repository.UserRepository;
import it.niedermann.nextcloud.remote.ApiProvider;
import jakarta.inject.Inject;
import retrofit2.HttpException;

public class UserRepositoryImpl implements UserRepository {

    private final ApiProvider.Factory apiFactory;
    private final AccountRepository accountRepository;

    @Inject
    public UserRepositoryImpl(ApiProvider.Factory apiFactory, AccountRepository accountRepository) {
        this.apiFactory = apiFactory;
        this.accountRepository = accountRepository;
    }

    @Override
    public CompletableFuture<Avatar> getAvatar(Account account, User.ID userId, int sizeInPx) {
        return CompletableFuture.completedFuture(account)
                .thenApplyAsync(apiFactory::create)
                .thenApplyAsync(ApiProvider::getOcsApi)
                .thenComposeAsync(ocsApi -> ocsApi.getAvatar(userId.value(), sizeInPx))
                .thenComposeAsync(response -> {
                    if (response.isSuccessful()) {
                        try (final var body = response.body()) {
                            if (body != null) {
                                final var contentType = body.contentType();
                                final var mimeType = contentType != null ? contentType.toString() : null;
                                final var eTag = response.headers().get("ETag");
                                final var content = body.bytes();
                                return CompletableFuture.completedFuture(new Avatar(mimeType, eTag, sizeInPx, content));
                            } else {
                                final var future = new CompletableFuture<Avatar>();
                                future.completeExceptionally(new IOException("Empty response body"));
                                return future;
                            }
                        } catch (IOException | RuntimeException e) {
                            final var future = new CompletableFuture<Avatar>();
                            future.completeExceptionally(e);
                            return future;
                        }
                    } else {
                        final var future = new CompletableFuture<Avatar>();
                        future.completeExceptionally(new HttpException(response));
                        return future;
                    }
                });
    }

    @Override
    public CompletableFuture<Avatar> getAvatar(Account account, int sizeInPx) {
        return getAvatar(account, new User.ID(account.username()), sizeInPx);
    }

    @Override
    public CompletableFuture<Avatar> getAvatar(User.ID userId, int sizeInPx) {
        return getAccountIdByUserId(userId)
                .thenCompose(accountRepository::getAccountSync)
                .thenCompose(account -> getAvatar(account, userId, sizeInPx));
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
