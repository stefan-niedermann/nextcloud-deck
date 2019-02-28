package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import it.niedermann.nextcloud.deck.model.AccessControl;

@Dao
public interface AccessControlDao extends GenericDao<AccessControl> {

    @Query("SELECT * FROM AccessControl WHERE accountId = :accountId and id = :remoteId")
    LiveData<AccessControl> getAccessControlByRemoteId(final long accountId, final long remoteId);

    @Query("SELECT * FROM AccessControl WHERE accountId = :accountId and id = :remoteId")
    AccessControl getAccessControlByRemoteIdDirectly(final long accountId, final long remoteId);
}