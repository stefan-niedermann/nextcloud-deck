package it.niedermann.nextcloud.deck.persistence.sync.helpers.providers;

import java.util.Date;
import java.util.List;

import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.AccessControl;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.ServerAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DataBaseAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.helpers.SyncHelper;

public class BoardDataProvider implements IDataProvider<FullBoard> {
    @Override
    public void getAllFromServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<List<FullBoard>> responder, Date lastSync) {
        serverAdapter.getBoards(responder);
    }

    @Override
    public FullBoard getSingleFromDB(DataBaseAdapter dataBaseAdapter, long accountId, FullBoard entitiy) {
        return dataBaseAdapter.getFullBoardByRemoteIdDirectly(accountId, entitiy.getEntity().getId());
    }

    @Override
    public long createInDB(DataBaseAdapter dataBaseAdapter, long accountId, FullBoard entity) {
        handleOwner(dataBaseAdapter, accountId, entity);
        return dataBaseAdapter.createBoardDirectly(accountId, entity.getBoard());
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
        List<AccessControl> acl = entityFromServer.getParticipants();
        if (acl != null && !acl.isEmpty()){
            for (AccessControl ac : acl){
                ac.setBoardId(existingEntity.getLocalId());
            }
            syncHelper.doSyncFor(new AccessControlDataProvider(acl));
        }
    }

    @Override
    public void createOnServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<FullBoard> responder, FullBoard entity) {
        serverAdapter.createBoard(entity.getBoard(), responder);
    }

    @Override
    public void doneAll(IResponseCallback<Boolean> responseCallback, boolean syncChangedSomething) {
        responseCallback.onResponse(syncChangedSomething);
    }

    @Override
    public List<FullBoard> getAllFromDB(DataBaseAdapter dataBaseAdapter, long accountId, Date lastSync) {
        return null;
    }

    @Override
    public void goDeeperForUpSync(SyncHelper syncHelper, FullBoard entity, FullBoard response) {
        syncHelper.doUpSyncFor(new StackDataProvider(entity));
    }

    @Override
    public void updateOnServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<FullBoard> callback, FullBoard entity) {
        serverAdapter.updateBoard(entity.getBoard());
    }

    @Override
    public void deleteInDB(DataBaseAdapter dataBaseAdapter, long accountId, FullBoard fullBoard) {
        dataBaseAdapter.deleteBoard(fullBoard.getBoard());
    }

    @Override
    public void deleteOnServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<FullBoard> callback, FullBoard entity) {

    }
}
