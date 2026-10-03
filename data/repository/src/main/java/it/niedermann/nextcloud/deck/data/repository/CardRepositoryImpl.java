package it.niedermann.nextcloud.deck.data.repository;

import org.reactivestreams.FlowAdapters;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import it.niedermann.nextcloud.deck.data.local.dao.AccountDao;
import it.niedermann.nextcloud.deck.data.local.dao.CardDao;
import it.niedermann.nextcloud.deck.data.local.dao.ColumnDao;
import it.niedermann.nextcloud.deck.data.local.dao.JoinCardWithDependentCardDao;
import it.niedermann.nextcloud.deck.data.local.dao.JoinCardWithLabelDao;
import it.niedermann.nextcloud.deck.data.local.dao.JoinCardWithUserDao;
import it.niedermann.nextcloud.deck.data.local.dao.UserDao;
import it.niedermann.nextcloud.deck.data.local.entity.CardEntity;
import it.niedermann.nextcloud.deck.data.local.entity.CardPreviewLocal;
import it.niedermann.nextcloud.deck.data.local.entity.JoinCardWithDependentCardEntity;
import it.niedermann.nextcloud.deck.data.local.entity.JoinCardWithLabelEntity;
import it.niedermann.nextcloud.deck.data.local.entity.JoinCardWithUserEntity;
import it.niedermann.nextcloud.deck.data.local.mapper.CardMapper;
import it.niedermann.nextcloud.deck.data.local.mapper.ColumnMapper;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.model.Column;
import it.niedermann.nextcloud.deck.domain.model.CreateCard;
import it.niedermann.nextcloud.deck.domain.model.DBStatus;
import it.niedermann.nextcloud.deck.domain.model.FilterInformation;
import it.niedermann.nextcloud.deck.domain.model.Label;
import it.niedermann.nextcloud.deck.domain.model.User;
import it.niedermann.nextcloud.deck.domain.model.query.PreviewCard;
import it.niedermann.nextcloud.deck.domain.repository.CardRepository;
import jakarta.inject.Inject;

public class CardRepositoryImpl implements CardRepository {

    private final CardDao cardDao;
    private final ColumnDao columnDao;
    private final CardMapper cardMapper;
    private final ColumnMapper columnMapper;
    private final JoinCardWithLabelDao joinCardWithLabelDao;
    private final JoinCardWithUserDao joinCardWithUserDao;
    private final JoinCardWithDependentCardDao joinCardWithDependentCardDao;
    private final UserDao userDao;
    private final AccountDao accountDao;

    @Inject
    public CardRepositoryImpl(CardDao cardDao,
                              ColumnDao columnDao,
                              CardMapper cardMapper,
                              ColumnMapper columnMapper,
                              JoinCardWithLabelDao joinCardWithLabelDao,
                              JoinCardWithUserDao joinCardWithUserDao,
                              JoinCardWithDependentCardDao joinCardWithDependentCardDao,
                              UserDao userDao,
                              AccountDao accountDao) {
        this.cardDao = cardDao;
        this.columnDao = columnDao;
        this.cardMapper = cardMapper;
        this.columnMapper = columnMapper;
        this.joinCardWithLabelDao = joinCardWithLabelDao;
        this.joinCardWithUserDao = joinCardWithUserDao;
        this.joinCardWithDependentCardDao = joinCardWithDependentCardDao;
        this.userDao = userDao;
        this.accountDao = accountDao;
    }

    @Override
    public CompletableFuture<Void> createCard(CreateCard card) {
        return columnDao.getColumnById(card.columnId().value())
                .thenCompose(column -> {
                    if (column == null) {
                        final var future = new CompletableFuture<Void>();
                        future.completeExceptionally(new IllegalArgumentException("Column not found: " + card.columnId().value()));
                        return future;
                    }
                    final var entity = new CardEntity(
                            0,
                            column.getAccountId(),
                            null,
                            DBStatus.LOCAL_EDITED.getId(),
                            null,
                            OffsetDateTime.now(),
                            null,
                            card.title(),
                            "",
                            card.columnId().value(),
                            "plain",
                            OffsetDateTime.now(),
                            null,
                            null,
                            0,
                            null,
                            0,
                            false,
                            null,
                            null,
                            null,
                            false,
                            0,
                            0,
                            null
                    );
                    return cardDao.insertOrReplace(entity).thenApply(id -> null);
                });
    }

