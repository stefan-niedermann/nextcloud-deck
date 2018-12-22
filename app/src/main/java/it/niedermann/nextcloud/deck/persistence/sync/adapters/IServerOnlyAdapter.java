package it.niedermann.nextcloud.deck.persistence.sync.adapters;

import java.util.List;

import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.model.full.FullCard;

public interface IServerOnlyAdapter extends IPersistenceAdapter {
    void getStacks(long accountId, long boardId, IResponseCallback<List<Stack>> responseCallback);

    void getStack(long accountId, long boardId, long stackId, IResponseCallback<Stack> responseCallback);

    void getBoards(long accountId, IResponseCallback<List<Board>> responseCallback);

    void getCard(long accountId, long boardId, long stackId, long cardId, IResponseCallback<FullCard> responseCallback);

}
