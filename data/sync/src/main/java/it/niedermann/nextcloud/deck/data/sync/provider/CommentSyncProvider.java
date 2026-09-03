package it.niedermann.nextcloud.deck.data.sync.provider;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import it.niedermann.nextcloud.deck.data.local.dao.CardDao;
import it.niedermann.nextcloud.deck.data.local.dao.CommentDao;
import it.niedermann.nextcloud.deck.data.local.entity.CommentEntity;
import it.niedermann.nextcloud.deck.data.local.mapper.CommentMapper;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.DBStatus;
import it.niedermann.nextcloud.deck.domain.state.SyncStatus;
import it.niedermann.nextcloud.remote.ApiProvider;
import it.niedermann.nextcloud.remote.deck.dto.CardDTO;
import it.niedermann.nextcloud.remote.deck.dto.CommentDTO;
import it.niedermann.nextcloud.remote.deck.mapper.CommentRemoteMapper;
import it.niedermann.nextcloud.remote.ocs.dto.OcsCommentResponseDTO;
import jakarta.inject.Inject;

public class CommentSyncProvider implements SyncProvider<CardDTO> {

    private static final Logger logger = Logger.getLogger(CommentSyncProvider.class.getName());

    private final CommentDao commentDao;
    private final CardDao cardDao;
    private final ApiProvider.Factory apiFactory;

    @Inject
    public CommentSyncProvider(CommentDao commentDao, CardDao cardDao, ApiProvider.Factory apiFactory) {
        this.commentDao = commentDao;
        this.cardDao = cardDao;
        this.apiFactory = apiFactory;
    }

    @Override
    public CompletableFuture<Void> upSync(Account account, SyncStatus status, Consumer<SyncStatus> reporter) {
        return commentDao.getChangedComments(account.id().value())
                .thenCompose(changedComments -> {
                    if (changedComments == null || changedComments.isEmpty())
                        return CompletableFuture.completedFuture(null);
                    logger.info("Found " + changedComments.size() + " changed comments for account " + account.id().value());
                    CompletableFuture<Void> future = CompletableFuture.completedFuture(null);
                    for (CommentEntity local : changedComments) {
                        final var finalFuture = future;
                        future = finalFuture.thenCompose(v -> upSyncSingle(account, local));
                    }
                    return future;
                });
    }

    private CompletableFuture<Void> upSyncSingle(Account account, CommentEntity local) {
        return cardDao.getCardById(local.getCardId())
                .thenCompose(card -> {
                    if (card == null || card.getRemoteId() == null) return CompletableFuture.completedFuture(null);
                    final var ocsApi = apiFactory.create(account).getOcsApi();
                    if (local.getStatus() == DBStatus.LOCAL_DELETED.getId()) {
                        if (local.getRemoteId() == null) {
                            return commentDao.deleteById(local.getLocalId());
                        }
                        return ocsApi.deleteCommentForCard(card.getRemoteId(), local.getRemoteId())
                                .thenCompose(v -> commentDao.deleteById(local.getLocalId()));
                    } else if (local.getStatus() == DBStatus.LOCAL_EDITED.getId()) {
                        CommentDTO dto = CommentRemoteMapper.INSTANCE.toDTO(CommentMapper.INSTANCE.toTO(local));
                        final CompletableFuture<OcsCommentResponseDTO> call;
                        if (local.getRemoteId() == null) {
                            call = ocsApi.createCommentForCard(card.getRemoteId(), dto);
                        } else {
                            call = ocsApi.updateCommentForCard(card.getRemoteId(), local.getRemoteId(), dto);
                        }
                        return call.thenCompose(response -> {
                            if (response == null || response.getOcs() == null || response.getOcs().getData() == null || response.getOcs().getData().isEmpty())
                                return CompletableFuture.completedFuture(null);
                            it.niedermann.nextcloud.remote.ocs.dto.CommentDTO serverDto = response.getOcs().getData().get(0);
                            CommentEntity updated = CommentMapper.INSTANCE.toEntity(CommentRemoteMapper.INSTANCE.toTOFromOcs(serverDto));
                            updated = new CommentEntity(
                                    local.getLocalId(),
                                    local.getAccountId(),
                                    updated.getRemoteId(),
                                    DBStatus.UP_TO_DATE.getId(),
                                    updated.getLastModified(),
                                    updated.getLastModified(),
                                    updated.getEtag(),
                                    local.getCardId(),
                                    updated.getActorType(),
                                    updated.getActorId(),
                                    updated.getActorDisplayName(),
                                    updated.getMessage(),
                                    updated.getParentRemoteId(),
                                    updated.getCreatedAt(),
                                    null
                            );
                            return commentDao.updateRx(updated);
                        });
                    }
                    return CompletableFuture.completedFuture(null);
                });
    }

    @Override
    public CompletableFuture<Void> downSync(Account account, CardDTO parent, Long parentLocalId, SyncStatus status, Consumer<SyncStatus> reporter) {
        if (parent == null || parent.getId() == null) return CompletableFuture.completedFuture(null);
        final var ocsApi = apiFactory.create(account).getOcsApi();
        return ocsApi.getCommentsForCard(parent.getId())
                .thenCompose(response -> {
                    if (response == null || response.getOcs() == null || response.getOcs().getData() == null)
                        return CompletableFuture.completedFuture(null);
                    
                    List<CompletableFuture<Void>> futures = response.getOcs().getData().stream()
                            .map(dto -> mergeComment(account, dto, parentLocalId))
                            .collect(Collectors.toList());
                    
                    return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                            .thenCompose(v -> commentDao.getCommentsByCardRx(parentLocalId))
                            .thenCompose(localComments -> {
                                Set<Long> remoteIds = response.getOcs().getData().stream()
                                        .map(it.niedermann.nextcloud.remote.ocs.dto.CommentDTO::getId)
                                        .filter(Objects::nonNull)
                                        .collect(Collectors.toSet());
                                List<CompletableFuture<?>> deletions = localComments.stream()
                                        .filter(c -> c.getRemoteId() != null && !remoteIds.contains(c.getRemoteId()) && c.getStatus() == DBStatus.UP_TO_DATE.getId())
                                        .map(c -> commentDao.deleteById(c.getLocalId()))
                                        .collect(Collectors.toList());
                                return CompletableFuture.allOf(deletions.toArray(new CompletableFuture[0]));
                            });
                });
    }

    private CompletableFuture<Void> mergeComment(Account account, it.niedermann.nextcloud.remote.ocs.dto.CommentDTO dto, Long cardId) {
        if (dto.getId() == null) return CompletableFuture.completedFuture(null);
        logger.info("Merging comment " + dto.getId() + " for card " + cardId);
        return commentDao.getCommentByRemoteId(account.id().value(), dto.getId())
                .handle((localComment, throwable) -> {
                    CommentEntity serverEntity = CommentMapper.INSTANCE.toEntity(CommentRemoteMapper.INSTANCE.toTOFromOcs(dto));
                    if (throwable != null || localComment == null) {
                        logger.info("Inserting new comment " + dto.getId());
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
                                serverEntity.getParentRemoteId(),
                                serverEntity.getCreatedAt(),
                                null
                        );
                        return commentDao.upsert(newLocal).thenApply(v -> (Void) null);
                    } else {
                        logger.info("Updating existing comment " + dto.getId());
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
                                serverEntity.getParentRemoteId(),
                                serverEntity.getCreatedAt(),
                                null
                        );
                        return commentDao.updateRx(updatedLocal);
                    }
                }).thenCompose(f -> f);
    }
}
