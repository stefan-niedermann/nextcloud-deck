package it.niedermann.nextcloud.deck.data.repository;

import org.reactivestreams.FlowAdapters;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import it.niedermann.nextcloud.deck.data.local.dao.AttachmentDao;
import it.niedermann.nextcloud.deck.data.local.dao.CardDao;
import it.niedermann.nextcloud.deck.data.local.dao.ColumnDao;
import it.niedermann.nextcloud.deck.data.local.dao.CommentDao;
import it.niedermann.nextcloud.deck.data.local.dao.LabelDao;
import it.niedermann.nextcloud.deck.data.local.dao.JoinCardWithLabelDao;
import it.niedermann.nextcloud.deck.data.local.dao.JoinCardWithUserDao;
import it.niedermann.nextcloud.deck.data.local.dao.UserDao;
import it.niedermann.nextcloud.deck.data.local.entity.CardEntity;
import it.niedermann.nextcloud.deck.data.local.entity.JoinCardWithLabelEntity;
import it.niedermann.nextcloud.deck.data.local.entity.JoinCardWithUserEntity;
import it.niedermann.nextcloud.deck.data.local.entity.LabelEntity;
import it.niedermann.nextcloud.deck.data.local.mapper.CardMapper;
import it.niedermann.nextcloud.deck.data.local.mapper.ColumnMapper;
import it.niedermann.nextcloud.deck.data.local.mapper.LabelMapper;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.model.Column;
import it.niedermann.nextcloud.deck.domain.model.CreateCard;
import it.niedermann.nextcloud.deck.domain.model.DBStatus;
import it.niedermann.nextcloud.deck.domain.model.FilterInformation;
import it.niedermann.nextcloud.deck.domain.model.User;
import it.niedermann.nextcloud.deck.domain.model.query.PreviewCard;
import it.niedermann.nextcloud.deck.domain.repository.CardRepository;
import jakarta.inject.Inject;

public class CardRepositoryImpl implements CardRepository {

    private final CardDao cardDao;
    private final ColumnDao columnDao;
    private final CardMapper cardMapper;
    private final ColumnMapper columnMapper;
    private final LabelDao labelDao;
    private final JoinCardWithLabelDao joinCardWithLabelDao;
    private final JoinCardWithUserDao joinCardWithUserDao;
    private final UserDao userDao;
    private final LabelMapper labelMapper;
    private final CommentDao commentDao;
    private final AttachmentDao attachmentDao;

