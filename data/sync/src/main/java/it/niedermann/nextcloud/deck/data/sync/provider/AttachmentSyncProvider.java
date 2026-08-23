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
            CompletableFuture<?>[] futures = new CompletableFuture[parent.getAttachments().size()];
            for (int i = 0; i < parent.getAttachments().size(); i++) {
                AttachmentDTO dto = parent.getAttachments().get(i);
                futures[i] = mergeAttachment(account, dto, parentLocalId);
            }
            return CompletableFuture.allOf(futures);
        }
        return CompletableFuture.completedFuture(null);
    }

    private CompletableFuture<Void> mergeAttachment(Account account, AttachmentDTO dto, Long cardId) {
        if (dto.getId() == null) return CompletableFuture.completedFuture(null);
        return attachmentDao.getAttachmentByRemoteId(account.id().value(), dto.getId())
                .handle((localAttachment, throwable) -> {
                    AttachmentEntity serverEntity = AttachmentMapper.INSTANCE.toEntity(AttachmentRemoteMapper.INSTANCE.toTO(dto));
                    if (throwable != null || localAttachment == null) {
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
                        return attachmentDao.upsert(newLocal).thenApply(v -> (Void) null);
                    } else {
                        if (localAttachment.getStatus() == DBStatus.CONFLICT.getId()) {
                            return CompletableFuture.<Void>completedFuture(null);
                        }
                        if (serverEntity.getEtag() != null && serverEntity.getEtag().equals(localAttachment.getEtag())) {
                            return CompletableFuture.<Void>completedFuture(null);
                        }
                        AttachmentEntity updatedLocal = new AttachmentEntity(
                                localAttachment.getLocalId(),
                                localAttachment.getAccountId(),
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
                        return attachmentDao.updateRx(updatedLocal);
                    }
                }).thenCompose(f -> f);
    }
}
