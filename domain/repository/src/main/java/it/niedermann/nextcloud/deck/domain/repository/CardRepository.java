package it.niedermann.nextcloud.deck.domain.repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;

import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.model.Column;
import it.niedermann.nextcloud.deck.domain.model.CreateCard;
import it.niedermann.nextcloud.deck.domain.model.User;

public interface CardRepository {

    CompletableFuture<Void> createCard(CreateCard card);

    CompletableFuture<Void> updateCard(Card card);

    CompletableFuture<Void> deleteCard(Card.ID cardId);

    CompletableFuture<Void> assignUser(Card.ID cardId, User.ID userId);

    CompletableFuture<Void> unassignUser(Card.ID cardId, User.ID userId);

    Flow.Publisher<List<Card>> getNotDeletedCards(Column.ID columnId);

    Flow.Publisher<Map<Column, List<Card>>> getNotDeletedCardsByColumn(Board.ID boardId);

    Flow.Publisher<Card> getCard(Card.ID cardId);

    Flow.Publisher<Collection<Card>> find(String userText);
}