    @Override
    public CompletableFuture<Void> updateCard(Card card) {
        return cardDao.getCardById(card.id().value())
                .thenCompose(oldEntity -> {
                    if (oldEntity == null) {
                        final var future = new CompletableFuture<Void>();
                        future.completeExceptionally(new IllegalArgumentException("Card not found: " + card.id().value()));
                        return future;
                    }
                    CompletableFuture<Long> userIdFuture = CompletableFuture.completedFuture(oldEntity.getUserId());
                    if (card.ownerId() != null) {
                        userIdFuture = CompletableFuture.completedFuture(card.ownerId().value());
                    }
                    return userIdFuture.thenCompose(userId -> {
                        final var entity = cardMapper.toEntity(card);
                        final var updatedEntity = new CardEntity(
                                entity.getLocalId(),
                                entity.getAccountId() != 0 ? entity.getAccountId() : oldEntity.getAccountId(),
                                entity.getRemoteId() != null ? entity.getRemoteId() : oldEntity.getRemoteId(),
                                DBStatus.LOCAL_EDITED.getId(),
                                entity.getLastModified(),
                                OffsetDateTime.now(),
                                (entity.getEtag() != null && !entity.getEtag().isBlank()) ? entity.getEtag() : oldEntity.getEtag(),
                                entity.getTitle(),
                                entity.getDescription(),
                                entity.getColumnId(),
                                card.type() != null ? card.type() : (oldEntity.getType() != null ? oldEntity.getType() : "plain"),
                                entity.getCreatedAt(),
                                entity.getDeletedAt(),
                                entity.getDone(),
                                entity.getAttachmentCount(),
                                userId,
                                entity.getOrder(),
                                entity.getArchived(),
                                entity.getColor(),
                                entity.getStartDate(),
                                entity.getDueDate(),
                                entity.getNotified(),
                                entity.getOverdue(),
                                entity.getCommentsUnread(),
                                entity.getConflictWithId()
                        );
                        return cardDao.updateRx(updatedEntity);
                    });
                })
                .thenCompose(v -> joinCardWithLabelDao.getJoinsByCardId(card.id().value()))
                .thenCompose(existingJoins -> {
                    final var newLabelIds = card.labels().stream()
                            .map(Label.ID::value)
                            .collect(Collectors.toSet());

                    final List<CompletableFuture<?>> futures = new ArrayList<>();

                    for (final var join : existingJoins) {
                        if (!newLabelIds.contains(join.getLabelId())) {
                            if (join.getStatus() == DBStatus.UP_TO_DATE.getId()) {
                                futures.add(joinCardWithLabelDao.upsert(new JoinCardWithLabelEntity(join.getCardId(), join.getLabelId(), DBStatus.LOCAL_DELETED.getId())));
                            } else {
                                futures.add(joinCardWithLabelDao.deletePhysically(join.getCardId(), join.getLabelId()));
                            }
                        } else {
                            if (join.getStatus() == DBStatus.LOCAL_DELETED.getId()) {
                                futures.add(joinCardWithLabelDao.upsert(new JoinCardWithLabelEntity(join.getCardId(), join.getLabelId(), DBStatus.LOCAL_EDITED.getId())));
                            }
                        }
                    }

                    final var existingLabelIds = existingJoins.stream()
                            .map(JoinCardWithLabelEntity::getLabelId)
                            .collect(Collectors.toSet());

                    for (final var labelId : card.labels()) {
                        if (!existingLabelIds.contains(labelId.value())) {
                            futures.add(joinCardWithLabelDao.upsert(new JoinCardWithLabelEntity(card.id().value(), labelId.value(), DBStatus.LOCAL_EDITED.getId())));
                        }
                    }

                    return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
                })
                .thenCompose(v -> joinCardWithUserDao.softDeleteByCardId(card.id().value()))
                .thenCompose(v -> cardDao.getCardById(card.id().value()))
                .thenCompose(cardEntity -> {
                    CompletableFuture<?>[] userFutures = card.assignees().stream()
                            .map(userId -> userDao.getUserByLocalId(userId.value())
                                    .thenCompose(user -> {
                                        if (user == null) return CompletableFuture.completedFuture(null);
                                        return joinCardWithUserDao.upsert(new JoinCardWithUserEntity(card.id().value(), user.getLocalId(), DBStatus.LOCAL_EDITED.getId()));
                                    }))
                            .toArray(CompletableFuture[]::new);
                    return CompletableFuture.allOf(userFutures);
                })
                .thenCompose(v -> joinCardWithDependentCardDao.getActiveJoinsByCardId(card.id().value()))
                .thenCompose(existingJoins -> {
                    final var newDependents = card.dependents().stream()
                            .filter(id -> !id.equals(card.id()))
                            .map(id -> cardDao.getCardById(id.value()))
                            .collect(Collectors.toList());

                    return CompletableFuture.allOf(newDependents.toArray(new CompletableFuture[0]))
                            .thenCompose(v -> {
                                final var newRemoteIds = newDependents.stream()
                                        .map(CompletableFuture::join)
                                        .filter(Objects::nonNull)
                                        .map(CardEntity::getRemoteId)
                                        .filter(Objects::nonNull)
                                        .collect(Collectors.toSet());

                                final List<CompletableFuture<?>> futures = new ArrayList<>();
                                for (final var join : existingJoins) {
                                    if (!newRemoteIds.contains(join.getDependentRemoteId())) {
                                        futures.add(joinCardWithDependentCardDao.upsert(new JoinCardWithDependentCardEntity(join.getCardId(), join.getDependentRemoteId(), DBStatus.LOCAL_DELETED.getId())));
                                    }
                                }

                                final var existingRemoteIds = existingJoins.stream()
                                        .map(JoinCardWithDependentCardEntity::getDependentRemoteId)
                                        .collect(Collectors.toSet());

                                for (final var remoteId : newRemoteIds) {
                                    if (!existingRemoteIds.contains(remoteId)) {
                                        futures.add(joinCardWithDependentCardDao.upsert(new JoinCardWithDependentCardEntity(card.id().value(), remoteId, DBStatus.LOCAL_EDITED.getId())));
                                    }
                                }
                                return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
                            });
                })
                .thenApply(v -> null);
    }

