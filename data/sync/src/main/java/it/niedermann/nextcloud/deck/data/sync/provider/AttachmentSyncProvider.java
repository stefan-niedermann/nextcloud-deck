package it.niedermann.nextcloud.deck.data.sync.provider;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import it.niedermann.nextcloud.deck.data.local.dao.AttachmentDao;
import it.niedermann.nextcloud.deck.data.local.entity.AttachmentEntity;
import it.niedermann.nextcloud.deck.data.local.mapper.AttachmentMapper;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.DBStatus;
import it.niedermann.nextcloud.deck.domain.state.SyncStatus;
import it.niedermann.nextcloud.remote.ApiProvider;
import it.niedermann.nextcloud.remote.deck.dto.AttachmentDTO;
import it.niedermann.nextcloud.remote.deck.dto.CardDTO;
import it.niedermann.nextcloud.remote.deck.mapper.AttachmentRemoteMapper;
import jakarta.inject.Inject;

public class AttachmentSyncProvider implements SyncProvider<CardDTO> {

    private final AttachmentDao attachmentDao;

    @Inject
    public AttachmentSyncProvider(AttachmentDao attachmentDao, ApiProvider.Factory apiFactory) {
        this.attachmentDao = attachmentDao;
    }

    @Override
    public CompletableFuture<Void> upSync(Account account, SyncStatus status, Consumer<SyncStatus> reporter) {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> downSync(Account account, CardDTO parent, Long parentLocalId, SyncStatus status, Consumer<SyncStatus> reporter) {
        if (parent == null) return CompletableFuture.completedFuture(null);
        if (parent.getAttachments() != null && !parent.getAttachments().isEmpty()) {
            CompletableFuture<Void> future = CompletableFuture.completedFuture(null);
            for (AttachmentDTO dto : parent.getAttachments()) {
                final var finalFuture = future;
                future = finalFuture.thenCompose(v -> mergeAttachment(account, dto, parentLocalId));
            }
            return future;
        }
        return CompletableFuture.completedFuture(null);
    }

    private CompletableFuture<Void> mergeAttachment(Account account, AttachmentDTO dto, Long cardId) {
        AttachmentEntity serverEntity = AttachmentMapper.INSTANCE.toEntity(AttachmentRemoteMapper.INSTANCE.toTO(dto));
        AttachmentEntity newLocal = new AttachmentEntity(
                0,
                account.id().value(),
                serverEntity.getRemoteId(),
                DBStatus.UP_TO_DATE.getId(),
                serverEntity.getLastModified(),
                serverEntity.getLastModified(),
                serverEntity.getEtag(),
                cardId,
                serverEntity.getType(),
                serverEntity.getData(),
                serverEntity.getCreatedAt(),
                serverEntity.getCreatedBy(),
                serverEntity.getDeletedAt(),
                serverEntity.getFilesize(),
                serverEntity.getMimetype(),
                serverEntity.getDirname(),
                serverEntity.getBasename(),
                serverEntity.getExtension(),
                serverEntity.getFilename(),
                serverEntity.getLocalPath(),
                serverEntity.getFileId(),
                null
        );
        return attachmentDao.insert(newLocal).thenApply(v -> null);
    }
}
