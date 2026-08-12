package it.niedermann.nextcloud.deck.data.repository;

import org.reactivestreams.FlowAdapters;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import it.niedermann.nextcloud.deck.data.local.dao.AttachmentDao;
import it.niedermann.nextcloud.deck.data.local.dao.CardDao;
import it.niedermann.nextcloud.deck.data.local.dao.CommentDao;
import it.niedermann.nextcloud.deck.data.local.dao.LabelDao;
import it.niedermann.nextcloud.deck.data.local.entity.LabelEntity;
import it.niedermann.nextcloud.deck.data.local.mapper.CardMapper;
import it.niedermann.nextcloud.deck.data.local.mapper.LabelMapper;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.model.Column;
import it.niedermann.nextcloud.deck.domain.model.CreateCard;
import it.niedermann.nextcloud.deck.domain.model.User;
import it.niedermann.nextcloud.deck.domain.model.query.PreviewCard;
import it.niedermann.nextcloud.deck.domain.repository.CardRepository;
import jakarta.inject.Inject;

public class CardRepositoryImpl implements CardRepository {

    private final CardDao cardDao;
    private final CardMapper cardMapper;
    private final LabelDao labelDao;
    private final LabelMapper labelMapper;
    private final CommentDao commentDao;
    private final AttachmentDao attachmentDao;

    @Inject
    public CardRepositoryImpl(CardDao cardDao,
                              CardMapper cardMapper,
                              LabelDao labelDao,
                              LabelMapper labelMapper,
                              CommentDao commentDao,
                              AttachmentDao attachmentDao) {
        this.cardDao = cardDao;
        this.cardMapper = cardMapper;
        this.labelDao = labelDao;
        this.labelMapper = labelMapper;
        this.commentDao = commentDao;
        this.attachmentDao = attachmentDao;
    }

    @Override
    public CompletableFuture<Void> createCard(CreateCard card) {
        // TODO: Local-first or Sync?
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> updateCard(Card card) {
        return cardDao.updateRx(cardMapper.toEntity(card));
    }

    @Override
    public CompletableFuture<Void> deleteCard(Card.ID cardId) {
        return cardDao.deleteById(cardId.value());
    }

    @Override
    public Flow.Publisher<List<Card>> getNotDeletedCards(Column.ID columnId) {
        return FlowAdapters.toFlowPublisher(
                cardDao.getCardsByColumn(columnId.value())
                        .map(cardMapper::toTOList)
                        .subscribeOn(Schedulers.io())
        );
    }

    @Override
    public Flow.Publisher<List<PreviewCard>> getNotDeletedCardPreviews(Column.ID columnId) {
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
                card.dueDate(),
                card.color()
        );
    }

    @Override
    public Flow.Publisher<Map<Column, List<Card>>> getNotDeletedCardsByColumn(Board.ID boardId) {
        // TODO: Implement properly
        return null;
    }

    @Override
    public Flow.Publisher<Card> getCard(Card.ID cardId) {
        return FlowAdapters.toFlowPublisher(
                Maybe.fromCompletionStage(cardDao.getCardById(cardId.value()))
                        .toFlowable()
                        .map(cardMapper::toTO)
                        .subscribeOn(Schedulers.io())
        );
    }

    @Override
    public Flow.Publisher<Collection<Card>> find(String userText) {
        // TODO: Implement search in CardDao
        return null;
    }
}
