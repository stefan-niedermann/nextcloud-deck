package it.niedermann.nextcloud.deck.persistence.sync.helpers.providers;

import java.util.Date;
import java.util.List;

import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.AccessControl;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.ServerAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DataBaseAdapter;

public class AccessControlDataProvider extends AbstractSyncDataProvider<AccessControl> {

    private List<AccessControl> acl;
    private FullBoard board;

    public AccessControlDataProvider(AbstractSyncDataProvider<?> parent, FullBoard board, List<AccessControl> acl) {
        super(parent);
        this.board = board;
        this.acl = acl;
    }

    @Override
    public void getAllFromServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<List<AccessControl>> responder, Date lastSync) {
        responder.onResponse(acl);
    }

    @Override
    public AccessControl getSingleFromDB(DataBaseAdapter dataBaseAdapter, long accountId, AccessControl entity) {
        return dataBaseAdapter.getAccessControlByRemoteIdDirectly(accountId, entity.getEntity().getId());
    }

    @Override
    public long createInDB(DataBaseAdapter dataBaseAdapter, long accountId, AccessControl entity) {
        prepareUser(dataBaseAdapter, accountId, entity);
        return dataBaseAdapter.createAccessControl(accountId, entity);
    }

    private void prepareUser(DataBaseAdapter dataBaseAdapter, long accountId, AccessControl entity) {
        User user = dataBaseAdapter.getUserByUidDirectly(accountId, entity.getUser().getUid());
        if (user == null) {
            long userId = dataBaseAdapter.createUser(accountId, entity.getUser());
            entity.setUserId(userId);
        } else {
            entity.setUserId(user.getLocalId());
            entity.getUser().setLocalId(user.getLocalId());
            dataBaseAdapter.updateUser(accountId, entity.getUser(), false);
        }
    }

    @Override
    public void updateInDB(DataBaseAdapter dataBaseAdapter, long accountId, AccessControl entity, boolean setStatus) {
        prepareUser(dataBaseAdapter, accountId, entity);
        dataBaseAdapter.updateAccessControl(entity, setStatus);
    }

    @Override
    public void updateInDB(DataBaseAdapter dataBaseAdapter, long accountId, AccessControl entity) {
        updateInDB(dataBaseAdapter, accountId, entity, false);
    }

    @Override
    public void createOnServer(ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, long accountId, IResponseCallback<AccessControl> responder, AccessControl entity) {
        AccessControl acl = new AccessControl(entity);
        acl.setBoardId(board.getBoard().getId());
        serverAdapter.createAccessControl(board.getBoard().getId(), acl, responder);
    }

    @Override
    public void updateOnServer(ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, long accountId, IResponseCallback<AccessControl> callback, AccessControl entity) {
        serverAdapter.updateAccessControl(board.getBoard().getId(), entity, callback);
    }

    @Override
    public void deleteInDB(DataBaseAdapter dataBaseAdapter, long accountId, AccessControl accessControl) {
        dataBaseAdapter.deleteAccessControl(accessControl, true);
    }

    @Override
    public void deletePhysicallyInDB(DataBaseAdapter dataBaseAdapter, long accountId, AccessControl accessControl) {
        dataBaseAdapter.deleteAccessControl(accessControl, false);
    }

    @Override
    public void deleteOnServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<Void> callback, AccessControl entity, DataBaseAdapter dataBaseAdapter) {
        serverAdapter.deleteAccessControl(board.getBoard().getId(), entity, callback);
    }

    @Override
    public List<AccessControl> getAllChangedFromDB(DataBaseAdapter dataBaseAdapter, long accountId, Date lastSync) {
        return dataBaseAdapter.getLocallyChangedAccessControl(accountId, board.getLocalId());
    }
}
