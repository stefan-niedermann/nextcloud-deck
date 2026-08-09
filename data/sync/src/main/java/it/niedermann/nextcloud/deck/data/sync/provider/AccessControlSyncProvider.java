package it.niedermann.nextcloud.deck.data.sync.provider;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import it.niedermann.nextcloud.deck.data.local.dao.AccessControlDao;
import it.niedermann.nextcloud.deck.data.local.entity.AccessControlEntity;
import it.niedermann.nextcloud.deck.data.local.mapper.AccessControlMapper;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.DBStatus;
import it.niedermann.nextcloud.deck.domain.state.SyncStatus;
import it.niedermann.nextcloud.remote.ApiProvider;
import it.niedermann.nextcloud.remote.deck.dto.AccessControlDTO;
import it.niedermann.nextcloud.remote.deck.dto.BoardDTO;
import it.niedermann.nextcloud.remote.deck.mapper.AccessControlRemoteMapper;
import jakarta.inject.Inject;

public class AccessControlSyncProvider implements SyncProvider<BoardDTO> {

    private final AccessControlDao accessControlDao;

    @Inject
    public AccessControlSyncProvider(AccessControlDao accessControlDao, ApiProvider.Factory apiFactory) {
        this.accessControlDao = accessControlDao;
    }

    @Override
    public CompletableFuture<Void> upSync(Account account, SyncStatus status, Consumer<SyncStatus> reporter) {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> downSync(Account account, BoardDTO parent, Long parentLocalId, SyncStatus status, Consumer<SyncStatus> reporter) {
        if (parent == null) return CompletableFuture.completedFuture(null);
        if (parent.getAcl() != null && !parent.getAcl().isEmpty()) {
            CompletableFuture<Void> future = CompletableFuture.completedFuture(null);
            for (AccessControlDTO dto : parent.getAcl()) {
                final var finalFuture = future;
                future = finalFuture.thenCompose(v -> mergeAcl(account, dto, parentLocalId));
            }
            return future;
        }
        return CompletableFuture.completedFuture(null);
    }

    private CompletableFuture<Void> mergeAcl(Account account, AccessControlDTO dto, Long boardId) {
        AccessControlEntity serverEntity = AccessControlMapper.INSTANCE.toEntity(AccessControlRemoteMapper.INSTANCE.toTO(dto));
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
                serverEntity.getUserId(),
                null
        );
        return accessControlDao.insert(newLocal).thenApply(v -> null);
    }
}
