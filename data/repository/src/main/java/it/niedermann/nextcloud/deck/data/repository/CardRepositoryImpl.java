package it.niedermann.nextcloud.deck.data.repository;

import org.reactivestreams.FlowAdapters;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.model.Column;
import it.niedermann.nextcloud.deck.domain.model.CreateCard;
import it.niedermann.nextcloud.deck.domain.model.User;
import it.niedermann.nextcloud.deck.domain.model.query.PreviewCard;
import it.niedermann.nextcloud.deck.domain.repository.CardRepository;
import jakarta.inject.Inject;

public class CardRepositoryImpl implements CardRepository {

    @Inject
    public CardRepositoryImpl(
    ) {
    }

    @Override
    public CompletableFuture<Void> createCard(CreateCard card) {
        // TODO Mock Implementation
        System.out.println("[Mock][" + CardRepositoryImpl.class.getSimpleName() + "/createCard]: " + card);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> updateCard(Card card) {
        // TODO Mock Implementation
        System.out.println("[Mock][" + CardRepositoryImpl.class.getSimpleName() + "/updateCard]: " + card);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> deleteCard(Card.ID cardId) {
        // TODO Mock Implementation
        System.out.println("[Mock][" + CardRepositoryImpl.class.getSimpleName() + "/deleteCard]: " + cardId);
        return CompletableFuture.completedFuture(null);
    }

    @SuppressWarnings("NewApi")
    @Override
    public Flow.Publisher<List<Card>> getNotDeletedCards(Column.ID columnId) {
        // TODO Mock Implementation
        return FlowAdapters.toFlowPublisher(
                Flowable.fromCallable(() -> MockData.MOCK_CARDS.stream().filter(card -> Objects.equals(card.columnId(), columnId)).toList())
                        .subscribeOn(Schedulers.io())
        );
    }

    @Override
    public Flow.Publisher<List<PreviewCard>> getNotDeletedCardPreviews(Column.ID columnId) {
        return FlowAdapters.toFlowPublisher(
                Flowable.fromCallable(() -> MockData.MOCK_CARDS.stream()
                        .filter(card -> Objects.equals(card.columnId(), columnId))
                        .map(this::toPreviewCard)
                        .collect(Collectors.toList()))
                        .subscribeOn(Schedulers.io())
        );
    }

    private PreviewCard toPreviewCard(Card card) {
        final var excerpt = card.description().length() > 300
                ? card.description().substring(0, 300)
                : card.description();

        final var labels = card.labels().stream()
                .map(labelId -> Arrays.stream(MockData.MOCK_LABELS)
                        .filter(l -> Objects.equals(l.id(), labelId))
                        .findFirst()
                        .map(l -> new PreviewCard.LabelPreview(l.title(), l.color()))
                        .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        final var commentCount = (int) Arrays.stream(MockData.MOCK_COMMENTS)
                .filter(comment -> Objects.equals(comment.cardId(), card.id()))
                .count();

        final var attachmentCount = (int) Arrays.stream(MockData.MOCK_ATTACHMENTS)
                .filter(attachment -> Objects.equals(attachment.cardId(), card.id()))
                .count();

        final var description = card.description();
        int checkboxTotalCount = 0;
        int checkboxDoneCount = 0;
        final var matcher = java.util.regex.Pattern.compile("\\[([ xX])]").matcher(description);
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
                labels,
                commentCount,
                attachmentCount,
                card.assignees().size(),
                card.assignees().contains(new User.ID("jdoe")),
                checkboxDoneCount,
                checkboxTotalCount,
                card.dueDate(),
                card.color()
        );
    }

    @Override
    public Flow.Publisher<Map<Column, List<Card>>> getNotDeletedCardsByColumn(Board.ID boardId) {
        // TODO Mock Implementation

        return FlowAdapters.toFlowPublisher(
                Flowable.fromCallable(() -> {
                    final var columns = Arrays.stream(MockData.MOCK_COLUMNS)
                            .filter(column -> Objects.equals(column.boardId(), boardId))
                            .map(Column::id)
                            .collect(Collectors.toList());

                    return MockData.MOCK_CARDS.stream().filter(card -> columns.contains(card.columnId()))
                            .collect(Collectors.groupingBy(card -> MockData.MOCK_COLUMNS[(int) card.columnId().value()]));
                }).subscribeOn(Schedulers.io())
        );
    }

    @Override
    public Flow.Publisher<Card> getCard(Card.ID cardId) {
        // TODO Mock Implementation
        return FlowAdapters.toFlowPublisher(
                Flowable.fromCallable(() -> {
                    if (cardId.value() < MockData.MOCK_CARDS.size()) {
                        return MockData.MOCK_CARDS.get((int) cardId.value());
                    }
                    throw new NoSuchElementException("No card with id " + cardId);
                }).subscribeOn(Schedulers.io())
        );
    }

    @Override
    public Flow.Publisher<Boolean> cardExists(Card.ID cardId) {
        // TODO Mock Implementation
        return FlowAdapters.toFlowPublisher(
                Flowable.fromCallable(() -> MockData.MOCK_CARDS.stream().anyMatch(card -> Objects.equals(card.id(), cardId)))
                        .subscribeOn(Schedulers.io())
        );
    }

    @Override
    public Flow.Publisher<Collection<Card>> find(String userText) {
        // TODO Mock Implementation
        return FlowAdapters.toFlowPublisher(
                Flowable.fromCallable(() -> MockData.MOCK_CARDS.stream()
                        .filter(card ->
                                card.title().toLowerCase().contains(userText.toLowerCase()) ||
                                        card.description().toLowerCase().contains(userText.toLowerCase()))
                        .collect(Collectors.toList()))
                        .subscribeOn(Schedulers.io())
        );
    }
}