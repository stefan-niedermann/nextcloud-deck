package it.niedermann.nextcloud.deck.persistence.sync.helpers.providers;

import java.util.List;

import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.ServerAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DataBaseAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.helpers.SyncHelper;

public class BoardDataProvider implements IDataProvider<Board> {
    @Override
    public void getAllFromServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<List<Board>> responder) {
        serverAdapter.getBoards(accountId, responder);
    }

    @Override
    public Board getSingleFromDB(DataBaseAdapter dataBaseAdapter, long accountId, long remoteId) {
        return dataBaseAdapter.getBoardByRemoteIdDirectly(accountId, remoteId);
    }

    @Override
    public void createInDB(DataBaseAdapter dataBaseAdapter, long accountId, Board entity) {
        dataBaseAdapter.createBoard(accountId, entity);
    }

    @Override
    public void updateInDB(DataBaseAdapter dataBaseAdapter, long accountId, Board entity) {
        dataBaseAdapter.updateBoard(entity);
    }

    @Override
    public void goDeeper(SyncHelper syncHelper, Board entityFromServer) {
        syncHelper.doSyncFor(new StackDataProvider(entityFromServer));
    }

    @Override
    public void doneAll(IResponseCallback<Boolean> responseCallback, boolean syncChangedSomething) {
        responseCallback.onResponse(syncChangedSomething);
    }
}
