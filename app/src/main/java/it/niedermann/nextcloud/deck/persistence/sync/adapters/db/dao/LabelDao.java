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

    @Query("SELECT l.* FROM label l WHERE accountId = :accountId" +
            " AND NOT EXISTS (" +
                "select 1 from joincardwithlabel jl where jl.labelId = l.localId " +
                "and jl.cardId = :notYetAssignedToLocalCardId AND status <> 3" + // not LOCAL_DELETED
            ") " +
            " AND boardId = :boardId and title LIKE :searchTerm")
    LiveData<List<Label>> searchNotYetAssignedLabelsByTitle(final long accountId, final long boardId, final long notYetAssignedToLocalCardId, String searchTerm);

    @Query("SELECT * FROM label WHERE accountId = :accountId and (status<>1 or id is null or lastModified <> lastModifiedLocal)")
    List<Label> getLocallyChangedLabelsDirectly(long accountId);

    @Query("SELECT l.* " +
            "FROM label l LEFT JOIN joincardwithlabel j ON j.labelId = l.localId " +
            "WHERE l.accountId = :accountId AND l.boardId = :boardId " +
            "AND NOT EXISTS (" +
                "select 1 from joincardwithlabel jl where jl.labelId = l.localId " +
                "and jl.cardId = :notAssignedToLocalCardId AND status <> 3" + // not LOCAL_DELETED
            ") " +
            "GROUP BY l.localId ORDER BY count(*) DESC")
    LiveData<List<Label>> findProposalsForLabelsToAssign(long accountId, long boardId, long notAssignedToLocalCardId);
}