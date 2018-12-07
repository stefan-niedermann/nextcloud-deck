package it.niedermann.nextcloud.deck.persistence.sync.adapters;

import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Stack;

public interface IDatabaseOnlyAdapter extends IDataBasePersistenceAdapter {
    public Board getBoard(long accountId, long remoteId);

    public Stack getStack(long accountId, long localBoardId, long remoteId);
}
