package it.niedermann.nextcloud.deck.data.sync.provider;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import it.niedermann.nextcloud.deck.data.local.dao.BoardDao;
import it.niedermann.nextcloud.deck.data.local.dao.CardDao;
import it.niedermann.nextcloud.deck.data.local.dao.ColumnDao;
import it.niedermann.nextcloud.deck.data.local.dao.JoinCardWithDependentCardDao;
import it.niedermann.nextcloud.deck.data.local.dao.JoinCardWithLabelDao;
import it.niedermann.nextcloud.deck.data.local.dao.JoinCardWithUserDao;
import it.niedermann.nextcloud.deck.data.local.dao.LabelDao;
import it.niedermann.nextcloud.deck.data.local.dao.UserDao;
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
    private final UserDao userDao;
    private final UserSyncHelper userSyncHelper;
    private final ApiProvider.Factory apiFactory;
    private final Map<String, CompletableFuture<Long>> inFlightCardSyncs = new ConcurrentHashMap<>();
    private AttachmentSyncProvider attachmentSyncProvider;
    private CommentSyncProvider commentSyncProvider;

    @Inject
    public CardSyncProvider(CardDao cardDao, ColumnDao columnDao, BoardDao boardDao, LabelDao labelDao, JoinCardWithLabelDao joinCardWithLabelDao, JoinCardWithUserDao joinCardWithUserDao, JoinCardWithDependentCardDao joinCardWithDependentCardDao, UserDao userDao, UserSyncHelper userSyncHelper, ApiProvider.Factory apiFactory) {
        this.cardDao = cardDao;
        this.columnDao = columnDao;
        this.boardDao = boardDao;
        this.labelDao = labelDao;
        this.joinCardWithLabelDao = joinCardWithLabelDao;
        this.joinCardWithUserDao = joinCardWithUserDao;
        this.joinCardWithDependentCardDao = joinCardWithDependentCardDao;
        this.userDao = userDao;
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
        return cardDao.getChangedCards(account.id().value())
                .thenCompose(changedCards -> {
                    CompletableFuture<Void> future = CompletableFuture.completedFuture(null);
                    for (int i = 0; i < changedCards.size(); i++) {
                        CardEntity localCard = changedCards.get(i);
                        final var finalFuture = future;
                        future = finalFuture.thenCompose(v -> upSyncSingle(account, localCard));
                    }
                    return future;
                })
                .thenCompose(v -> upSyncLabels(account))
                .thenCompose(v -> upSyncAssignedUsers(account))
                .thenCompose(v -> upSyncDependents(account));
    }

    private CompletableFuture<Void> upSyncSingle(Account account, CardEntity localCard) {
        return columnDao.getColumnById(localCard.getColumnId())
                .thenCompose(column -> boardDao.getBoardById(column.getBoardId())
                        .thenCompose(board -> {
                            if (board == null || board.getRemoteId() == null || column == null || column.getRemoteId() == null) {
                                return CompletableFuture.completedFuture(null);
                            }

                            CompletableFuture<String> ownerUidFuture = CompletableFuture.completedFuture(null);
                            if (localCard.getUserId() != null) {
                                ownerUidFuture = userDao.getUserByLocalId(localCard.getUserId())
                                        .thenApply(u -> u != null ? u.getRemoteId() : null);
                            }

                            return ownerUidFuture.thenCompose(ownerUid -> {
                                long remoteBoardId = board.getRemoteId();
                                long remoteColumnId = column.getRemoteId();
                                DeckApi api = apiFactory.create(account).getDeckApi();
                                CardDTO dto = CardRemoteMapper.INSTANCE.toDTO(CardMapper.INSTANCE.toTO(localCard));
                                dto.setStackId(remoteColumnId);

                                CompletableFuture<CardDTO> call;
                                if (localCard.getRemoteId() == null) {
                                    logger.info("Creating card \"" + localCard.getTitle() + "\" on remote board " + remoteBoardId + " and stack " + remoteColumnId);
                                    call = api.createCard(remoteBoardId, remoteColumnId, dto);
                                } else if (localCard.getStatus() == DBStatus.LOCAL_EDITED.getId()) {
                                    it.niedermann.nextcloud.remote.deck.dto.CardUpdateDTO updateDto = CardRemoteMapper.INSTANCE.toUpdateDTO(CardMapper.INSTANCE.toTO(localCard));
                                    updateDto.setStackId(remoteColumnId);
                                    if (ownerUid != null) {
                                        updateDto.setOwner(ownerUid);
                                    } else {
                                        updateDto.setOwner(account.username());
                                    }
                                    logger.info("Updating card " + localCard.getRemoteId() + " at board " + remoteBoardId + " and stack " + remoteColumnId + " with " + updateDto);
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
                                            localCard.getColumnId(),
                                            updatedLocal.getType(),
                                            updatedLocal.getCreatedAt(),
                                            updatedLocal.getDeletedAt(),
                                            updatedLocal.getDone(),
                                            updatedLocal.getAttachmentCount(),
                                            localCard.getUserId(),
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
                            });
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
        DeckApi api = apiFactory.create(account).getDeckApi();

        CompletableFuture<List<CardDTO>> activeCardsFuture;
        if (parent.getCards() != null && !parent.getCards().isEmpty()) {
            activeCardsFuture = CompletableFuture.completedFuture(parent.getCards());
        } else {
            activeCardsFuture = api.getStack(parent.getBoardId(), parent.getId(), null)
                    .thenApply(stack -> stack != null ? stack.getCards() : null);
        }

        return activeCardsFuture.thenCompose(activeCards -> {
            List<CardDTO> allCards = activeCards != null ? new ArrayList<>(activeCards) : new ArrayList<>();
            return api.getArchivedStacks(parent.getBoardId(), null)
                    .thenCompose(archivedStacks -> {
                        if (archivedStacks != null) {
                            for (ColumnDTO archivedStack : archivedStacks) {
                                if (parent.getId().equals(archivedStack.getId()) && archivedStack.getCards() != null) {
                                    allCards.addAll(archivedStack.getCards());
                                }
                            }
                        }
                        return syncCards(account, parent.getBoardId(), parent.getId(), allCards, parentLocalId, status, reporter, true);
                    });
        });
    }

    private CompletableFuture<Void> syncCards(Account account, long boardId, long stackId, List<CardDTO> cards, Long parentLocalId, SyncStatus status, Consumer<SyncStatus> reporter, boolean isFullResponse) {
        if (cards == null) return CompletableFuture.completedFuture(null);

        Set<Long> remoteIdsFromServer = cards.stream()
                .map(CardDTO::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        return cardDao.getCardsByColumnSync(parentLocalId)
                .thenCompose(localCards -> {
                    List<CompletableFuture<?>> deleteFutures = new ArrayList<>();
                    if (isFullResponse) {
                        for (CardEntity local : localCards) {
                            if (local.getRemoteId() != null && !remoteIdsFromServer.contains(local.getRemoteId())) {
                                if (local.getStatus() == DBStatus.UP_TO_DATE.getId()) {
                                    deleteFutures.add(cardDao.deleteById(local.getLocalId()));
                                }
                            }
                        }
                    }
                    return CompletableFuture.allOf(deleteFutures.toArray(new CompletableFuture[0]));
                })
                .thenCompose(v -> {
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

                            CompletableFuture<CardDTO> dtoFuture;
                            if (dto.getLabels() != null && dto.getAssignedUsers() != null) {
                                dtoFuture = CompletableFuture.completedFuture(dto);
                            } else {
                                DeckApi api = apiFactory.create(account).getDeckApi();
                                dtoFuture = api.getCard_1_1(boardId, stackId, dto.getId(), null);
                            }

                            final var future = dtoFuture
                                    .thenCompose(fullDto -> {
                                        if (fullDto == null) return CompletableFuture.completedFuture(null);
                                        return mergeCard(account, fullDto, parentLocalId)
                                                .thenCompose(localCardId -> {
                                                    SyncStatus newStatus = status.withCards(total, finished);
                                                    reporter.accept(newStatus);
                                                    logger.info("Card " + localCardId + " merged, starting sub-syncs");
                                                    return CompletableFuture.allOf(
                                                            syncLabels(account, fullDto, localCardId),
                                                            syncAssignedUsers(account, fullDto, localCardId),
                                                            syncDependents(account, fullDto, localCardId),
                                                            attachmentSyncProvider.downSync(account, fullDto, localCardId, newStatus, reporter),
                                                            commentSyncProvider.downSync(account, fullDto, localCardId, newStatus, reporter)
                                                    ).handle((v2, t2) -> {
                                                        if (t2 != null) {
                                                            logger.log(java.util.logging.Level.SEVERE, "Sub-sync failed for card " + localCardId, t2);
                                                        }
                                                        logger.info("Card " + localCardId + " sub-syncs finished");
                                                        return localCardId;
                                                    });
                                                });
                                    });
                            future.whenComplete((v2, t) -> inFlightCardSyncs.remove(key));
                            return future;
                        }).thenApply(v2 -> null).exceptionally(throwable -> {
                            logger.log(java.util.logging.Level.SEVERE, "Failed to sync card " + dto.getId(), throwable);
                            return null;
                        });
                    }
                    return CompletableFuture.allOf(cardFutures)
                            .thenCompose(v2 -> cardDao.getCardsByColumnRx(parentLocalId))
                            .thenCompose(localCards -> {
                                List<CompletableFuture<?>> deletionFutures = new ArrayList<>();
                                for (CardEntity localCard : localCards) {
                                    if (localCard.getRemoteId() != null && !remoteIdsFromServer.contains(localCard.getRemoteId()) && localCard.getStatus() != DBStatus.LOCAL_EDITED.getId()) {
                                        logger.info("Deleting local card because it was deleted on remote: " + localCard.getRemoteId());
                                        deletionFutures.add(cardDao.deleteById(localCard.getLocalId()));
                                    }
                                }
                                return CompletableFuture.allOf(deletionFutures.toArray(new CompletableFuture[0]));
                            });
                });
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

    private CompletableFuture<Void> upSyncLabels(Account account) {
        return joinCardWithLabelDao.getChangedJoinsForAccount(account.id().value())
                .thenCompose(changedLabels -> {
                    if (changedLabels == null || changedLabels.isEmpty())
                        return CompletableFuture.completedFuture(null);
                    logger.info("Found " + changedLabels.size() + " changed labels for account " + account.id().value());
                    CompletableFuture<Void> future = CompletableFuture.completedFuture(null);
                    for (JoinCardWithLabelEntity join : changedLabels) {
                        final var finalFuture = future;
                        future = finalFuture.thenCompose(v -> cardDao.getCardById(join.getCardId())
                                .thenCompose(card -> labelDao.getLabelById(join.getLabelId())
                                        .thenCompose(label -> {
                                            if (card == null || card.getRemoteId() == null || label == null || label.getRemoteId() == null) {
                                                logger.warning("Skipping label sync: card or label remote ID missing. Card: " + (card != null ? card.getRemoteId() : "null") + ", Label: " + (label != null ? label.getRemoteId() : "null"));
                                                return CompletableFuture.completedFuture(null);
                                            }
                                            return cardDao.getBoardRemoteIdByLocalId(card.getLocalId())
                                                    .thenCompose(boardId -> cardDao.getStackRemoteIdByLocalId(card.getLocalId())
                                                            .thenCompose(stackId -> {
                                                                if (boardId == null || stackId == null) {
                                                                    logger.warning("Skipping label sync: board or stack remote ID missing. Board: " + boardId + ", Stack: " + stackId);
                                                                    return CompletableFuture.completedFuture(null);
                                                                }
                                                                DeckApi api = apiFactory.create(account).getDeckApi();
                                                                if (join.getStatus() == DBStatus.LOCAL_DELETED.getId()) {
                                                                    logger.info("Unassigning label " + label.getRemoteId() + " from card " + card.getRemoteId());
                                                                    return api.unassignLabelFromCard(boardId, stackId, card.getRemoteId(), label.getRemoteId())
                                                                            .thenCompose(v2 -> joinCardWithLabelDao.deleteByCardIdAndLabelId(join.getCardId(), join.getLabelId()));
                                                                } else if (join.getStatus() == DBStatus.LOCAL_EDITED.getId()) {
                                                                    logger.info("Assigning label " + label.getRemoteId() + " to card " + card.getRemoteId());
                                                                    return api.assignLabelToCard(boardId, stackId, card.getRemoteId(), label.getRemoteId())
                                                                            .thenCompose(v2 -> {
                                                                                JoinCardWithLabelEntity updated = new JoinCardWithLabelEntity(join.getCardId(), join.getLabelId(), DBStatus.UP_TO_DATE.getId());
                                                                                return joinCardWithLabelDao.upsert(updated).thenApply(id -> null);
                                                                            });
                                                                }
                                                                return CompletableFuture.completedFuture(null);
                                                            }));
                                        })));
                    }
                    return future;
                });
    }

    private CompletableFuture<Void> upSyncAssignedUsers(Account account) {
        return joinCardWithUserDao.getChangedJoinsForAccount(account.id().value())
                .thenCompose(changedUsers -> {
                    if (changedUsers == null || changedUsers.isEmpty())
                        return CompletableFuture.completedFuture(null);
                    logger.info("Found " + changedUsers.size() + " changed users for account " + account.id().value());
                    CompletableFuture<Void> future = CompletableFuture.completedFuture(null);
                    for (JoinCardWithUserEntity join : changedUsers) {
                        final var finalFuture = future;
                        future = finalFuture.thenCompose(v -> cardDao.getCardById(join.getCardId())
                                .thenCompose(card -> userDao.getUserByLocalId(join.getUserId())
                                        .thenCompose(user -> {
                                            if (card == null || card.getRemoteId() == null || user == null || user.getRemoteId() == null) {
                                                logger.warning("Skipping user sync: card or user remote ID missing. Card: " + (card != null ? card.getRemoteId() : "null") + ", User: " + (user != null ? user.getRemoteId() : "null"));
                                                return CompletableFuture.completedFuture(null);
                                            }
                                            return cardDao.getBoardRemoteIdByLocalId(card.getLocalId())
                                                    .thenCompose(boardId -> cardDao.getStackRemoteIdByLocalId(card.getLocalId())
                                                            .thenCompose(stackId -> {
                                                                if (boardId == null || stackId == null) {
                                                                    logger.warning("Skipping user sync: board or stack remote ID missing. Board: " + boardId + ", Stack: " + stackId);
                                                                    return CompletableFuture.completedFuture(null);
                                                                }
                                                                DeckApi api = apiFactory.create(account).getDeckApi();
                                                                it.niedermann.nextcloud.remote.deck.dto.UserForAssignmentDTO assignment = new it.niedermann.nextcloud.remote.deck.dto.UserForAssignmentDTO().userId(user.getRemoteId());
                                                                if (join.getStatus() == DBStatus.LOCAL_DELETED.getId()) {
                                                                    logger.info("Unassigning user " + user.getRemoteId() + " from card " + card.getRemoteId());
                                                                    return api.unassignUserFromCard(boardId, stackId, card.getRemoteId(), assignment)
                                                                            .thenCompose(v2 -> joinCardWithUserDao.deleteByCardIdAndUserId(join.getCardId(), join.getUserId()));
                                                                } else if (join.getStatus() == DBStatus.LOCAL_EDITED.getId()) {
                                                                    logger.info("Assigning user " + user.getRemoteId() + " to card " + card.getRemoteId());
                                                                    return api.assignUserToCard(boardId, stackId, card.getRemoteId(), assignment)
                                                                            .thenCompose(v2 -> {
                                                                                JoinCardWithUserEntity updated = new JoinCardWithUserEntity(join.getCardId(), join.getUserId(), DBStatus.UP_TO_DATE.getId());
                                                                                return joinCardWithUserDao.upsert(updated).thenApply(id -> null);
                                                                            });
                                                                }
                                                                return CompletableFuture.completedFuture(null);
                                                            }));
                                        })));
                    }
                    return future;
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
        CompletableFuture<Long> userIdFuture = userSyncHelper.syncUser(account, cardDto.getOwner())
                .thenApply(user -> user != null ? user.getLocalId() : null);
        return userIdFuture.thenCompose(ownerLocalId -> cardDao.getCardByRemoteId(account.id().value(), cardDto.getId())
                .thenCompose(localCard -> {
                    final long existingLocalId = localCard != null ? localCard.getLocalId() : 0;
                    CardEntity serverCard = CardMapper.INSTANCE.toEntity(CardRemoteMapper.INSTANCE.toTO(cardDto));
                    if (localCard == null || serverCard.getEtag() == null || !serverCard.getEtag().equals(localCard.getEtag())) {
                        logger.info("Merging card " + cardDto.getId() + " (existingLocalId: " + existingLocalId + ")");
                        CardEntity newLocal = new CardEntity(
                                existingLocalId,
                                account.id().value(),
                                serverCard.getRemoteId(),
                                DBStatus.UP_TO_DATE.getId(),
                                serverCard.getLastModified(),
                                serverCard.getLastModified() != null ? serverCard.getLastModified() : OffsetDateTime.now(),
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