    @Override
    public CompletableFuture<Void> deleteCard(Card.ID cardId) {
        return cardDao.getCardById(cardId.value())
                .thenCompose(card -> {
                    if (card == null) {
                        return CompletableFuture.completedFuture(null);
                    }
                    if (card.getRemoteId() == null) {
                        return cardDao.deleteById(card.getLocalId()).thenApply(v -> null);
                    }
                    final var deletedCard = new CardEntity(
                            card.getLocalId(),
                            card.getAccountId(),
                            card.getRemoteId(),
                            DBStatus.LOCAL_DELETED.getId(),
                            card.getLastModified(),
                            OffsetDateTime.now(),
                            card.getEtag(),
                            card.getTitle(),
                            card.getDescription(),
                            card.getColumnId(),
                            card.getType(),
                            card.getCreatedAt(),
                            OffsetDateTime.now(),
                            card.getDone(),
                            card.getAttachmentCount(),
                            card.getUserId(),
                            card.getOrder(),
                            card.getArchived(),
                            card.getColor(),
                            card.getStartDate(),
                            card.getDueDate(),
                            card.getNotified(),
                            card.getOverdue(),
                            card.getCommentsUnread(),
                            card.getConflictWithId()
                    );
                    return cardDao.updateRx(deletedCard).thenApply(v -> null);
                });
    }

    @Override
    public Flow.Publisher<List<Card>> getNotDeletedCards(Column.ID columnId) {
        return FlowAdapters.toFlowPublisher(
                cardDao.getCardsByColumn(columnId.value())
                        .flatMapSingle(entities -> Flowable.fromIterable(entities)
                                .flatMapSingle(this::fullMap)
                                .toList())
                        .subscribeOn(Schedulers.io())
        );
    }

    private Single<Card> fullMap(CardEntity entity) {
        final Card card = cardMapper.toTO(entity);
        final User.ID ownerId = (entity.getUserId() != null && entity.getUserId() != 0L) ? new User.ID(entity.getUserId()) : null;

        return Single.fromCompletionStage(joinCardWithLabelDao.getActiveJoinsByCardId(entity.getLocalId()))
                .flatMap(labels -> {
                    final var labelIds = labels.stream().map(l -> new Label.ID(l.getLabelId())).collect(Collectors.toSet());
                    return Single.fromCompletionStage(joinCardWithUserDao.getActiveJoinsByCardId(entity.getLocalId()))
                            .flatMap(userJoins -> {
                                final var assignees = userJoins.stream().map(uj -> {
                                    return new User.ID(uj.getUserId());
                                }).collect(Collectors.toSet());
                                return Single.fromCompletionStage(joinCardWithDependentCardDao.getActiveJoinsByCardId(entity.getLocalId()))
                                        .map(dependentJoins -> {
                                            final var dependents = dependentJoins.stream()
                                                    .map(dj -> cardDao.getCardByRemoteId(entity.getAccountId(), dj.getDependentRemoteId()).join())
                                                    .filter(Objects::nonNull)
                                                    .map(c -> new Card.ID(c.getLocalId()))
                                                    .filter(id -> id.value() != entity.getLocalId())
                                                    .collect(Collectors.toList());
                                            return card.withLabels(labelIds).withAssignees(assignees).withOwnerId(ownerId).withDependents(dependents);
                                        });
                            });
                });
    }

