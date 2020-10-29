package it.niedermann.nextcloud.deck.persistence.sync.helpers.providers.partial;

import java.util.List;

import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.AccessControl;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.ServerAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DataBaseAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.helpers.SyncHelper;
import it.niedermann.nextcloud.deck.persistence.sync.helpers.providers.AccessControlDataProvider;
import it.niedermann.nextcloud.deck.persistence.sync.helpers.providers.BoardDataProvider;

public class BoardWithAclDownSyncDataProvider extends BoardDataProvider {

    @Override
    public void goDeeper(SyncHelper syncHelper, FullBoard existingEntity, FullBoard entityFromServer, IResponseCallback<Boolean> callback) {

        List<AccessControl> acl = entityFromServer.getParticipants();
        if (acl != null && !acl.isEmpty()){
            for (AccessControl ac : acl){
                ac.setBoardId(existingEntity.getLocalId());
            }
            syncHelper.doSyncFor(new AccessControlDataProvider(this, existingEntity, acl));
        }
    }

    @Override
    public void goDeeperForUpSync(SyncHelper syncHelper, ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, IResponseCallback<Boolean> callback) {
        // do nothing!
    }
}
