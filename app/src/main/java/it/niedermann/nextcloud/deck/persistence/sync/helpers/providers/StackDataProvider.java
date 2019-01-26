package it.niedermann.nextcloud.deck.persistence.sync.helpers.providers;

import java.util.List;

import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.model.full.FullStack;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.ServerAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DataBaseAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.helpers.SyncHelper;

public class StackDataProvider implements IDataProvider<FullStack> {
    private FullBoard board;

    public StackDataProvider(FullBoard board) {
        this.board = board;
    }

    @Override
    public void getAllFromServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<List<FullStack>> responder) {
        serverAdapter.getStacks(accountId, board.getId(), responder);
    }

    @Override
    public FullStack getSingleFromDB(DataBaseAdapter dataBaseAdapter, long accountId, long remoteId) {
        return dataBaseAdapter.getFullStackByRemoteIdDirectly(accountId, board.getLocalId(), remoteId);
    }

    @Override
    public void createInDB(DataBaseAdapter dataBaseAdapter, long accountId, FullStack entity) {
        entity.getStack().setBoardId(board.getLocalId());
        dataBaseAdapter.createStack(accountId, entity.getStack());
    }

    @Override
    public void updateInDB(DataBaseAdapter dataBaseAdapter, long accountId, FullStack entity) {
        dataBaseAdapter.updateStack(entity.getStack());
    }

    @Override
    public void goDeeper(SyncHelper syncHelper, FullStack entityFromServer) {
        syncHelper.doSyncFor(new CardDataProvider(board.getBoard(), entityFromServer));
    }
}
