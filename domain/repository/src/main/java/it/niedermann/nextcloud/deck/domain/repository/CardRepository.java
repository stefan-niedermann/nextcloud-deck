package it.niedermann.nextcloud.deck.domain.repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;

import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.model.Column;

public interface CardRepository {

    CompletableFuture<Void> createCard(Card card);

    CompletableFuture<Void> updateCard(Card card);

    CompletableFuture<Void> deleteCard(long cardId);

    CompletableFuture<Void> assignUser(long cardId, String userId);

    CompletableFuture<Void> unassignUser(long cardId, String userId);

    Flow.Publisher<List<Card>> getNotDeletedCards(long columnId);

    Flow.Publisher<Map<Column, List<Card>>> getNotDeletedCardsByColumn(long boardId);

    Flow.Publisher<Card> getCard(long cardId);

    Flow.Publisher<Collection<Card>> find(String userText);
}
