package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import it.niedermann.nextcloud.deck.model.AccessControl;

@Dao
public interface AccessControlDao extends GenericDao<AccessControl> {

    @Query("SELECT * FROM AccessControl WHERE accountId = :accountId and id = :remoteId")
    LiveData<AccessControl> getAccessControlByRemoteId(final long accountId, final long remoteId);

    @Query("SELECT * FROM AccessControl WHERE accountId = :accountId and boardId = :localBoardId")
    LiveData<List<AccessControl>> getAccessControlByLocalBoardId(final long accountId, final long localBoardId);

    @Query("SELECT * FROM AccessControl WHERE accountId = :accountId and id = :remoteId")
    AccessControl getAccessControlByRemoteIdDirectly(final long accountId, final long remoteId);
}