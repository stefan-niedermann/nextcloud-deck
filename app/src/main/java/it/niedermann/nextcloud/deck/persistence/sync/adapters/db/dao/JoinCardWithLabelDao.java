package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao;

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
}