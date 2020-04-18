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

    @Query("SELECT * FROM AccessControl WHERE accountId = :accountId and boardId = :localBoardId and status <> 3")
    LiveData<List<AccessControl>> getAccessControlByLocalBoardId(final long accountId, final long localBoardId);

    @Query("SELECT * FROM AccessControl WHERE accountId = :accountId and id = :remoteId")
    AccessControl getAccessControlByRemoteIdDirectly(final long accountId, final long remoteId);

    @Query("SELECT * FROM AccessControl WHERE accountId = :accountId and boardId = :boardId and (status<>1 or id is null or lastModified <> lastModifiedLocal)")
    List<AccessControl> getLocallyChangedAccessControl(long accountId, long boardId);

    @Query("SELECT distinct boardId FROM AccessControl WHERE accountId = :accountId and (status<>1 or id is null or lastModified <> lastModifiedLocal)")
    List<Long> getBoardIDsOfLocallyChangedAccessControl(long accountId);
}