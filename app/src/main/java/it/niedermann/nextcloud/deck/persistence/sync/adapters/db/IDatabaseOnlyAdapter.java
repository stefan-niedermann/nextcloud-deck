package it.niedermann.nextcloud.deck.persistence.sync.adapters.db;

import android.arch.lifecycle.LiveData;

import java.util.List;

import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.IDataBasePersistenceAdapter;

public interface IDatabaseOnlyAdapter extends IDataBasePersistenceAdapter {
    LiveData<Board> getBoard(long accountId, long remoteId);

    LiveData<Stack> getStack(long accountId, long localBoardId, long remoteId);

    LiveData<Card> getCard(long accountId, long remoteId);

    LiveData<User> getUser(long accountId, long remoteId);

    void getStacks(long accountId, long boardId, IResponseCallback<LiveData<List<Stack>>> responseCallback);

    void getStack(long accountId, long boardId, long stackId, IResponseCallback<LiveData<Stack>> responseCallback);

    void getBoards(long accountId, IResponseCallback<LiveData<List<Board>>> responseCallback);

    void getCard(long accountId, long boardId, long stackId, long cardId, IResponseCallback<LiveData<FullCard>> responseCallback);

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
