package it.niedermann.nextcloud.deck.persistence.sync.adapters.db;

import android.arch.lifecycle.LiveData;

import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.IDataBasePersistenceAdapter;

public interface IDatabaseOnlyAdapter extends IDataBasePersistenceAdapter {
    LiveData<Board> getBoard(long accountId, long remoteId);

    LiveData<Stack> getStack(long accountId, long localBoardId, long remoteId);

    LiveData<Card> getCard(long accountId, long remoteId);

    LiveData<User> getUser(long accountId, long remoteId);

    void createUser(long accountId, User user);

    void updateUser(long accountId, User user);

    Label getLabel(long accountId, long remoteId);

    void createLabel(long accountId, Label label);

    void createJoinCardWithLabel(long localLabelId, long localCardId);

    void deleteJoinedLabelsForCard(long localCardId);

    void createJoinCardWithUser(long localUserId, long localCardId);

    void deleteJoinedUsersForCard(long localCardId);

    void createJoinStackWithCard(long localCardId, long localStackId);

    void deleteJoinedCardsForStack(long localStackId);


    void updateLabel(long accountId, Label label);
}
