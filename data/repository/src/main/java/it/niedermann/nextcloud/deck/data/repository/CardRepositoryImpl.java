package it.niedermann.nextcloud.deck.data.repository;

import org.reactivestreams.FlowAdapters;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.Flowable;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.model.Column;
import it.niedermann.nextcloud.deck.domain.repository.CardRepository;
import jakarta.inject.Inject;

public class CardRepositoryImpl implements CardRepository {

    @Inject
    public CardRepositoryImpl(
    ) {
    }

    @Override
    public CompletableFuture<Void> createCard(Card card) {
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
    public CompletableFuture<Void> deleteCard(long cardId) {
        // TODO Mock Implementation
        System.out.println("[Mock][" + CardRepositoryImpl.class.getSimpleName() + "/deleteCard]: " + cardId);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> assignUser(long cardId, String userId) {
        // TODO Mock Implementation
        System.out.println("[Mock][" + CardRepositoryImpl.class.getSimpleName() + "/assign]: " + cardId + " / " + userId);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> unassignUser(long cardId, String userId) {
        // TODO Mock Implementation
        System.out.println("[Mock][" + CardRepositoryImpl.class.getSimpleName() + "/unassign]: " + cardId + " / " + userId);
        return CompletableFuture.completedFuture(null);
    }

    @SuppressWarnings("NewApi")
    @Override
    public Flow.Publisher<List<Card>> getNotDeletedCards(long columnId) {
        // TODO Mock Implementation
        return FlowAdapters.toFlowPublisher(Flowable.just(MockData.MOCK_CARDS.stream().filter(card -> card.columnId() == columnId).toList()));
    }

    @Override
    public Flow.Publisher<Map<Column, List<Card>>> getNotDeletedCardsByColumn(long boardId) {
        // TODO Mock Implementation
        return FlowAdapters.toFlowPublisher(Flowable.just(
                MockData.MOCK_CARDS.stream().filter(card -> card.boardId() == boardId)
                        .collect(Collectors.groupingBy(card -> MockData.MOCK_COLUMNS[(int) card.columnId()]))
        ));
    }

    @Override
    public Flow.Publisher<Card> getCard(long cardId) {
        // TODO Mock Implementation
        if (cardId < MockData.MOCK_CARDS.size()) {
            return FlowAdapters.toFlowPublisher(Flowable.just(MockData.MOCK_CARDS.get((int) cardId)));
        }

        return FlowAdapters.toFlowPublisher(Flowable.error(new NoSuchElementException("No card with id " + cardId)));
    }

    @Override
    public Flow.Publisher<Collection<Card>> find(String userText) {
        // TODO Mock Implementation
        return FlowAdapters.toFlowPublisher(Flowable.just(
                MockData.MOCK_CARDS.stream()
                        .filter(card -> card.title().toLowerCase().startsWith(userText.toLowerCase()))
                        .collect(Collectors.toList())));
    }
}