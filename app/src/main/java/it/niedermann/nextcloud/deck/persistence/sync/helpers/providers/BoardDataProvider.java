package it.niedermann.nextcloud.deck.persistence.sync.helpers.providers;

import java.util.Date;
import java.util.List;

import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.AccessControl;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.ServerAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DataBaseAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.helpers.SyncHelper;

public class BoardDataProvider extends AbstractSyncDataProvider<FullBoard> {

    public BoardDataProvider(){
        super(null);
    }

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
                dataBaseAdapter.updateUser(accountId, remoteOwner, false);
            }
            owner = dataBaseAdapter.getUserByUidDirectly(accountId, remoteOwner.getUid());
            entity.getBoard().setOwnerId(owner.getLocalId());
        }
    }

    @Override
    public void updateInDB(DataBaseAdapter dataBaseAdapter, long accountId, FullBoard entity) {
        handleOwner(dataBaseAdapter, accountId, entity);
        dataBaseAdapter.updateBoard(entity.getBoard(), false);
    }

    @Override
    public void goDeeper(SyncHelper syncHelper, FullBoard existingEntity, FullBoard entityFromServer, IResponseCallback<Boolean> callback) {
        List<Label> labels = entityFromServer.getLabels();
        if (labels != null && !labels.isEmpty()){
            syncHelper.doSyncFor(new LabelDataProvider(this, existingEntity.getBoard(), labels));
        }
        syncHelper.fixRelations(new BoardLabelRelationshipProvider(existingEntity.getBoard(), labels));

        if (entityFromServer.getStacks() != null && !entityFromServer.getStacks().isEmpty()){
            syncHelper.doSyncFor(new StackDataProvider(this, existingEntity));
        }

        List<AccessControl> acl = entityFromServer.getParticipants();
        if (acl != null && !acl.isEmpty()){
            for (AccessControl ac : acl){
                ac.setBoardId(existingEntity.getLocalId());
            }
            syncHelper.doSyncFor(new AccessControlDataProvider(this, acl));
        }
    }

    @Override
    public void createOnServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<FullBoard> responder, FullBoard entity) {
        serverAdapter.createBoard(entity.getBoard(), responder);
    }

    @Override
    public List<FullBoard> getAllFromDB(DataBaseAdapter dataBaseAdapter, long accountId, Date lastSync) {
        return null;
        // TODO: reactivate for UpSync test
//        return dataBaseAdapter.getLocallyChangedBoards(accountId);
    }

    @Override
    public void goDeeperForUpSync(SyncHelper syncHelper, DataBaseAdapter dataBaseAdapter, IResponseCallback<Boolean> callback) {

        List<Label> locallyChangedLabels = dataBaseAdapter.getLocallyChangedLabels(callback.getAccount().getId());
        for (Label label : locallyChangedLabels) {
            label.setBoardId(dataBaseAdapter.getBoardByLocalIdDirectly(label.getBoardId()).getId());
        }
        syncHelper.doUpSyncFor(new LabelDataProvider(this, null, locallyChangedLabels));
        syncHelper.doUpSyncFor(new StackDataProvider(this, null));
    }

    @Override
    public void updateOnServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<FullBoard> callback, FullBoard entity) {
        serverAdapter.updateBoard(entity.getBoard(), callback);
    }

    @Override
    public void deleteInDB(DataBaseAdapter dataBaseAdapter, long accountId, FullBoard fullBoard) {
        dataBaseAdapter.deleteBoard(fullBoard.getBoard(), false);
    }

    @Override
    public void deleteOnServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<Void> callback, FullBoard entity) {
        serverAdapter.deleteBoard(entity.getBoard(), callback);
    }
}
