package it.niedermann.nextcloud.deck.data.repository;

import org.reactivestreams.FlowAdapters;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;

import io.reactivex.rxjava3.schedulers.Schedulers;
import it.niedermann.nextcloud.deck.data.local.dao.AccessControlDao;
import it.niedermann.nextcloud.deck.data.local.dao.BoardDao;
import it.niedermann.nextcloud.deck.data.local.dao.UserDao;
import it.niedermann.nextcloud.deck.data.local.entity.AccessControlEntity;
import it.niedermann.nextcloud.deck.data.local.entity.UserEntity;
import it.niedermann.nextcloud.deck.data.local.mapper.AccessControlMapper;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.BoardShare;
import it.niedermann.nextcloud.deck.domain.model.DBStatus;
import it.niedermann.nextcloud.deck.domain.model.User;
import it.niedermann.nextcloud.deck.domain.repository.AccountRepository;
import it.niedermann.nextcloud.deck.domain.repository.ShareRepository;
import it.niedermann.nextcloud.remote.ApiProvider;
import it.niedermann.nextcloud.remote.ocs.dto.OcsUserDTO;
import jakarta.inject.Inject;

public class ShareRepositoryImpl implements ShareRepository {

    private final AccessControlDao accessControlDao;
    private final BoardDao boardDao;
    private final UserDao userDao;
    private final AccessControlMapper accessControlMapper;
    private final ApiProvider.Factory apiFactory;
    private final AccountRepository accountRepository;

    @Inject
    public ShareRepositoryImpl(AccessControlDao accessControlDao,
                               BoardDao boardDao,
                               UserDao userDao,
                               AccessControlMapper accessControlMapper,
                               ApiProvider.Factory apiFactory,
                               AccountRepository accountRepository) {
        this.accessControlDao = accessControlDao;
        this.boardDao = boardDao;
        this.userDao = userDao;
        this.accessControlMapper = accessControlMapper;
        this.apiFactory = apiFactory;
        this.accountRepository = accountRepository;
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
        return boardDao.getBoardById(boardId.value())
                .thenCompose(boardEntity -> {
                    if (boardEntity == null) {
                        final var future = new CompletableFuture<Void>();
                        future.completeExceptionally(new IllegalArgumentException("Board not found: " + boardId.value()));
                        return future;
                    }
                    return userDao.getUserByRemoteId(boardEntity.getAccountId(), userId.value())
                            .thenCompose(userEntity -> {
                                if (userEntity != null) {
                                    return CompletableFuture.completedFuture(userEntity);
                                } else {
                                    return accountRepository.getAccountSync(new it.niedermann.nextcloud.deck.domain.model.Account.ID(boardEntity.getAccountId()))
                                            .thenCompose(account -> apiFactory.create(account).getOcsApi().getUser(null, userId.value()))
                                            .thenCompose(ocsResponse -> {
                                                if (ocsResponse == null || ocsResponse.getOcs() == null || ocsResponse.getOcs().getData() == null) {
                                                    final var future = new CompletableFuture<UserEntity>();
                                                    future.completeExceptionally(new IllegalArgumentException("User not found on server: " + userId.value()));
                                                    return future;
                                                }
                                                OcsUserDTO data = ocsResponse.getOcs().getData();
                                                final var newUser = new UserEntity(
                                                        0,
                                                        boardEntity.getAccountId(),
                                                        data.getId(),
                                                        DBStatus.UP_TO_DATE.getId(),
                                                        null,
                                                        OffsetDateTime.now(),
                                                        null,
                                                        data.getDisplayname()
                                                );
                                                return userDao.insertOrReplace(newUser)
                                                        .thenCompose(localId -> userDao.getUserByRemoteId(boardEntity.getAccountId(), userId.value()));
                                            });
                                }
                            }).thenCompose(userEntity -> {
                                if (userEntity == null) {
                                    final var future = new CompletableFuture<Void>();
                                    future.completeExceptionally(new IllegalStateException("Failed to retrieve user entity after sync"));
                                    return future;
                                }
                                final var acl = new AccessControlEntity(
                                        0,
                                        boardEntity.getAccountId(),
                                        null,
                                        DBStatus.LOCAL_EDITED.getId(),
                                        null,
                                        OffsetDateTime.now(),
                                        null,
                                        0L, // type 0 = user
                                        boardEntity.getLocalId(),
                                        false,
                                        permissions.permissionEdit(),
                                        permissions.permissionShare(),
                                        permissions.permissionManage(),
                                        userEntity.getLocalId(),
                                        null
                                );
                                return accessControlDao.insert(acl).thenApply(v -> null);
                            });
                });
    }

