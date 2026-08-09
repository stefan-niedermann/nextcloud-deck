package it.niedermann.nextcloud.deck.data.sync.provider;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.logging.Logger;

import it.niedermann.nextcloud.deck.data.local.dao.CardDao;
import it.niedermann.nextcloud.deck.data.local.dao.ColumnDao;
import it.niedermann.nextcloud.deck.data.local.dao.BoardDao;
import it.niedermann.nextcloud.deck.data.local.entity.CardEntity;
import it.niedermann.nextcloud.deck.data.local.mapper.CardMapper;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.DBStatus;
import it.niedermann.nextcloud.deck.domain.state.SyncStatus;
import it.niedermann.nextcloud.remote.ApiProvider;
import it.niedermann.nextcloud.remote.deck.DeckApi;
import it.niedermann.nextcloud.remote.deck.dto.CardDTO;
import it.niedermann.nextcloud.remote.deck.mapper.CardRemoteMapper;
import it.niedermann.nextcloud.remote.deck.dto.ColumnDTO;
import jakarta.inject.Inject;
import retrofit2.HttpException;

public class CardSyncProvider implements SyncProvider<ColumnDTO> {

    private static final Logger logger = Logger.getLogger(CardSyncProvider.class.getName());

    private final CardDao cardDao;
    private final ColumnDao columnDao;
    private final BoardDao boardDao;
    private final ApiProvider.Factory apiFactory;
    private AttachmentSyncProvider attachmentSyncProvider;
    private CommentSyncProvider commentSyncProvider;

    @Inject
    public CardSyncProvider(CardDao cardDao, ColumnDao columnDao, BoardDao boardDao, ApiProvider.Factory apiFactory) {
        this.cardDao = cardDao;
        this.columnDao = columnDao;
        this.boardDao = boardDao;
        this.apiFactory = apiFactory;
    }

    @Inject
    public void setAttachmentSyncProvider(AttachmentSyncProvider attachmentSyncProvider) {
        this.attachmentSyncProvider = attachmentSyncProvider;
    }

    @Inject
    public void setCommentSyncProvider(CommentSyncProvider commentSyncProvider) {
        this.commentSyncProvider = commentSyncProvider;
    }

    @Override
    public CompletableFuture<Void> upSync(Account account, SyncStatus status, Consumer<SyncStatus> reporter) {
        return cardDao.getChangedCards(account.id().value())
                .thenCompose(changedCards -> {
                    if (changedCards == null) return CompletableFuture.completedFuture(null);
                    CompletableFuture<Void> future = CompletableFuture.completedFuture(null);
                    for (CardEntity localCard : changedCards) {
                        final var finalFuture = future;
                        future = finalFuture.thenCompose(v -> upSyncSingle(account, localCard));
                    }
                    return future;
                });
    }

