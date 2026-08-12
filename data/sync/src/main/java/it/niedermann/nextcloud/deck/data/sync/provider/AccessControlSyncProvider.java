package it.niedermann.nextcloud.deck.data.sync.provider;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.logging.Logger;

import it.niedermann.nextcloud.deck.data.local.dao.AccessControlDao;
import it.niedermann.nextcloud.deck.data.local.entity.AccessControlEntity;
import it.niedermann.nextcloud.deck.data.local.mapper.AccessControlMapper;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.DBStatus;
import it.niedermann.nextcloud.deck.domain.state.SyncStatus;
import it.niedermann.nextcloud.remote.deck.dto.AccessControlDTO;
import it.niedermann.nextcloud.remote.deck.dto.BoardDTO;
import it.niedermann.nextcloud.remote.deck.mapper.AccessControlRemoteMapper;
import jakarta.inject.Inject;

public class AccessControlSyncProvider implements SyncProvider<BoardDTO> {

    private static final Logger logger = Logger.getLogger(AccessControlSyncProvider.class.getName());

    private final AccessControlDao accessControlDao;
    private final UserSyncHelper userSyncHelper;

    @Inject
    public AccessControlSyncProvider(AccessControlDao accessControlDao, UserSyncHelper userSyncHelper) {
        this.accessControlDao = accessControlDao;
        this.userSyncHelper = userSyncHelper;
    }

    @Override
    public CompletableFuture<Void> upSync(Account account, SyncStatus status, Consumer<SyncStatus> reporter) {
        return CompletableFuture.completedFuture(null);
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
                                return accessControlDao.insertOrReplace(newLocal);
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
