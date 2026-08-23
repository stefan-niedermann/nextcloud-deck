package it.niedermann.nextcloud.deck.data.sync.provider;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.logging.Logger;

import it.niedermann.nextcloud.deck.data.local.dao.BoardDao;
import it.niedermann.nextcloud.deck.data.local.dao.CardDao;
import it.niedermann.nextcloud.deck.data.local.dao.ColumnDao;
import it.niedermann.nextcloud.deck.data.local.dao.JoinCardWithDependentCardDao;
import it.niedermann.nextcloud.deck.data.local.dao.JoinCardWithLabelDao;
import it.niedermann.nextcloud.deck.data.local.dao.JoinCardWithUserDao;
import it.niedermann.nextcloud.deck.data.local.dao.LabelDao;
import it.niedermann.nextcloud.deck.data.local.entity.CardEntity;
import it.niedermann.nextcloud.deck.data.local.entity.JoinCardWithDependentCardEntity;
import it.niedermann.nextcloud.deck.data.local.entity.JoinCardWithLabelEntity;
import it.niedermann.nextcloud.deck.data.local.entity.JoinCardWithUserEntity;
import it.niedermann.nextcloud.deck.data.local.mapper.CardMapper;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.DBStatus;
import it.niedermann.nextcloud.deck.domain.state.SyncStatus;
import it.niedermann.nextcloud.remote.ApiProvider;
import it.niedermann.nextcloud.remote.deck.DeckApi;
import it.niedermann.nextcloud.remote.deck.dto.CardDTO;
import it.niedermann.nextcloud.remote.deck.dto.ColumnDTO;
import it.niedermann.nextcloud.remote.deck.mapper.CardRemoteMapper;
import jakarta.inject.Inject;
import retrofit2.HttpException;

public class CardSyncProvider implements SyncProvider<ColumnDTO> {

    private static final Logger logger = Logger.getLogger(CardSyncProvider.class.getName());

    private final CardDao cardDao;
    private final ColumnDao columnDao;
    private final BoardDao boardDao;
    private final LabelDao labelDao;
    private final JoinCardWithLabelDao joinCardWithLabelDao;
    private final JoinCardWithUserDao joinCardWithUserDao;
    private final JoinCardWithDependentCardDao joinCardWithDependentCardDao;
    private final UserSyncHelper userSyncHelper;
    private final ApiProvider.Factory apiFactory;
    private final Map<String, CompletableFuture<Long>> inFlightCardSyncs = new ConcurrentHashMap<>();
    private AttachmentSyncProvider attachmentSyncProvider;
    private CommentSyncProvider commentSyncProvider;

