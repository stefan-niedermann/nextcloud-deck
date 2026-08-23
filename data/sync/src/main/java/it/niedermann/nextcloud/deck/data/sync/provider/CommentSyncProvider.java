package it.niedermann.nextcloud.deck.data.sync.provider;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import it.niedermann.nextcloud.deck.data.local.dao.CommentDao;
import it.niedermann.nextcloud.deck.data.local.entity.CommentEntity;
import it.niedermann.nextcloud.deck.data.local.mapper.CommentMapper;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.DBStatus;
import it.niedermann.nextcloud.deck.domain.state.SyncStatus;
import it.niedermann.nextcloud.remote.ApiProvider;
import it.niedermann.nextcloud.remote.deck.dto.CommentDTO;
import it.niedermann.nextcloud.remote.deck.dto.CardDTO;
import it.niedermann.nextcloud.remote.deck.mapper.CommentRemoteMapper;
import jakarta.inject.Inject;

public class CommentSyncProvider implements SyncProvider<CardDTO> {

    private final CommentDao commentDao;

    @Inject
    public CommentSyncProvider(CommentDao commentDao, ApiProvider.Factory apiFactory) {
        this.commentDao = commentDao;
    }

    @Override
    public CompletableFuture<Void> upSync(Account account, SyncStatus status, Consumer<SyncStatus> reporter) {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> downSync(Account account, CardDTO parent, Long parentLocalId, SyncStatus status, Consumer<SyncStatus> reporter) {
        return CompletableFuture.completedFuture(null);
    }

    private CompletableFuture<Void> mergeComment(Account account, CommentDTO dto, Long cardId) {
        if (dto.getId() == null) return CompletableFuture.completedFuture(null);
        return commentDao.getCommentByRemoteId(account.id().value(), dto.getId())
                .handle((localComment, throwable) -> {
                    CommentEntity serverEntity = CommentMapper.INSTANCE.toEntity(CommentRemoteMapper.INSTANCE.toTO(dto));
                    if (throwable != null || localComment == null) {
                        CommentEntity newLocal = new CommentEntity(
                                0,
                                account.id().value(),
                                serverEntity.getRemoteId(),
                                DBStatus.UP_TO_DATE.getId(),
                                serverEntity.getLastModified(),
                                serverEntity.getLastModified(),
                                serverEntity.getEtag(),
                                cardId,
                                serverEntity.getActorType(),
                                serverEntity.getActorId(),
                                serverEntity.getActorDisplayName(),
                                serverEntity.getMessage(),
                                serverEntity.getParentId(),
                                serverEntity.getCreatedAt(),
                                null
                        );
                        return commentDao.upsert(newLocal).thenApply(v -> (Void) null);
                    } else {
                        if (localComment.getStatus() == DBStatus.CONFLICT.getId()) {
                            return CompletableFuture.<Void>completedFuture(null);
                        }
                        if (serverEntity.getEtag() != null && serverEntity.getEtag().equals(localComment.getEtag())) {
                            return CompletableFuture.<Void>completedFuture(null);
                        }
                        CommentEntity updatedLocal = new CommentEntity(
                                localComment.getLocalId(),
                                localComment.getAccountId(),
                                serverEntity.getRemoteId(),
                                DBStatus.UP_TO_DATE.getId(),
                                serverEntity.getLastModified(),
                                serverEntity.getLastModified(),
                                serverEntity.getEtag(),
                                cardId,
                                serverEntity.getActorType(),
                                serverEntity.getActorId(),
                                serverEntity.getActorDisplayName(),
                                serverEntity.getMessage(),
                                serverEntity.getParentId(),
                                serverEntity.getCreatedAt(),
                                null
                        );
                        return commentDao.updateRx(updatedLocal);
                    }
                }).thenCompose(f -> f);
    }
}
