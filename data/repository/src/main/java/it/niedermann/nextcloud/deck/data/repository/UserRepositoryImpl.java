package it.niedermann.nextcloud.deck.data.repository;

import org.reactivestreams.FlowAdapters;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Flow;
import java.util.logging.Logger;

import io.reactivex.rxjava3.schedulers.Schedulers;
import it.niedermann.nextcloud.deck.data.local.dao.UserDao;
import it.niedermann.nextcloud.deck.data.local.entity.UserEntity;
import it.niedermann.nextcloud.deck.data.local.mapper.UserMapper;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.Avatar;
import it.niedermann.nextcloud.deck.domain.model.DBStatus;
import it.niedermann.nextcloud.deck.domain.model.User;
import it.niedermann.nextcloud.deck.domain.repository.AccountRepository;
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
    public CompletableFuture<Avatar> getAvatar(Account account, User.RemoteID remoteId, int sizeInPx) {
        final var ocsApi = apiFactory.create(account).getOcsApi();
        return ocsApi.getAvatar(remoteId.value(), sizeInPx)
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
        return getAvatar(account, new User.RemoteID(account.username()), sizeInPx);
    }

    @Override
    public CompletableFuture<Avatar> getAvatar(User.ID userId, int sizeInPx) {
        return userDao.getUserByLocalId(userId.value())
                .thenCompose(userEntity -> {
                    if (userEntity == null) {
                        final var f = new CompletableFuture<Avatar>();
                        f.completeExceptionally(new IllegalArgumentException("User not found: " + userId.value()));
                        return f;
                    }
                    return accountRepository.getAccountSync(new Account.ID(userEntity.getAccountId()))
                            .thenCompose(account -> getAvatar(account, new User.RemoteID(userEntity.getRemoteId()), sizeInPx));
                });
    }

    @Override
    public Flow.Publisher<List<User>> getNotDeletedUsers(Account.ID accountId) {
        return FlowAdapters.toFlowPublisher(
                userDao.getUsersByAccount(accountId.value())
                        .map(userMapper::toTOList)
                        .subscribeOn(Schedulers.io())
        );
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
        return userDao.getUserByLocalId(userId.value())
                .thenApply(entity -> entity != null ? new Account.ID(entity.getAccountId()) : null);
    }

    @Override
    public CompletableFuture<User.RemoteID> getRemoteIdByUserId(User.ID userId) {
        return userDao.getUserByLocalId(userId.value())
                .thenApply(entity -> entity != null && entity.getRemoteId() != null ? new User.RemoteID(entity.getRemoteId()) : null);
    }

    @Override
    public CompletableFuture<User.ID> getUserId(Account account, User.RemoteID remoteId) {
        return userDao.getUserByRemoteId(account.id().value(), remoteId.value())
                .thenCompose(userEntity -> {
                    if (userEntity != null) {
                        return CompletableFuture.completedFuture(new User.ID(userEntity.getLocalId()));
                    } else {
                        final var newUser = new UserEntity(
                                0,
                                account.id().value(),
                                remoteId.value(),
                                DBStatus.UP_TO_DATE.getId(),
                                null,
                                OffsetDateTime.now(),
                                null,
                                remoteId.value()
                        );
                        return userDao.insertOrReplace(newUser)
                                .thenApply(User.ID::new);
                    }
                });
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
