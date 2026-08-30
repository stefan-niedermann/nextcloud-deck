package it.niedermann.nextcloud.deck.data.repository;

import org.reactivestreams.FlowAdapters;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Flow;
import java.util.logging.Logger;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import it.niedermann.nextcloud.deck.data.local.dao.UserDao;
import it.niedermann.nextcloud.deck.data.local.mapper.UserMapper;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.Avatar;
import it.niedermann.nextcloud.deck.domain.model.User;
import it.niedermann.nextcloud.deck.domain.repository.AccountRepository;
import it.niedermann.nextcloud.deck.domain.repository.MockData;
import it.niedermann.nextcloud.deck.domain.repository.UserRepository;
import it.niedermann.nextcloud.remote.ApiProvider;
import jakarta.inject.Inject;
import retrofit2.HttpException;

public class UserRepositoryImpl implements UserRepository {

    private static final Logger logger = Logger.getLogger(UserRepositoryImpl.class.getName());

    private final ApiProvider.Factory apiFactory;
    private final AccountRepository accountRepository;
    private final UserDao userDao;
    private final UserMapper userMapper;

    @Inject
    public UserRepositoryImpl(ApiProvider.Factory apiFactory,
                              AccountRepository accountRepository,
                              UserDao userDao,
                              UserMapper userMapper) {
        this.apiFactory = apiFactory;
        this.accountRepository = accountRepository;
        this.userDao = userDao;
        this.userMapper = userMapper;
    }

    @Override
    public CompletableFuture<Avatar> getAvatar(Account account, User.ID userId, int sizeInPx) {
        final var ocsApi = apiFactory.create(account).getOcsApi();
        return ocsApi.getAvatar(userId.value(), sizeInPx)
                .thenApplyAsync(response -> {
                    if (response.isSuccessful()) {
                        try (final var body = response.body()) {
                            if (body != null) {
                                final var contentType = body.contentType();
                                final var mimeType = contentType != null ? contentType.toString() : null;
                                final var eTag = response.headers().get("ETag");
                                final var content = body.bytes();
                                return new Avatar(mimeType, eTag, sizeInPx, content);
                            } else {
                                throw new IOException("Empty response body");
                            }
                        } catch (IOException exception) {
                            throw new CompletionException(exception);
                        }
                    } else {
                        throw new HttpException(response);
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
    public Flow.Publisher<List<User>> getNotDeletedUsers(Account.ID accountId) {
        logger.info("[Mock][getNotDeletedUsers]: " + accountId);
        return FlowAdapters.toFlowPublisher(Flowable.just(Arrays.asList(MockData.MOCK_USERS)));
    }

    @Override
    public Flow.Publisher<Collection<User>> getUser(String userId) {
        logger.info("[Mock][getUser]: " + userId);
        return null;
    }

    @Override
    public Flow.Publisher<User> getUserByAccountId(Account.ID accountId) {
        logger.info("[Mock][getUserByAccountId]: " + accountId);
        return null;
    }

    @Override
    public CompletableFuture<Account.ID> getAccountIdByUserId(User.ID userId) {
        logger.info("[Mock][getUserByAccountId]: " + userId);
        return CompletableFuture.completedFuture(new Account.ID(1L));
    }

    @Override
    public Flow.Publisher<Collection<User>> find(String userText) {
        return FlowAdapters.toFlowPublisher(
                userDao.findUsers(userText)
                        .map(entities -> (Collection<User>) userMapper.toTOList(entities))
                        .subscribeOn(Schedulers.io())
        );
    }
}
