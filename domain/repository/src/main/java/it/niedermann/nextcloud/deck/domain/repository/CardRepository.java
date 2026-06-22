package it.niedermann.nextcloud.deck.domain.repository;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;

import it.niedermann.nextcloud.deck.domain.model.Card;

public interface CardRepository {

    CompletableFuture<Void> createCard(Card card);

    CompletableFuture<Void> updateCard(Card card);

    CompletableFuture<Void> assignUser(long cardId, String userId);

    CompletableFuture<Void> unassignUser(long cardId, String userId);

    Flow.Publisher<List<Card>> getNotDeletedCards(long columnId);

    Flow.Publisher<Card> getCard(long cardId);
}
