package it.niedermann.nextcloud.deck.data.sync.provider;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import it.niedermann.nextcloud.deck.data.local.dao.UserDao;
import it.niedermann.nextcloud.deck.data.local.entity.UserEntity;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.DBStatus;
import it.niedermann.nextcloud.remote.deck.dto.UserDTO;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class UserSyncHelper {

    private static final Logger logger = Logger.getLogger(UserSyncHelper.class.getName());

    private final UserDao userDao;
    private final Map<String, CompletableFuture<UserEntity>> inFlightSyncs = new ConcurrentHashMap<>();

    @Inject
    public UserSyncHelper(UserDao userDao) {
        this.userDao = userDao;
    }

    public CompletableFuture<UserEntity> syncUser(Account account, UserDTO userDto) {
        if (userDto == null) {
            return CompletableFuture.completedFuture(null);
        }
        return syncUser(account, userDto.getUid(), userDto.getDisplayname());
    }

    public CompletableFuture<UserEntity> syncUser(Account account, String uid, String displayName) {
        if (uid == null) {
            return CompletableFuture.completedFuture(null);
        }
        final String key = account.id().value() + ":" + uid;

        return inFlightSyncs.compute(key, (k, existingFuture) -> {
            if (existingFuture != null && !existingFuture.isCompletedExceptionally()) {
                return existingFuture;
            }
            logger.info("Syncing user: " + uid);
            final var future = userDao.getUserByRemoteId(account.id().value(), uid)
                    .thenCompose(localUser -> {
                        final long existingLocalId = localUser != null ? localUser.getLocalId() : 0;
                        UserEntity entity = new UserEntity(
                                existingLocalId,
                                account.id().value(),
                                uid,
                                DBStatus.UP_TO_DATE.getId(),
                                null,
                                null,
                                null,
                                displayName != null ? displayName : uid
                        );
                        logger.info((localUser == null ? "Inserting" : "Updating") + " user: " + uid);
                        return userDao.upsert(entity).thenCompose(id -> {
                            if (id != -1) {
                                return CompletableFuture.completedFuture(new UserEntity(
                                        id,
                                        entity.getAccountId(),
                                        entity.getRemoteId(),
                                        entity.getStatus(),
                                        entity.getLastModified(),
                                        entity.getLastModifiedLocal(),
                                        entity.getEtag(),
                                        entity.getDisplayName()
                                ));
                            } else if (existingLocalId != 0) {
                                return CompletableFuture.completedFuture(new UserEntity(
                                        existingLocalId,
                                        entity.getAccountId(),
                                        entity.getRemoteId(),
                                        entity.getStatus(),
                                        entity.getLastModified(),
                                        entity.getLastModifiedLocal(),
                                        entity.getEtag(),
                                        entity.getDisplayName()
                                ));
                            } else {
                                // Someone else inserted it in the meantime
                                return userDao.getUserByRemoteId(account.id().value(), uid);
                            }
                        });
                    });

            future.whenComplete((v, throwable) -> inFlightSyncs.remove(key));

            return future;
        });
    }
}
