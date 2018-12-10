package it.niedermann.nextcloud.deck.persistence.sync.adapters;

import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Stack;

public interface IDatabaseOnlyAdapter extends IDataBasePersistenceAdapter {
    Board getBoard(long accountId, long remoteId);

    Stack getStack(long accountId, long localBoardId, long remoteId);
    Card getCard(long accountId, long remoteId);
}