    @Override
    public Flow.Publisher<List<PreviewCard>> getNotDeletedCardPreviews(Column.ID columnId) {
        return getNotDeletedCardPreviews(columnId, FilterInformation.EMPTY);
    }

    @Override
    public Flow.Publisher<List<PreviewCard>> getNotDeletedCardPreviews(Column.ID columnId, FilterInformation filter) {
        return FlowAdapters.toFlowPublisher(
                Single.fromCompletionStage(columnDao.getColumnById(columnId.value()))
                        .flatMapPublisher(column -> {
                            if (column == null) {
                                return Flowable.just(Collections.<PreviewCard>emptyList());
                            }
                            return accountDao.getAccountSingle(column.getAccountId())
                                    .toSingle()
                                    .flatMapPublisher(account -> {
                                        final User.ID currentUserId;
                                        final var userEntity = userDao.getUserByRemoteId(column.getAccountId(), account.username()).join();
                                        currentUserId = userEntity != null ? new User.ID(userEntity.getLocalId()) : new User.ID(0L);
                                        return cardDao.getCardPreviewsByColumn(columnId.value())
                                                .map(locals -> locals.stream()
                                                        .map(local -> toPreviewCard(local, currentUserId))
                                                        .filter(c -> applyFilter(c, filter, OffsetDateTime.now()))
                                                        .collect(Collectors.toList())
                                                );
                                    });
                        })
                        .subscribeOn(Schedulers.io())
        );
    }

    private boolean applyFilter(PreviewCard card, FilterInformation filter, OffsetDateTime now) {
        if (!filter.labelIds().isEmpty()) {
            boolean hasLabel = card.labels().stream().anyMatch(l -> filter.labelIds().contains(l.id()));
            if (!hasLabel) return false;
        }
        if (!filter.assigneeIds().isEmpty()) {
            boolean hasAssignee = card.assignees().stream().anyMatch(filter.assigneeIds()::contains);
            if (!hasAssignee) return false;
        }
        if (filter.doneState() == FilterInformation.DoneState.DONE && !card.isDone()) return false;
        if (filter.doneState() == FilterInformation.DoneState.NOT_DONE && card.isDone()) return false;

        if (filter.dueDateFilter() != FilterInformation.DueDateFilter.ALL) {
            final OffsetDateTime due = card.dueDate();
            switch (filter.dueDateFilter()) {
                case OVERDUE:
                    if (due == null || !due.isBefore(now) || card.isDone()) return false;
                    break;
                case TODAY:
                    if (due == null || !due.toLocalDate().isEqual(now.toLocalDate())) return false;
                    break;
                case NEXT_7_DAYS:
                    if (due == null || due.isBefore(now) || due.isAfter(now.plusDays(7))) return false;
                    break;
                case NEXT_30_DAYS:
                    if (due == null || due.isBefore(now) || due.isAfter(now.plusDays(30))) return false;
                    break;
                case NO_DUE_DATE:
                    if (due != null) return false;
                    break;
            }
        }
        return true;
    }

    private boolean applyFilter(Card card, FilterInformation filter, OffsetDateTime now) {
        if (!filter.labelIds().isEmpty() && card.labels().stream().noneMatch(filter.labelIds()::contains))
            return false;
        if (!filter.assigneeIds().isEmpty() && card.assignees().stream().noneMatch(filter.assigneeIds()::contains))
            return false;
        // TODO: Implement other filters properly based on record fields if needed
        return true;
    }

