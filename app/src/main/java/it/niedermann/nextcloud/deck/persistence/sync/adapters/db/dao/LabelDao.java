package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import it.niedermann.nextcloud.deck.model.Label;

@Dao
public interface LabelDao extends GenericDao<Label> {

//    @Query("SELECT * FROM label WHERE stackId = :localStackId")
//    LiveData<List<Label>> getLabelsForStack(final long localStackId);

    @Query("SELECT * FROM label WHERE accountId = :accountId and id = :remoteId")
    LiveData<Label> getLabelByRemoteId(final long accountId, final long remoteId);

    @Query("SELECT * FROM label WHERE accountId = :accountId and id = :remoteId")
    Label getLabelByRemoteIdDirectly(final long accountId, final long remoteId);

    @Query("SELECT * FROM label WHERE localId IN (:labelIDs) and status <> 3") // not LOCAL_DELETED
    List<Label> getLabelsByIdDirectly(List<Long> labelIDs);

    @Query("SELECT * FROM label WHERE accountId = :accountId and title = :searchTerm")
    LiveData<List<Label>> searchLabelByTitle(final long accountId, String searchTerm);
}