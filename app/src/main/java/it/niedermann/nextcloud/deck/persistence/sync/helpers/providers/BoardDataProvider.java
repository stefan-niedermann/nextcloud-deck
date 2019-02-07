package it.niedermann.nextcloud.deck.persistence.sync.helpers.providers;

import java.util.List;

import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.ServerAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DataBaseAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.helpers.SyncHelper;

public class BoardDataProvider implements IDataProvider<FullBoard> {
    @Override
    public void getAllFromServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<List<FullBoard>> responder) {
        serverAdapter.getBoards(responder);
    }

    @Override
    public FullBoard getSingleFromDB(DataBaseAdapter dataBaseAdapter, long accountId, long remoteId) {
        return dataBaseAdapter.getFullBoardByRemoteIdDirectly(accountId, remoteId);
    }

    @Override
    public void createInDB(DataBaseAdapter dataBaseAdapter, long accountId, FullBoard entity) {
        handleOwner(dataBaseAdapter, accountId, entity);
        dataBaseAdapter.createBoard(accountId, entity.getBoard());
    }

    private void handleOwner(DataBaseAdapter dataBaseAdapter, long accountId, FullBoard entity) {
        if (entity.getOwner()!=null && entity.getOwner().size() == 1) {
            User remoteOwner = entity.getOwner().get(0);
            User owner = dataBaseAdapter.getUserByUidDirectly(accountId, remoteOwner.getUid());
            if (owner == null){
                dataBaseAdapter.createUser(accountId, remoteOwner);
            } else {
                dataBaseAdapter.updateUser(accountId, remoteOwner);
            }
            owner = dataBaseAdapter.getUserByUidDirectly(accountId, remoteOwner.getUid());
            entity.getBoard().setOwnerId(owner.getLocalId());
        }
    }

    @Override
    public void updateInDB(DataBaseAdapter dataBaseAdapter, long accountId, FullBoard entity) {
        handleOwner(dataBaseAdapter, accountId, entity);
        dataBaseAdapter.updateBoard(entity.getBoard());
    }

    @Override
    public void goDeeper(SyncHelper syncHelper, FullBoard existingEntity, FullBoard entityFromServer) {
        syncHelper.doSyncFor(new LabelDataProvider(entityFromServer.getLabels()));
        syncHelper.fixRelations(new BoardLabelRelationshipProvider(existingEntity.getBoard(), entityFromServer.getLabels()));
        syncHelper.doSyncFor(new StackDataProvider(existingEntity));
    }

    @Override
    public void doneAll(IResponseCallback<Boolean> responseCallback, boolean syncChangedSomething) {
        responseCallback.onResponse(syncChangedSomething);
    }
}
