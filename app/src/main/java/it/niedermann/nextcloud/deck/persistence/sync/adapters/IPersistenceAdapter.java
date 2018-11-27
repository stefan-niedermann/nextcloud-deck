package it.niedermann.nextcloud.deck.persistence.sync.adapters;

import java.util.List;

import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Stack;

public interface IPersistenceAdapter {

    //### STACKS
    void getStacks(long accountId, long boardId, IResponseCallback<List<Stack>> responseCallback);
    void getStack(long accountId, long boardId, long stackId, IResponseCallback<Stack> responseCallback);
    void createStack(long accountId, Stack stack);
    void deleteStack(Stack stack);
    void updateStack(Stack stack);

    //### BOARDS
    void getBoards(long accountId, IResponseCallback<List<Board>> responseCallback);
    void createBoard(long accountId, Board board);
    void deleteBoard(Board board);
    void updateBoard(Board board);

    //### CARDS
    void getCard(long accountId, long boardId, long stackId, long cardId, IResponseCallback<Card> responseCallback);
    void createCard(long accountId, long boardId, long stackId, Card card);
    void deleteCard(Card card);
    void updateCard(Card card);
}
