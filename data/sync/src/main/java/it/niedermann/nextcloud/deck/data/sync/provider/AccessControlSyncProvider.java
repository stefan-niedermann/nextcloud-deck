package it.niedermann.nextcloud.deck.data.sync.provider;

import java.time.OffsetDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.logging.Logger;

import it.niedermann.nextcloud.deck.data.local.dao.AccessControlDao;
import it.niedermann.nextcloud.deck.data.local.dao.BoardDao;
import it.niedermann.nextcloud.deck.data.local.dao.UserDao;
import it.niedermann.nextcloud.deck.data.local.entity.AccessControlEntity;
import it.niedermann.nextcloud.deck.data.local.mapper.AccessControlMapper;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.DBStatus;
import it.niedermann.nextcloud.deck.domain.state.SyncStatus;
import it.niedermann.nextcloud.remote.ApiProvider;
import it.niedermann.nextcloud.remote.deck.DeckApi;
import it.niedermann.nextcloud.remote.deck.dto.AccessControlDTO;
import it.niedermann.nextcloud.remote.deck.dto.BoardDTO;
import it.niedermann.nextcloud.remote.deck.mapper.AccessControlRemoteMapper;
import jakarta.inject.Inject;

public class AccessControlSyncProvider implements SyncProvider<BoardDTO> {

    private static final Logger logger = Logger.getLogger(AccessControlSyncProvider.class.getName());

    private final AccessControlDao accessControlDao;
    private final BoardDao boardDao;
    private final UserDao userDao;
    private final UserSyncHelper userSyncHelper;
    private final ApiProvider.Factory apiFactory;

    @Inject
    public AccessControlSyncProvider(AccessControlDao accessControlDao, BoardDao boardDao, UserDao userDao, UserSyncHelper userSyncHelper, ApiProvider.Factory apiFactory) {
        this.accessControlDao = accessControlDao;
        this.boardDao = boardDao;
        this.userDao = userDao;
        this.userSyncHelper = userSyncHelper;
        this.apiFactory = apiFactory;
    }

    @Override
    public CompletableFuture<Void> upSync(Account account, SyncStatus status, Consumer<SyncStatus> reporter) {
        return accessControlDao.getChangedAcl(account.id().value())
                .thenCompose(changedAcl -> {
                    if (changedAcl == null || changedAcl.isEmpty()) return CompletableFuture.completedFuture(null);
                    CompletableFuture<Void> future = CompletableFuture.completedFuture(null);
                    for (AccessControlEntity localAcl : changedAcl) {
                        final var finalFuture = future;
                        future = finalFuture.thenCompose(v -> upSyncSingle(account, localAcl));
                    }
                    return future;
                });
    }

    private CompletableFuture<Void> upSyncSingle(Account account, AccessControlEntity localAcl) {
        DeckApi api = apiFactory.create(account).getDeckApi();
        return boardDao.getBoardById(localAcl.getBoardId())
                .thenCompose(boardEntity -> {
                    if (boardEntity == null || boardEntity.getRemoteId() == null) {
                        return CompletableFuture.completedFuture(null);
                    }
                    if (localAcl.getUserId() == null) {
                        return CompletableFuture.completedFuture(null);
                    }
                    return userDao.getUserByLocalId(localAcl.getUserId()).thenCompose(userEntity -> {
                        if (userEntity == null) return CompletableFuture.completedFuture(null);

                        AccessControlDTO dto = AccessControlRemoteMapper.INSTANCE.toDTO(AccessControlMapper.INSTANCE.toTO(localAcl));
                        // The participant should be the user's remote ID
                        if (dto.getParticipant() == null) {
                            dto.setParticipant(new it.niedermann.nextcloud.remote.deck.dto.UserDTO());
                        }
                        dto.getParticipant().setUid(userEntity.getRemoteId());
                        dto.getParticipant().setDisplayname(userEntity.getDisplayName());
                        dto.getParticipant().setPrimaryKey(userEntity.getRemoteId());
                        dto.getParticipant().setType(0);
                        dto.setBoardId(null);

                        CompletableFuture<AccessControlDTO> call;
                        if (localAcl.getRemoteId() == null) {
                            call = api.createAccessControl(boardEntity.getRemoteId(), dto);
                        } else if (localAcl.getStatus() == DBStatus.LOCAL_DELETED.getId()) {
                            if (localAcl.getRemoteId() == null) {
                                return accessControlDao.deleteRx(localAcl).thenApply(v -> null);
                            }
                            return api.deleteAccessControl(boardEntity.getRemoteId(), localAcl.getRemoteId())
                                    .thenCompose(v -> accessControlDao.deleteRx(localAcl))
                                    .thenApply(v -> null);
                        } else {
                            call = api.updateAccessControl(boardEntity.getRemoteId(), localAcl.getRemoteId(), dto);
                        }

                        return call.thenCompose(response -> {
                            if (response == null) return CompletableFuture.completedFuture(null);
                            AccessControlEntity updatedLocal = AccessControlMapper.INSTANCE.toEntity(AccessControlRemoteMapper.INSTANCE.toTO(response));
                            updatedLocal = new AccessControlEntity(
                                    localAcl.getLocalId(),
                                    localAcl.getAccountId(),
                                    updatedLocal.getRemoteId(),
                                    DBStatus.UP_TO_DATE.getId(),
                                    updatedLocal.getLastModified(),
                                    OffsetDateTime.now(),
                                    updatedLocal.getEtag(),
                                    updatedLocal.getType(),
                                    localAcl.getBoardId(),
                                    updatedLocal.getOwner(),
                                    updatedLocal.getPermissionEdit(),
                                    updatedLocal.getPermissionShare(),
                                    updatedLocal.getPermissionManage(),
                                    localAcl.getUserId(),
                                    null
                            );
                            return accessControlDao.updateRx(updatedLocal);
                        }).thenApply(v -> null);
                    });
                });
    }

