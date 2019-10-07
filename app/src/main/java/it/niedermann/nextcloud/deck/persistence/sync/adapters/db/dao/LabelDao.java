package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import it.niedermann.nextcloud.deck.model.Label;

@Dao
public interface LabelDao extends GenericDao<Label> {

//    @Query("SELECT * FROM label WHERE stackId = :localStackId")
//    LiveData<List<Label>> getLabelsForStack(final long localStackId);

    @Query("SELECT * FROM label WHERE accountId = :accountId and id = :remoteId")
    LiveData<Label> getLabelByRemoteId(final long accountId, final long remoteId);

    @Query("SELECT * FROM label WHERE localId = :localId")
    LiveData<Label> getLabelByLocalId(final long localId);

    @Query("SELECT * FROM label WHERE accountId = :accountId and id = :remoteId")
    Label getLabelByRemoteIdDirectly(final long accountId, final long remoteId);

    @Query("SELECT * FROM label WHERE localId IN (:labelIDs) and status <> 3") // not LOCAL_DELETED
    List<Label> getLabelsByIdsDirectly(List<Long> labelIDs);

    @Query("SELECT * FROM label WHERE localId = :localLabelID")
    Label getLabelsByIdDirectly(final long localLabelID);

    @Query("SELECT * FROM label WHERE accountId = :accountId and boardId = :boardId and title LIKE :searchTerm")
    LiveData<List<Label>> searchLabelByTitle(final long accountId, final long boardId, String searchTerm);

    @Query("SELECT * FROM label WHERE accountId = :accountId and (status<>1 or id is null or lastModified <> lastModifiedLocal)")
    List<Label> getLocallyChangedLabelsDirectly(long accountId);

    @Query("SELECT l.* " +
            "FROM joincardwithlabel j LEFT JOIN label l ON j.labelId = l.localId " +
            "WHERE l.accountId = :accountId AND l.boardId = :boardId " +
            "AND NOT EXISTS (select 1 from joincardwithlabel jl where jl.labelId = l.localId and jl.cardId = :notAssignedToLocalCardId) " +
            "GROUP BY j.labelId ORDER BY count(*) DESC " +
            "LIMIT :topX")
    LiveData<List<Label>> findProposalsForLabelsToAssign(long accountId, long boardId, long notAssignedToLocalCardId, int topX);
}