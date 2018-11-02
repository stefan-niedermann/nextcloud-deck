package it.niedermann.nextcloud.deck.persistence.sync.adapters;

import java.util.List;

import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Stack;

public interface IPersistenceAdapter {

    void getBoards(long accountId, IResponseCallback<List<Board>> responseCallback);
    void getStacks(long accountId, long boardId, IResponseCallback<List<Stack>> responseCallback);
    void getCards(long accountId, long stackId, IResponseCallback<List<Card>> responseCallback);
}
