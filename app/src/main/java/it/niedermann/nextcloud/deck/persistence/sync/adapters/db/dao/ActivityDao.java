package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import it.niedermann.nextcloud.deck.model.ocs.Activity;

@Dao
public interface ActivityDao extends GenericDao<Activity> {

    @Query("SELECT * FROM activity WHERE cardId = :localCardId")
    LiveData<List<Activity>> getActivitiesForCard(final long localCardId);

    @Query("SELECT * FROM activity WHERE accountId = :accountId and id = :remoteActivityId order by lastModified desc")
    Activity getActivityByRemoteIdDirectly(long accountId, long remoteActivityId);
}