    private PreviewCard toPreviewCard(CardPreviewLocal local, User.ID currentUserId) {
        final CardEntity entity = local.getCard();
        final String excerpt = entity.getDescription() != null && entity.getDescription().length() > 300
                ? entity.getDescription().substring(0, 300)
                : entity.getDescription();

        final var labelPreviews = local.getLabels().stream()
                .filter(l -> {
                    try {
                        // Check if the junction join itself is marked as LOCAL_DELETED
                        final var join = joinCardWithLabelDao.getJoin(entity.getLocalId(), l.getLocalId()).join();
                        return join == null || join.getStatus() != DBStatus.LOCAL_DELETED.getId();
                    } catch (Exception e) {
                        return true;
                    }
                })
                .map(l -> new PreviewCard.LabelPreview(new Label.ID(l.getLocalId()), l.getTitle(), l.getColor()))
                .collect(Collectors.toSet());

        final var assigneeIds = local.getAssignees().stream()
                .map(u -> new User.ID(u.getLocalId()))
                .collect(Collectors.toSet());

        final String description = entity.getDescription() != null ? entity.getDescription() : "";
        int checkboxTotalCount = 0;
        int checkboxDoneCount = 0;
        final Matcher matcher = Pattern.compile("\\[([ xX])]").matcher(description);
        while (matcher.find()) {
            checkboxTotalCount++;
            if (!matcher.group(1).isBlank()) {
                checkboxDoneCount++;
            }
        }

        return new PreviewCard(
                new Card.ID(entity.getLocalId()),
                entity.getRemoteId() != null ? new Card.RemoteID(entity.getRemoteId()) : null,
                entity.getTitle(),
                excerpt != null ? excerpt : "",
                labelPreviews,
                assigneeIds,
                local.getCommentCount(),
                local.getAttachmentsCount(),
                assigneeIds.size(),
                assigneeIds.contains(currentUserId),
                entity.getDone() != null,
                checkboxDoneCount,
                checkboxTotalCount,
                entity.getStartDate(),
                entity.getDueDate(),
                entity.getColor()
        );
    }

    @Override
    public Flow.Publisher<Map<Column, List<Card>>> getNotDeletedCardsByColumn(Board.ID boardId) {
        return FlowAdapters.toFlowPublisher(
                cardDao.getCardsByBoard(boardId.value())
                        .flatMapSingle(entities -> Flowable.fromIterable(entities)
                                .flatMapSingle(this::fullMap)
                                .toList())
                        .map(cards -> cards.stream()
                                .collect(Collectors.groupingBy(card -> {
                                    final var columnEntity = columnDao.getColumnById(card.columnId().value()).join();
                                    return columnMapper.toTO(columnEntity);
                                })))
                        .subscribeOn(Schedulers.io())
        );
    }

    @Override
    public Flow.Publisher<Card> getCard(Card.ID cardId) {
        return FlowAdapters.toFlowPublisher(
                Maybe.fromCompletionStage(cardDao.getCardById(cardId.value()))
                        .flatMap(entity -> fullMap(entity).toMaybe())
                        .toFlowable()
                        .subscribeOn(Schedulers.io())
        );
    }

    @Override
    public CompletableFuture<Card.ID> findCardByRemoteId(Account.ID accountId, Card.RemoteID remoteId) {
        return cardDao.getCardByRemoteId(accountId.value(), remoteId.value())
                .thenApply(entity -> entity != null ? new Card.ID(entity.getLocalId()) : null);
    }

    @Override
    public Flow.Publisher<Boolean> cardExists(Card.ID cardId) {
        return FlowAdapters.toFlowPublisher(
                Maybe.fromCompletionStage(cardDao.getCardById(cardId.value()))
                        .map(Objects::nonNull)
                        .defaultIfEmpty(false)
                        .toFlowable()
                        .subscribeOn(Schedulers.io())
        );
    }

    @Override
    public Flow.Publisher<Collection<Card>> find(String userText, Card.ID excludeId) {
        return FlowAdapters.toFlowPublisher(
                cardDao.find(userText, excludeId.value())
                        .flatMapSingle(entities ->
                                Flowable.fromIterable(entities)
                                        .flatMapSingle(this::fullMap)
                                        .toList()
                                        .map(ArrayList::new)
                        )
                        .subscribeOn(Schedulers.io())
        );
    }

    @Override
    public Flow.Publisher<Collection<Card>> find(Board.ID boardId, String userText, Card.ID excludeId) {
        return FlowAdapters.toFlowPublisher(
                cardDao.find(boardId.value(), userText, excludeId.value())
                        .flatMapSingle(entities ->
                                Flowable.fromIterable(entities)
                                        .flatMapSingle(this::fullMap)
                                        .toList()
                                        .map(ArrayList::new)
                        )
                        .subscribeOn(Schedulers.io())
        );
    }

    @Override
    public CompletableFuture<Void> markAsDone(Card.ID cardId) {
        return cardDao.setDoneStateOfCard(cardId.value(), OffsetDateTime.now(), DBStatus.LOCAL_EDITED.getId());
    }

    @Override
    public CompletableFuture<Void> markAsUndone(Card.ID cardId) {
        return cardDao.setDoneStateOfCard(cardId.value(), null, DBStatus.LOCAL_EDITED.getId());
    }
}