    @Inject
    public CardSyncProvider(CardDao cardDao, ColumnDao columnDao, BoardDao boardDao, LabelDao labelDao, JoinCardWithLabelDao joinCardWithLabelDao, JoinCardWithUserDao joinCardWithUserDao, JoinCardWithDependentCardDao joinCardWithDependentCardDao, UserSyncHelper userSyncHelper, ApiProvider.Factory apiFactory) {
        this.cardDao = cardDao;
        this.columnDao = columnDao;
        this.boardDao = boardDao;
        this.labelDao = labelDao;
        this.joinCardWithLabelDao = joinCardWithLabelDao;
        this.joinCardWithUserDao = joinCardWithUserDao;
        this.joinCardWithDependentCardDao = joinCardWithDependentCardDao;
        this.userSyncHelper = userSyncHelper;
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
        return upSyncDependents(account)
                .thenCompose(v -> cardDao.getChangedCards(account.id().value()))
                .thenCompose(changedCards -> {
                    CompletableFuture<Void> future = CompletableFuture.completedFuture(null);
                    for (int i = 0; i < changedCards.size(); i++) {
                        CardEntity localCard = changedCards.get(i);
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
                            } else if (localCard.getStatus() == DBStatus.LOCAL_EDITED.getId()) {
                                it.niedermann.nextcloud.remote.deck.dto.CardUpdateDTO updateDto = CardRemoteMapper.INSTANCE.toUpdateDTO(CardMapper.INSTANCE.toTO(localCard));
                                call = api.updateCard(remoteBoardId, remoteColumnId, localCard.getRemoteId(), updateDto);
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
                                        updatedLocal.getColor(),
                                        updatedLocal.getStartDate(),
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
                                                serverCard.getColor(),
                                                serverCard.getStartDate(),
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
                                                    localCard.getColor(),
                                                    localCard.getStartDate(),
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
        if (parent == null || parent.getBoardId() == null || parent.getId() == null) return CompletableFuture.completedFuture(null);
        if (parent.getCards() != null && !parent.getCards().isEmpty()) {
            boolean allCardsHaveLabels = true;
            for (CardDTO card : parent.getCards()) {
                if (card.getLabels() == null) {
                    allCardsHaveLabels = false;
                    break;
                }
            }
            if (allCardsHaveLabels) {
                return syncCards(account, parent.getBoardId(), parent.getId(), parent.getCards(), parentLocalId, status, reporter);
            }
        }
        DeckApi api = apiFactory.create(account).getDeckApi();
        return api.getStack(parent.getBoardId(), parent.getId(), null)
                .thenCompose(stack -> {
                    if (stack == null || stack.getCards() == null) return CompletableFuture.completedFuture(null);
                    return syncCards(account, parent.getBoardId(), parent.getId(), stack.getCards(), parentLocalId, status, reporter);
                });
    }

    private CompletableFuture<Void> syncCards(Account account, long boardId, long stackId, List<CardDTO> cards, Long parentLocalId, SyncStatus status, Consumer<SyncStatus> reporter) {
        if (cards == null || cards.isEmpty()) return CompletableFuture.completedFuture(null);
        long total = cards.size();
        CompletableFuture<?>[] cardFutures = new CompletableFuture[cards.size()];
        for (int i = 0; i < cards.size(); i++) {
            CardDTO dto = cards.get(i);
            if (dto == null || dto.getId() == null) {
                cardFutures[i] = CompletableFuture.completedFuture(null);
                continue;
            }
            final long finished = i + 1;

            final String key = account.id().value() + ":" + dto.getId();
            cardFutures[i] = inFlightCardSyncs.compute(key, (k, existingFuture) -> {
                if (existingFuture != null && !existingFuture.isCompletedExceptionally()) {
                    return existingFuture;
                }
                // Fetch always for now, as cards in stack response seem to have null labels
                logger.info("Fetching full details for card: " + dto.getId());
                DeckApi api = apiFactory.create(account).getDeckApi();
                final var future = api.getCard_1_1(boardId, stackId, dto.getId(), null)
                        .thenCompose(fullDto -> {
                            if (fullDto == null) return CompletableFuture.completedFuture(null);
                            return mergeCard(account, fullDto, parentLocalId)
                                    .thenCompose(localCardId -> {
                                        SyncStatus newStatus = status.withCards(total, finished);
                                        reporter.accept(newStatus);
                                        return CompletableFuture.allOf(
                                                syncLabels(account, fullDto, localCardId),
                                                syncAssignedUsers(account, fullDto, localCardId),
                                                syncDependents(account, fullDto, localCardId),
                                                attachmentSyncProvider.downSync(account, fullDto, localCardId, newStatus, reporter),
                                                commentSyncProvider.downSync(account, fullDto, localCardId, newStatus, reporter)
                                        ).thenApply(v -> localCardId);
                                    });
                        });
                future.whenComplete((v, t) -> inFlightCardSyncs.remove(key));
                return future;
            }).thenApply(v -> null).exceptionally(throwable -> {
                logger.log(java.util.logging.Level.SEVERE, "Failed to sync card " + dto.getId(), throwable);
                return null;
            });
        }
        return CompletableFuture.allOf(cardFutures);
    }

    private CompletableFuture<Void> syncLabels(Account account, CardDTO cardDto, Long localCardId) {
        if (cardDto.getLabels() == null) {
            logger.warning("Labels are null for card " + cardDto.getId());
            return CompletableFuture.completedFuture(null);
        }
        logger.info("Syncing " + cardDto.getLabels().size() + " labels for card " + cardDto.getId());
        return joinCardWithLabelDao.deleteByCardId(localCardId)
                .thenCompose(v -> {
                    CompletableFuture<?>[] futures = new CompletableFuture[cardDto.getLabels().size()];
                    for (int i = 0; i < cardDto.getLabels().size(); i++) {
                        var labelDto = cardDto.getLabels().get(i);
                        futures[i] = labelDao.getLabelByRemoteId(account.id().value(), labelDto.getId())
                                .thenCompose(localLabel -> {
                                    if (localLabel == null) {
                                        logger.warning("Label " + labelDto.getId() + " not found locally for board!");
                                        return CompletableFuture.completedFuture(null);
                                    }
                                    return joinCardWithLabelDao.upsert(new JoinCardWithLabelEntity(localCardId, localLabel.getLocalId(), DBStatus.UP_TO_DATE.getId()))
                                            .thenApply(v3 -> null);
                                });
                    }
                    return CompletableFuture.allOf(futures);
                });
    }

    private CompletableFuture<Void> syncAssignedUsers(Account account, CardDTO cardDto, Long localCardId) {
        if (cardDto.getAssignedUsers() == null) {
            logger.warning("Assigned users are null for card " + cardDto.getId());
            return CompletableFuture.completedFuture(null);
        }
        logger.info("Syncing " + cardDto.getAssignedUsers().size() + " assigned users for card " + cardDto.getId());
        return joinCardWithUserDao.deleteByCardId(localCardId)
                .thenCompose(v -> {
                    CompletableFuture<?>[] futures = new CompletableFuture[cardDto.getAssignedUsers().size()];
                    for (int i = 0; i < cardDto.getAssignedUsers().size(); i++) {
                        var aclDto = cardDto.getAssignedUsers().get(i);
                        if (aclDto.getParticipant() == null) {
                            futures[i] = CompletableFuture.completedFuture(null);
                        } else {
                            futures[i] = userSyncHelper.syncUser(account, aclDto.getParticipant())
                                    .thenCompose(localUser -> {
                                        if (localUser == null) return CompletableFuture.completedFuture(null);
                                        return joinCardWithUserDao.upsert(new JoinCardWithUserEntity(localCardId, localUser.getLocalId(), DBStatus.UP_TO_DATE.getId()))
                                                .thenApply(v3 -> null);
                                    });
                        }
                    }
                    return CompletableFuture.allOf(futures);
                });
    }

    private CompletableFuture<Void> syncDependents(Account account, CardDTO cardDto, Long localCardId) {
        if (cardDto.getDependentCards() == null) {
            return CompletableFuture.completedFuture(null);
        }
        return joinCardWithDependentCardDao.deleteByCardId(localCardId)
                .thenCompose(v -> {
                    CompletableFuture<?>[] futures = new CompletableFuture[cardDto.getDependentCards().size()];
                    for (int i = 0; i < cardDto.getDependentCards().size(); i++) {
                        var remoteId = cardDto.getDependentCards().get(i);
                        futures[i] = joinCardWithDependentCardDao.upsert(new JoinCardWithDependentCardEntity(localCardId, remoteId, DBStatus.UP_TO_DATE.getId()))
                                .thenApply(v3 -> null);
                    }
                    return CompletableFuture.allOf(futures);
                });
    }

    private CompletableFuture<Void> upSyncDependents(Account account) {
        return joinCardWithDependentCardDao.getChangedJoinsForAccount(account.id().value())
                .thenCompose(changedDependents -> {
                    if (changedDependents == null || changedDependents.isEmpty())
                        return CompletableFuture.completedFuture(null);
                    CompletableFuture<Void> future = CompletableFuture.completedFuture(null);
                    for (JoinCardWithDependentCardEntity changedDependentLocal : changedDependents) {
                        final var finalFuture = future;
                        future = finalFuture.thenCompose(v -> cardDao.getCardById(changedDependentLocal.getCardId())
                                .thenCompose(card -> {
                                    if (card == null || card.getRemoteId() == null)
                                        return CompletableFuture.completedFuture(null);
                                    return cardDao.getBoardRemoteIdByLocalId(card.getLocalId())
                                            .thenCompose(boardId -> cardDao.getStackRemoteIdByLocalId(card.getLocalId())
                                                    .thenCompose(stackId -> {
                                                        if (boardId == null || stackId == null)
                                                            return CompletableFuture.completedFuture(null);
                                                        DeckApi api = apiFactory.create(account).getDeckApi();
                                                        if (changedDependentLocal.getStatus() == DBStatus.LOCAL_DELETED.getId()) {
                                                            return api.unassignDependentFromCard(boardId, stackId, card.getRemoteId(), changedDependentLocal.getDependentRemoteId())
                                                                    .thenCompose(v2 -> joinCardWithDependentCardDao.deletePhysically(changedDependentLocal.getCardId(), changedDependentLocal.getDependentRemoteId()));
                                                        } else if (changedDependentLocal.getStatus() == DBStatus.LOCAL_EDITED.getId()) {
                                                            return api.assignDependentToCard(boardId, stackId, card.getRemoteId(), changedDependentLocal.getDependentRemoteId())
                                                                    .thenCompose(v2 -> joinCardWithDependentCardDao.setStatus(changedDependentLocal.getCardId(), changedDependentLocal.getDependentRemoteId(), DBStatus.UP_TO_DATE.getId()));
                                                        }
                                                        return CompletableFuture.completedFuture(null);
                                                    }));
                                }));
                    }
                    return future;
                });
    }

    private CompletableFuture<Long> mergeCard(Account account, CardDTO cardDto, Long columnId) {
        if (cardDto.getId() == null) return CompletableFuture.completedFuture(null);
        logger.info("Merging card: " + cardDto.getId());
        CompletableFuture<Long> userIdFuture = userSyncHelper.syncUser(account, cardDto.getOwner())
                .thenApply(user -> user != null ? user.getLocalId() : null);
        return userIdFuture.thenCompose(ownerLocalId -> cardDao.getCardByRemoteId(account.id().value(), cardDto.getId())
                .thenCompose(localCard -> {
                    final long existingLocalId = localCard != null ? localCard.getLocalId() : 0;
                    CardEntity serverCard = CardMapper.INSTANCE.toEntity(CardRemoteMapper.INSTANCE.toTO(cardDto));
                    if (localCard == null || serverCard.getEtag() == null || !serverCard.getEtag().equals(localCard.getEtag())) {
                        CardEntity newLocal = new CardEntity(
                                existingLocalId,
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
                                ownerLocalId,
                                serverCard.getOrder(),
                                serverCard.getArchived(),
                                serverCard.getColor(),
                                serverCard.getStartDate(),
                                serverCard.getDueDate(),
                                serverCard.getNotified(),
                                serverCard.getOverdue(),
                                serverCard.getCommentsUnread(),
                                null
                        );
                        return cardDao.upsert(newLocal).thenCompose(id -> {
                            if (id != -1) {
                                return CompletableFuture.completedFuture(id);
                            } else if (existingLocalId != 0) {
                                return CompletableFuture.completedFuture(existingLocalId);
                            } else {
                                return cardDao.getCardByRemoteId(account.id().value(), cardDto.getId())
                                        .thenApply(c -> c != null ? c.getLocalId() : null);
                            }
                        });
                    } else {
                        return CompletableFuture.completedFuture(localCard.getLocalId());
                    }
                }));
    }
}
