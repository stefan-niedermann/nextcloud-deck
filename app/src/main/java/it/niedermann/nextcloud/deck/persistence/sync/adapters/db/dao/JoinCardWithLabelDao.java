package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import it.niedermann.nextcloud.deck.model.JoinCardWithLabel;

@Dao
public interface JoinCardWithLabelDao extends GenericDao<JoinCardWithLabel> {
    @Query("DELETE FROM joincardwithlabel WHERE  cardId = :localCardId and status == 1") // only if UP_TO_DATE
    void deleteByCardId(long localCardId);

    @Query("DELETE FROM joincardwithlabel WHERE cardId = :localCardId and labelId = :labelId")
    void deleteByCardIdAndLabelId(long localCardId, long labelId);

    @Query("Update joincardwithlabel set status = :status WHERE cardId = :localCardId and labelId = :localLabelId")
    void setDbStatus(long localCardId, long localLabelId, int status);

    @Query("select labelId from joincardwithlabel WHERE cardId = :localCardId and labelId IN (:localLabelIds) and status <> 3") // not LOCAL_DELETED
    List<Long> filterDeleted(long localCardId, List<Long> localLabelIds);

    @Query("select * from joincardwithlabel WHERE cardId = :localCardId and labelId = :localLabelId")
    JoinCardWithLabel getJoin(Long localLabelId, Long localCardId);

    @Query("select l.id as labelId, c.id as cardId, j.status from joincardwithlabel j " +
                "inner join card c on j.cardId = c.localId " +
                "inner join label l on j.labelId = l.localId " +
                "WHERE j.status <> 1") // not UP_TO_DATE
    List<JoinCardWithLabel> getAllDeletedJoinsWithRemoteIDs();

    @Query("select l.id as labelId, c.id as cardId, j.status from joincardwithlabel j " +
                "inner join card c on j.cardId = c.localId " +
                "inner join label l on j.labelId = l.localId " +
                "WHERE j.cardId = :localCardId and j.labelId = :localLabelId") // not UP_TO_DATE
    JoinCardWithLabel getRemoteIdsForJoin(long localCardId, long localLabelId);

    @Query("select * from joincardwithlabel WHERE status <> 1") // not UP_TO_DATE
    List<JoinCardWithLabel> getAllChangedJoins();

    @Query("select j.* from joincardwithlabel j inner join card c on j.cardId = c.localId  WHERE c.stackId = :localStackId and j.status <> 1") // not UP_TO_DATE
    List<JoinCardWithLabel> getAllChangedJoinsForStack(Long localStackId);

    @Query("delete from joincardwithlabel " +
            "where cardId = (select c.localId from card c where c.accountId = :accountId and c.id = :remoteCardId) " +
            "and labelId = (select l.localId from label l where l.accountId = :accountId and l.id = :remoteLabelId)")
    void deleteJoinedLabelForCardPhysicallyByRemoteIDs(Long accountId, Long remoteCardId, Long remoteLabelId);

    @Query("select count(*) from joincardwithlabel WHERE labelId = :localLabelId and status <> 3") // not locally deleted
    LiveData<Integer> countCardsWithLabel(long localLabelId);
}