    private CompletableFuture<Void> upSyncSingle(Account account, CardEntity localCard) {
        return columnDao.getColumnById(localCard.getColumnId())
                .thenCompose(column -> boardDao.getBoardById(column.getBoardId())
                        .thenCompose(board -> {
                            if (board == null || board.getRemoteId() == null || column == null || column.getRemoteId() == null) {
                                return CompletableFuture.completedFuture(null);
                            }
                            long remoteBoardId = board.getRemoteId();
                            long remoteColumnId = column.getRemoteId();
                            DeckApi api = apiFactory.create(account).getDeckApi();
                            CardDTO dto = CardRemoteMapper.INSTANCE.toDTO(CardMapper.INSTANCE.toTO(localCard));

                            CompletableFuture<CardDTO> call;
                            if (localCard.getRemoteId() == null) {
                                call = api.createCard(remoteBoardId, remoteColumnId, dto);
                            } else if (localCard.getStatus() == DBStatus.LOCAL_DELETED.getId()) {
                                return api.deleteCard(remoteBoardId, remoteColumnId, localCard.getRemoteId())
                                        .thenCompose(v -> cardDao.deleteRx(localCard))
                                        .thenApply(v -> null);
                            } else {
                                return CompletableFuture.completedFuture(null);
                            }

                            return call.thenCompose(response -> {
                                if (response == null) return CompletableFuture.completedFuture((Void) null);
                                CardEntity updatedLocal = CardMapper.INSTANCE.toEntity(CardRemoteMapper.INSTANCE.toTO(response));
                                updatedLocal = new CardEntity(
                                        localCard.getLocalId(),
                                        localCard.getAccountId(),
                                        updatedLocal.getRemoteId(),
                                        DBStatus.UP_TO_DATE.getId(),
                                        updatedLocal.getLastModified(),
                                        updatedLocal.getLastModified(),
                                        updatedLocal.getEtag(),
                                        updatedLocal.getTitle(),
                                        updatedLocal.getDescription(),
                                        updatedLocal.getColumnId(),
                                        updatedLocal.getType(),
                                        updatedLocal.getCreatedAt(),
                                        updatedLocal.getDeletedAt(),
                                        updatedLocal.getDone(),
                                        updatedLocal.getAttachmentCount(),
                                        updatedLocal.getUserId(),
                                        updatedLocal.getOrder(),
                                        updatedLocal.getArchived(),
                                        updatedLocal.getDueDate(),
                                        updatedLocal.getNotified(),
                                        updatedLocal.getOverdue(),
                                        updatedLocal.getCommentsUnread(),
                                        null
                                );

                                CompletableFuture<Void> cleanupFuture = CompletableFuture.completedFuture(null);
                                if (localCard.getStatus() == DBStatus.RESOLVED.getId() && localCard.getConflictWithId() != null) {
                                    cleanupFuture = cardDao.deleteById(localCard.getConflictWithId()).thenApply(v -> null);
                                }

                                CardEntity finalUpdatedLocal = updatedLocal;
                                return cleanupFuture.thenCompose(v -> cardDao.updateRx(finalUpdatedLocal));
                            }).handle((v, throwable) -> {
                                if (throwable != null) {
                                    Throwable cause = throwable.getCause();
                                    if (cause instanceof HttpException && ((HttpException) cause).code() == 412) {
                                        return handleConflict(account, localCard);
                                    }
                                    CompletableFuture<Void> failed = new CompletableFuture<>();
                                    failed.completeExceptionally(throwable);
                                    return failed;
                                }
                                return CompletableFuture.completedFuture((Void) null);
                            }).thenCompose(f -> f);
                        }));
    }

    private CompletableFuture<Void> handleConflict(Account account, CardEntity localCard) {
        return columnDao.getColumnById(localCard.getColumnId())
                .thenCompose(column -> boardDao.getBoardById(column.getBoardId())
                        .thenCompose(board -> {
                            if (board == null || board.getRemoteId() == null || column == null || column.getRemoteId() == null || localCard.getRemoteId() == null) {
                                return CompletableFuture.completedFuture(null);
                            }
                            DeckApi api = apiFactory.create(account).getDeckApi();
                            return api.getCard_1_1(board.getRemoteId(), column.getRemoteId(), localCard.getRemoteId(), null)
                                    .thenCompose(serverDto -> {
                                        if (serverDto == null) return CompletableFuture.completedFuture(null);
                                        CardEntity serverCard = CardMapper.INSTANCE.toEntity(CardRemoteMapper.INSTANCE.toTO(serverDto));
                                        serverCard = new CardEntity(
                                                0,
                                                -1L,
                                                serverCard.getRemoteId(),
                                                DBStatus.UP_TO_DATE.getId(),
                                                serverCard.getLastModified(),
                                                serverCard.getLastModified(),
                                                serverCard.getEtag(),
                                                serverCard.getTitle(),
                                                serverCard.getDescription(),
                                                serverCard.getColumnId(),
                                                serverCard.getType(),
                                                serverCard.getCreatedAt(),
                                                serverCard.getDeletedAt(),
                                                serverCard.getDone(),
                                                serverCard.getAttachmentCount(),
                                                serverCard.getUserId(),
                                                serverCard.getOrder(),
                                                serverCard.getArchived(),
                                                serverCard.getDueDate(),
                                                serverCard.getNotified(),
                                                serverCard.getOverdue(),
                                                serverCard.getCommentsUnread(),
                                                null
                                        );

                                        return cardDao.insert(serverCard)
                                                .thenCompose(serverLocalId -> {
                                                    CardEntity updatedLocal = new CardEntity(
                                                            localCard.getLocalId(),
                                                            localCard.getAccountId(),
                                                            localCard.getRemoteId(),
                                                            DBStatus.CONFLICT.getId(),
                                                            localCard.getLastModified(),
                                                            localCard.getLastModifiedLocal(),
                                                            localCard.getEtag(),
                                                            localCard.getTitle(),
                                                            localCard.getDescription(),
                                                            localCard.getColumnId(),
                                                            localCard.getType(),
                                                            localCard.getCreatedAt(),
                                                            localCard.getDeletedAt(),
                                                            localCard.getDone(),
                                                            localCard.getAttachmentCount(),
                                                            localCard.getUserId(),
                                                            localCard.getOrder(),
                                                            localCard.getArchived(),
                                                            localCard.getDueDate(),
                                                            localCard.getNotified(),
                                                            localCard.getOverdue(),
                                                            localCard.getCommentsUnread(),
                                                            serverLocalId
                                                    );
                                                    return cardDao.updateRx(updatedLocal);
                                                });
                                    });
                        }));
    }