    @Override
    public CompletableFuture<Void> downSync(Account account, BoardDTO parent, Long parentLocalId, SyncStatus status, Consumer<SyncStatus> reporter) {
        if (parent == null) return CompletableFuture.completedFuture(null);
        logger.info("Syncing ACL for board " + parent.getId());
        return accessControlDao.deleteByBoardId(parentLocalId)
                .thenCompose(v -> {
                    if (parent.getAcl() != null && !parent.getAcl().isEmpty()) {
                        logger.info("Syncing " + parent.getAcl().size() + " ACL entries for board " + parent.getId());
                        CompletableFuture<?>[] futures = new CompletableFuture[parent.getAcl().size()];
                        for (int i = 0; i < parent.getAcl().size(); i++) {
                            futures[i] = mergeAcl(account, parent.getAcl().get(i), parentLocalId);
                        }
                        return CompletableFuture.allOf(futures);
                    }
                    return CompletableFuture.completedFuture(null);
                });
    }

    private CompletableFuture<Void> mergeAcl(Account account, AccessControlDTO dto, Long boardId) {
        if (dto.getId() == null) return CompletableFuture.completedFuture(null);
        logger.info("Merging ACL " + dto.getId() + " for board " + boardId);
        return userSyncHelper.syncUser(account, dto.getParticipant())
                .thenCompose(localUser -> accessControlDao.getAclByRemoteId(account.id().value(), dto.getId())
                        .handle((localAcl, throwable) -> {
                            if (throwable != null) {
                                logger.log(java.util.logging.Level.SEVERE, "Failed to get local ACL " + dto.getId(), throwable);
                            }
                            AccessControlEntity serverEntity = AccessControlMapper.INSTANCE.toEntity(AccessControlRemoteMapper.INSTANCE.toTO(dto));
                            final Long localUserId = localUser != null ? localUser.getLocalId() : null;
                            if (throwable != null || localAcl == null) {
                                logger.info("Inserting new ACL " + dto.getId());
                                AccessControlEntity newLocal = new AccessControlEntity(
                                        0,
                                        account.id().value(),
                                        serverEntity.getRemoteId(),
                                        DBStatus.UP_TO_DATE.getId(),
                                        serverEntity.getLastModified(),
                                        serverEntity.getLastModified(),
                                        serverEntity.getEtag(),
                                        serverEntity.getType(),
                                        boardId,
                                        serverEntity.getOwner(),
                                        serverEntity.getPermissionEdit(),
                                        serverEntity.getPermissionShare(),
                                        serverEntity.getPermissionManage(),
                                        localUserId,
                                        null
                                );
                                return accessControlDao.upsert(newLocal);
                            } else {
                                logger.info("Updating existing ACL " + dto.getId());
                                if (localAcl.getStatus() == DBStatus.CONFLICT.getId()) {
                                    return CompletableFuture.completedFuture(localAcl.getLocalId());
                                }
                                if (serverEntity.getEtag() != null && serverEntity.getEtag().equals(localAcl.getEtag())) {
                                    return CompletableFuture.completedFuture(localAcl.getLocalId());
                                }
                                AccessControlEntity updatedLocal = new AccessControlEntity(
                                        localAcl.getLocalId(),
                                        localAcl.getAccountId(),
                                        serverEntity.getRemoteId(),
                                        DBStatus.UP_TO_DATE.getId(),
                                        serverEntity.getLastModified(),
                                        serverEntity.getLastModified(),
                                        serverEntity.getEtag(),
                                        serverEntity.getType(),
                                        boardId,
                                        serverEntity.getOwner(),
                                        serverEntity.getPermissionEdit(),
                                        serverEntity.getPermissionShare(),
                                        serverEntity.getPermissionManage(),
                                        localUserId,
                                        null
                                );
                                return accessControlDao.updateRx(updatedLocal).thenApply(v -> localAcl.getLocalId());
                            }
                        }).thenCompose(f -> f).thenApply(v -> null));
    }
}