    @Override
    public CompletableFuture<Void> updateShare(Board.ID boardId, User.ID userId, Board.Permissions permissions) {
        return boardDao.getBoardById(boardId.value())
                .thenCompose(boardEntity -> {
                    if (boardEntity == null) return CompletableFuture.completedFuture(null);
                    return userDao.getUserByRemoteId(boardEntity.getAccountId(), userId.value())
                            .thenCompose(userEntity -> {
                                if (userEntity == null) return CompletableFuture.completedFuture(null);
                                return accessControlDao.getAclByBoardAndUser(boardId.value(), userEntity.getLocalId())
                                        .thenCompose(entity -> {
                                            if (entity == null) return CompletableFuture.completedFuture(null);
                                            final var updatedAcl = new AccessControlEntity(
                                                    entity.getLocalId(),
                                                    entity.getAccountId(),
                                                    entity.getRemoteId(),
                                                    DBStatus.LOCAL_EDITED.getId(),
                                                    entity.getLastModified(),
                                                    OffsetDateTime.now(),
                                                    entity.getEtag(),
                                                    entity.getType(),
                                                    entity.getBoardId(),
                                                    entity.getOwner(),
                                                    permissions.permissionEdit(),
                                                    permissions.permissionShare(),
                                                    permissions.permissionManage(),
                                                    entity.getUserId(),
                                                    entity.getConflictWithId()
                                            );
                                            return accessControlDao.updateRx(updatedAcl).thenApply(v -> null);
                                        });
                            });
                });
    }

    @Override
    public CompletableFuture<Void> removeShare(Board.ID boardId, User.ID userId) {
        return boardDao.getBoardById(boardId.value())
                .thenCompose(boardEntity -> {
                    if (boardEntity == null) return CompletableFuture.completedFuture(null);
                    return userDao.getUserByRemoteId(boardEntity.getAccountId(), userId.value())
                            .thenCompose(userEntity -> {
                                if (userEntity == null) return CompletableFuture.completedFuture(null);
                                return accessControlDao.getAclByBoardAndUser(boardId.value(), userEntity.getLocalId())
                                        .thenCompose(entity -> {
                                            if (entity == null) return CompletableFuture.completedFuture(null);
                                            if (entity.getRemoteId() == null) {
                                                return accessControlDao.deleteRx(entity).thenApply(v -> null);
                                            } else {
                                                final var deletedAcl = new AccessControlEntity(
                                                        entity.getLocalId(),
                                                        entity.getAccountId(),
                                                        entity.getRemoteId(),
                                                        DBStatus.LOCAL_DELETED.getId(),
                                                        entity.getLastModified(),
                                                        OffsetDateTime.now(),
                                                        entity.getEtag(),
                                                        entity.getType(),
                                                        entity.getBoardId(),
                                                        entity.getOwner(),
                                                        entity.getPermissionEdit(),
                                                        entity.getPermissionShare(),
                                                        entity.getPermissionManage(),
                                                        entity.getUserId(),
                                                        entity.getConflictWithId()
                                                );
                                                return accessControlDao.updateRx(deletedAcl).thenApply(v -> null);
                                            }
                                        });
                            });
                });
    }
}
