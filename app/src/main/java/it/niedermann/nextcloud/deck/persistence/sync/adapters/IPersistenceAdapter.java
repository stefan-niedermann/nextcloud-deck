package it.niedermann.nextcloud.deck.persistence.sync.adapters;

import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Stack;

public interface IPersistenceAdapter {

    //### STACKS
    void createStack(long accountId, Stack stack);

    void deleteStack(Stack stack);

    void updateStack(Stack stack);

    //### BOARDS
    void createBoard(long accountId, Board board);

    void deleteBoard(Board board);

    void updateBoard(Board board);

    //### CARDS
    void createCard(long accountId, Card card);

    void deleteCard(Card card);

    void updateCard(Card card);
}
