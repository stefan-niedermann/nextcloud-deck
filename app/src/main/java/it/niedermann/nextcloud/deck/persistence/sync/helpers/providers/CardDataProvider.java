package it.niedermann.nextcloud.deck.persistence.sync.helpers.providers;

import java.util.List;

import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.model.full.FullStack;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.ServerAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DataBaseAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.helpers.SyncHelper;

public class CardDataProvider implements IDataProvider<FullCard> {

    private Board board;
    private FullStack stack;

    public CardDataProvider(Board board, FullStack stack) {
        this.board = board;
        this.stack = stack;
    }

    @Override
    public void getAllFromServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<List<FullCard>> responder) {
        responder.onResponse(stack.getCards());
    }

    @Override
    public FullCard getSingleFromDB(DataBaseAdapter dataBaseAdapter, long accountId, long remoteId) {
        return dataBaseAdapter.getFullCardByRemoteIdDirectly(accountId, remoteId);
    }

    @Override
    public void createInDB(DataBaseAdapter dataBaseAdapter, long accountId, FullCard entity) {
        dataBaseAdapter.createCard(accountId, entity.getCard());
    }

    @Override
    public void updateInDB(DataBaseAdapter dataBaseAdapter, long accountId, FullCard entity) {
        dataBaseAdapter.updateCard(entity.getCard());
    }

    @Override
    public void goDeeper(SyncHelper syncHelper, FullCard entityFromServer) {
        syncHelper.doSyncFor(new LabelDataProvider(board, stack, entityFromServer));
        syncHelper.doSyncFor(new UserDataProvider(board, stack, entityFromServer, entityFromServer.getAssignedUsers()));
        syncHelper.doSyncFor(new UserDataProvider(board, stack, entityFromServer, entityFromServer.getOwner()));

        //TODO: Relations between card & users/labels!
        return;
    }
}
