package it.niedermann.nextcloud.deck.persistence.sync.helpers.providers;

import java.util.Date;
import java.util.List;

import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.AccessControl;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.ServerAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DataBaseAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.helpers.SyncHelper;

public class AccessControlDataProvider extends IDataProvider<AccessControl> {

    private List<AccessControl> acl;

    public AccessControlDataProvider(IDataProvider<?> parent, List<AccessControl> acl) {
        super(parent);
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
    public void updateInDB(DataBaseAdapter dataBaseAdapter, long accountId, AccessControl entity) {
        prepareUser(dataBaseAdapter, accountId, entity);
        dataBaseAdapter.updateAccessControl(entity, false);
    }

    @Override
    public void goDeeper(SyncHelper syncHelper, AccessControl existingEntity, AccessControl entityFromServer) {
        // ain't goin' deeper <3
        return;
    }

    @Override
    public void createOnServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<AccessControl> responder, AccessControl entity) {

    }

    @Override
    public void updateOnServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<AccessControl> callback, AccessControl entity) {

    }

    @Override
    public void deleteInDB(DataBaseAdapter dataBaseAdapter, long accountId, AccessControl accessControl) {

    }

    @Override
    public void deleteOnServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<AccessControl> callback, AccessControl entity) {

    }

    @Override
    public List<AccessControl> getAllFromDB(DataBaseAdapter dataBaseAdapter, long accountId, Date lastSync) {
        return null;
    }

    @Override
    public void goDeeperForUpSync(SyncHelper syncHelper, AccessControl entity, AccessControl response) {

    }
}
