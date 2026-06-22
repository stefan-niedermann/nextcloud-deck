package it.niedermann.nextcloud.deck.data.repository;

import org.reactivestreams.FlowAdapters;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;

import io.reactivex.rxjava3.core.Flowable;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.repository.CardRepository;
import jakarta.inject.Inject;

public class CardRepositoryImpl implements CardRepository {

    final List<Card> cards = List.of(
            new Card(0, 0, 0, 0, LocalDateTime.now(), LocalDateTime.now(), 0, "Card-Title #0", "Card-Description 0 Lorem Ipsum Dolor sit Amet", Collections.emptySet(), Collections.emptySet(), Collections.emptyList(), null, LocalDateTime.now(), null, null, Collections.emptySet(), false, false, 0, 0),
            new Card(1, 0, 0, 0, LocalDateTime.now(), LocalDateTime.now(), 0, "Card-Title #1", "Card-Description 1 Lorem Ipsum Dolor sit Amet", Collections.emptySet(), Collections.emptySet(), Collections.emptyList(), null, LocalDateTime.now(), null, null, Collections.emptySet(), false, false, 0, 0),
            new Card(2, 0, 0, 1, LocalDateTime.now(), LocalDateTime.now(), 0, "Card-Title #2", "Card-Description 2 Lorem Ipsum Dolor sit Amet", Collections.emptySet(), Collections.emptySet(), Collections.emptyList(), null, LocalDateTime.now(), null, null, Collections.emptySet(), false, false, 0, 0),
            new Card(3, 0, 0, 1, LocalDateTime.now(), LocalDateTime.now(), 0, "Card-Title #3", "Card-Description 3 Lorem Ipsum Dolor sit Amet", Collections.emptySet(), Collections.emptySet(), Collections.emptyList(), null, LocalDateTime.now(), null, null, Collections.emptySet(), false, false, 0, 0),
            new Card(4, 0, 0, 2, LocalDateTime.now(), LocalDateTime.now(), 0, "Card-Title #4", "Card-Description 4 Lorem Ipsum Dolor sit Amet", Collections.emptySet(), Collections.emptySet(), Collections.emptyList(), null, LocalDateTime.now(), null, null, Collections.emptySet(), false, false, 0, 0),
            new Card(5, 0, 0, 2, LocalDateTime.now(), LocalDateTime.now(), 0, "Card-Title #5", "Card-Description 5 Lorem Ipsum Dolor sit Amet", Collections.emptySet(), Collections.emptySet(), Collections.emptyList(), null, LocalDateTime.now(), null, null, Collections.emptySet(), false, false, 0, 0),
            new Card(6, 0, 0, 3, LocalDateTime.now(), LocalDateTime.now(), 0, "Card-Title #6", "Card-Description 6 Lorem Ipsum Dolor sit Amet", Collections.emptySet(), Collections.emptySet(), Collections.emptyList(), null, LocalDateTime.now(), null, null, Collections.emptySet(), false, false, 0, 0),
            new Card(7, 0, 0, 3, LocalDateTime.now(), LocalDateTime.now(), 0, "Card-Title #7", "Card-Description 7 Lorem Ipsum Dolor sit Amet", Collections.emptySet(), Collections.emptySet(), Collections.emptyList(), null, LocalDateTime.now(), null, null, Collections.emptySet(), false, false, 0, 0),
            new Card(8, 0, 0, 4, LocalDateTime.now(), LocalDateTime.now(), 0, "Card-Title #8", "Card-Description 8 Lorem Ipsum Dolor sit Amet", Collections.emptySet(), Collections.emptySet(), Collections.emptyList(), null, LocalDateTime.now(), null, null, Collections.emptySet(), false, false, 0, 0),
            new Card(9, 0, 0, 4, LocalDateTime.now(), LocalDateTime.now(), 0, "Card-Title #9", "Card-Description 9 Lorem Ipsum Dolor sit Amet", Collections.emptySet(), Collections.emptySet(), Collections.emptyList(), null, LocalDateTime.now(), null, null, Collections.emptySet(), false, false, 0, 0)
    );

    @Inject
    public CardRepositoryImpl(
    ) {
    }

    @Override
    public CompletableFuture<Void> createCard(Card card) {
        System.out.println("[Mock][" + CardRepositoryImpl.class.getSimpleName() + "/createCard]: " + card);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> updateCard(Card card) {
        System.out.println("[Mock][" + CardRepositoryImpl.class.getSimpleName() + "/updateCard]: " + card);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> deleteCard(long cardId) {
        System.out.println("[Mock][" + CardRepositoryImpl.class.getSimpleName() + "/deleteCard]: " + cardId);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> assignUser(long cardId, String userId) {
        System.out.println("[Mock][" + CardRepositoryImpl.class.getSimpleName() + "/assign]: " + cardId + " / " + userId);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> unassignUser(long cardId, String userId) {
        System.out.println("[Mock][" + CardRepositoryImpl.class.getSimpleName() + "/unassign]: " + cardId + " / " + userId);
        return CompletableFuture.completedFuture(null);
    }

    // TODO Mock Implementation
    @SuppressWarnings("NewApi")
    @Override
    public Flow.Publisher<List<Card>> getNotDeletedCards(long columnId) {
        return FlowAdapters.toFlowPublisher(Flowable.just(cards.stream().filter(card -> card.columnId() == columnId).toList()));
    }

    // TODO Mock Implementation
    @Override
    public Flow.Publisher<Card> getCard(long cardId) {
        if (cardId < cards.size()) {
            return FlowAdapters.toFlowPublisher(Flowable.just(cards.get((int) cardId)));
        }

        return FlowAdapters.toFlowPublisher(Flowable.error(new NoSuchElementException("No card with id " + cardId)));
    }
}