    @Inject
    public CardRepositoryImpl(CardDao cardDao,
                              ColumnDao columnDao,
                              CardMapper cardMapper,
                              ColumnMapper columnMapper,
                              LabelDao labelDao,
                              JoinCardWithLabelDao joinCardWithLabelDao,
                              JoinCardWithUserDao joinCardWithUserDao,
                              UserDao userDao,
                              LabelMapper labelMapper,
                              CommentDao commentDao,
                              AttachmentDao attachmentDao) {
        this.cardDao = cardDao;
        this.columnDao = columnDao;
        this.cardMapper = cardMapper;
        this.columnMapper = columnMapper;
        this.labelDao = labelDao;
        this.joinCardWithLabelDao = joinCardWithLabelDao;
        this.joinCardWithUserDao = joinCardWithUserDao;
        this.userDao = userDao;
        this.labelMapper = labelMapper;
        this.commentDao = commentDao;
        this.attachmentDao = attachmentDao;
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
                            "text",
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
        final var entity = cardMapper.toEntity(card);
        return cardDao.getCardById(card.id().value())
                .thenCompose(oldEntity -> {
                    if (oldEntity == null) {
                        final var future = new CompletableFuture<Void>();
                        future.completeExceptionally(new IllegalArgumentException("Card not found: " + card.id().value()));
                        return future;
                    }
                    final var updatedEntity = new CardEntity(
                            entity.getLocalId(),
                            oldEntity.getAccountId(),
                            entity.getRemoteId(),
                            DBStatus.LOCAL_EDITED.getId(),
                            entity.getLastModified(),
                            entity.getLastModifiedLocal(),
                            entity.getEtag(),
                            entity.getTitle(),
                            entity.getDescription(),
                            entity.getColumnId(),
                            entity.getType(),
                            entity.getCreatedAt(),
                            entity.getDeletedAt(),
                            entity.getDone(),
                            entity.getAttachmentCount(),
                            entity.getUserId(),
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
                    return cardDao.updateRx(updatedEntity)
                            .thenCompose(v -> joinCardWithLabelDao.deleteByCardId(card.id().value()))
                            .thenCompose(v -> {
                                CompletableFuture<?>[] labelFutures = card.labels().stream()
                                        .map(labelId -> joinCardWithLabelDao.upsert(new JoinCardWithLabelEntity(card.id().value(), labelId.value(), DBStatus.LOCAL_EDITED.getId())))
                                        .toArray(CompletableFuture[]::new);
                                return CompletableFuture.allOf(labelFutures);
                            })
                            .thenCompose(v -> joinCardWithUserDao.deleteByCardId(card.id().value()))
                            .thenCompose(v -> {
                                CompletableFuture<?>[] userFutures = card.assignees().stream()
                                        .map(userId -> userDao.getUserByRemoteId(oldEntity.getAccountId(), userId.value())
                                                .thenCompose(user -> {
                                                    if (user == null) return CompletableFuture.completedFuture(null);
                                                    return joinCardWithUserDao.upsert(new JoinCardWithUserEntity(card.id().value(), user.getLocalId(), DBStatus.LOCAL_EDITED.getId()));
                                                }))
                                        .toArray(CompletableFuture[]::new);
                                return CompletableFuture.allOf(userFutures);
                            })
                            .thenApply(v -> null);
                });
    }

    @Override
    public CompletableFuture<Void> deleteCard(Card.ID cardId) {
        return cardDao.getCardById(cardId.value())
                .thenCompose(card -> {
                    if (card == null) {
                        return CompletableFuture.completedFuture(null);
                    }
                    if (card.getRemoteId() == null) {
                        return cardDao.deleteById(card.getLocalId());
                    }
                    final var deletedCard = new CardEntity(
                            card.getLocalId(),
                            card.getAccountId(),
                            card.getRemoteId(),
                            DBStatus.LOCAL_DELETED.getId(),
                            card.getLastModified(),
                            card.getLastModifiedLocal(),
                            card.getEtag(),
                            card.getTitle(),
                            card.getDescription(),
                            card.getColumnId(),
                            card.getType(),
                            card.getCreatedAt(),
                            card.getDeletedAt(),
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
                    return cardDao.updateRx(deletedCard);
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
        return labelDao.getLabelsByCard(entity.getLocalId()).firstOrError()
                .flatMap(labels -> {
                    final var labelIds = labels.stream().map(l -> new it.niedermann.nextcloud.deck.domain.model.Label.ID(l.getLocalId())).collect(Collectors.toSet());
                    return Single.fromCompletionStage(joinCardWithUserDao.getJoinsByCardId(entity.getLocalId()))
                            .map(userJoins -> {
                                final var assignees = userJoins.stream().map(uj -> {
                                    final var user = userDao.getUserByLocalId(uj.getUserId()).join();
                                    return new User.ID(user.getRemoteId());
                                }).collect(Collectors.toSet());
                                return card.withLabels(labelIds).withAssignees(assignees);
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
                cardDao.getCardsByColumn(columnId.value())
                        .flatMapSingle(entities -> Flowable.fromIterable(entities)
                                .flatMapSingle(entity -> {
                                    final Card card = cardMapper.toTO(entity);
                                    return Single.zip(
                                            labelDao.getLabelsByCard(entity.getLocalId()).firstOrError(),
                                            commentDao.getCommentsByCard(entity.getLocalId()).firstOrError(),
                                            attachmentDao.getAttachmentsByCard(entity.getLocalId()).firstOrError(),
                                            (labels, comments, attachments) -> toPreviewCard(card, labels, comments.size(), attachments.size())
                                    );
                                })
                                .toList())
                        .subscribeOn(Schedulers.io())
        );
    }

    private boolean applyFilter(Card card, FilterInformation filter, OffsetDateTime now) {
        if (!filter.labelIds().isEmpty() && card.labels().stream().noneMatch(filter.labelIds()::contains)) {
            return false;
        }
        if (!filter.assigneeIds().isEmpty() && card.assignees().stream().noneMatch(filter.assigneeIds()::contains)) {
            return false;
        }

        switch (filter.doneState()) {
            case DONE -> {
                if (card.done() == null) return false;
            }
            case NOT_DONE -> {
                if (card.done() != null) return false;
            }
        }

        switch (filter.dueDateFilter()) {
            case OVERDUE -> {
                if (card.dueDate() == null || !card.dueDate().isBefore(now)) return false;
            }
            case TODAY -> {
                if (card.dueDate() == null || !card.dueDate().toLocalDate().equals(now.toLocalDate())) return false;
            }
            case NEXT_7_DAYS -> {
                if (card.dueDate() == null || card.dueDate().isBefore(now) || !card.dueDate().isBefore(now.plusDays(7))) return false;
            }
            case NEXT_30_DAYS -> {
                if (card.dueDate() == null || card.dueDate().isBefore(now) || !card.dueDate().isBefore(now.plusDays(30))) return false;
            }
            case NO_DUE_DATE -> {
                if (card.dueDate() != null) return false;
            }
        }

        return true;
    }

    private PreviewCard toPreviewCard(Card card, List<LabelEntity> labels, int commentCount, int attachmentCount) {
        final String excerpt = card.description() != null && card.description().length() > 300
                ? card.description().substring(0, 300)
                : card.description();

        final var labelPreviews = labels.stream()
                .map(l -> new PreviewCard.LabelPreview(l.getTitle(), l.getColor()))
                .collect(Collectors.toSet());

        final String description = card.description() != null ? card.description() : "";
        int checkboxTotalCount = 0;
        int checkboxDoneCount = 0;
        final java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("\\[([ xX])]").matcher(description);
        while (matcher.find()) {
            checkboxTotalCount++;
            if (!matcher.group(1).isBlank()) {
                checkboxDoneCount++;
            }
        }

        return new PreviewCard(
                card.id(),
                card.remoteId(),
                card.title(),
                excerpt,
                labelPreviews,
                commentCount,
                attachmentCount,
                card.assignees().size(),
                card.assignees().contains(new User.ID("jdoe")), // TODO: Get current user
                checkboxDoneCount,
                checkboxTotalCount,
                card.startDate(),
                card.dueDate(),
                card.color()
        );
    }

    @Override
    public Flow.Publisher<Map<Column, List<Card>>> getNotDeletedCardsByColumn(Board.ID boardId) {
        return FlowAdapters.toFlowPublisher(
                cardDao.getCardsByBoard(boardId.value())
                        .map(entities -> entities.stream()
                                .map(cardMapper::toTO)
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
    public Flow.Publisher<Collection<Card>> find(String userText) {
        // TODO: Implement search in CardDao
        return null;
    }
}