    @Override
    public CompletableFuture<Void> downSync(Account account, ColumnDTO parent, Long parentLocalId, SyncStatus status, Consumer<SyncStatus> reporter) {
        if (parent == null) return CompletableFuture.completedFuture(null);
        if (parent.getCards() != null && !parent.getCards().isEmpty()) {
            CompletableFuture<Void> future = CompletableFuture.completedFuture(null);
            long total = parent.getCards().size();
            for (int i = 0; i < parent.getCards().size(); i++) {
                CardDTO dto = parent.getCards().get(i);
                if (dto == null) continue;
                final long finished = i + 1;
                final var finalFuture = future;
                future = finalFuture.thenCompose(v -> mergeCard(account, dto, parentLocalId))
                        .thenCompose(localCardId -> {
                            SyncStatus newStatus = status.withCards(total, finished);
                            reporter.accept(newStatus);
                            return CompletableFuture.allOf(
                                    attachmentSyncProvider.downSync(account, dto, localCardId, newStatus, reporter),
                                    commentSyncProvider.downSync(account, dto, localCardId, newStatus, reporter)
                            );
                        });
            }
            return future;
        }
        return CompletableFuture.completedFuture(null);
    }

    private CompletableFuture<Long> mergeCard(Account account, CardDTO cardDto, Long columnId) {
        if (cardDto.getId() == null) return CompletableFuture.completedFuture(null);
        return cardDao.getCardByRemoteId(account.id().value(), cardDto.getId())
                .handle((localCard, throwable) -> {
                    CardEntity serverCard = CardMapper.INSTANCE.toEntity(CardRemoteMapper.INSTANCE.toTO(cardDto));
                    if (throwable != null || localCard == null) {
                        CardEntity newLocal = new CardEntity(
                                0,
                                account.id().value(),
                                serverCard.getRemoteId(),
                                DBStatus.UP_TO_DATE.getId(),
                                serverCard.getLastModified(),
                                serverCard.getLastModified(),
                                serverCard.getEtag(),
                                serverCard.getTitle(),
                                serverCard.getDescription(),
                                columnId,
                                serverCard.getType(),
                                serverCard.getCreatedAt(),
                                serverCard.getDeletedAt(),
                                serverCard.getDone(),
                                serverCard.getAttachmentCount(),
                                serverCard.getUserId(),
                                serverCard.getOrder(),
                                serverCard.getArchived(),
                                serverCard.getDueDate(),
                                serverCard.getNotified(),
                                serverCard.getOverdue(),
                                serverCard.getCommentsUnread(),
                                null
                        );
                        return cardDao.insert(newLocal);
                    } else {
                        if (localCard.getStatus() == DBStatus.CONFLICT.getId()) {
                            return CompletableFuture.completedFuture(localCard.getLocalId());
                        }
                        if (serverCard.getEtag() != null && serverCard.getEtag().equals(localCard.getEtag())) {
                            return CompletableFuture.completedFuture(localCard.getLocalId());
                        }
                        CardEntity updatedLocal = new CardEntity(
                                localCard.getLocalId(),
                                localCard.getAccountId(),
                                serverCard.getRemoteId(),
                                DBStatus.UP_TO_DATE.getId(),
                                serverCard.getLastModified(),
                                serverCard.getLastModified(),
                                serverCard.getEtag(),
                                serverCard.getTitle(),
                                serverCard.getDescription(),
                                columnId,
                                serverCard.getType(),
                                serverCard.getCreatedAt(),
                                serverCard.getDeletedAt(),
                                serverCard.getDone(),
                                serverCard.getAttachmentCount(),
                                serverCard.getUserId(),
                                serverCard.getOrder(),
                                serverCard.getArchived(),
                                serverCard.getDueDate(),
                                serverCard.getNotified(),
                                serverCard.getOverdue(),
                                serverCard.getCommentsUnread(),
                                null
                        );
                        return cardDao.updateRx(updatedLocal).thenApply(v -> localCard.getLocalId());
                    }
                }).thenCompose(f -> f);
    }
}
