package it.niedermann.nextcloud.deck.persistence.sync.adapters;

import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.model.User;

public interface IDatabaseOnlyAdapter extends IDataBasePersistenceAdapter {
    Board getBoard(long accountId, long remoteId);

    Stack getStack(long accountId, long localBoardId, long remoteId);
    Card getCard(long accountId, long remoteId);

    User getUser(long accountId, long remoteId);

    void createUser(long accountId, User user);

    void updateUser(long accountId, User user);

    Label getLabel(long accountId, long remoteId);

    void createLabel(long accountId, Label label);
    void createJoinLabelWithCard(long labelId, long cardId);
    void deleteJoinLabelsForCard(long cardId);


    void updateLabel(long accountId, Label label);
}
