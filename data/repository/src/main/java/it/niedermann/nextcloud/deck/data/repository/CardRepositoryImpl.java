package it.niedermann.nextcloud.deck.data.repository;

import org.reactivestreams.FlowAdapters;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.Flowable;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.model.Column;
import it.niedermann.nextcloud.deck.domain.model.CreateCard;
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
        return FlowAdapters.toFlowPublisher(Flowable.just(MockData.MOCK_CARDS.stream().filter(card -> Objects.equals(card.columnId(), columnId)).toList()));
    }

    @Override
    public Flow.Publisher<Map<Column, List<Card>>> getNotDeletedCardsByColumn(Board.ID boardId) {
        // TODO Mock Implementation
        return FlowAdapters.toFlowPublisher(Flowable.just(
                MockData.MOCK_CARDS.stream().filter(card -> Objects.equals(card.boardId(), boardId))
                        .collect(Collectors.groupingBy(card -> MockData.MOCK_COLUMNS[(int) card.columnId().value()]))
        ));
    }

    @Override
    public Flow.Publisher<Card> getCard(Card.ID cardId) {
        // TODO Mock Implementation
        if (cardId.value() < MockData.MOCK_CARDS.size()) {
            return FlowAdapters.toFlowPublisher(Flowable.just(MockData.MOCK_CARDS.get((int) cardId.value())));
        }

        return FlowAdapters.toFlowPublisher(Flowable.error(new NoSuchElementException("No card with id " + cardId)));
    }

    @Override
    public Flow.Publisher<Collection<Card>> find(String userText) {
        // TODO Mock Implementation
        return FlowAdapters.toFlowPublisher(Flowable.just(
                MockData.MOCK_CARDS.stream()
                        .filter(card ->
                                card.title().toLowerCase().contains(userText.toLowerCase()) ||
                                card.description().toLowerCase().contains(userText.toLowerCase())
                        )
                        .collect(Collectors.toList())));
    }
}