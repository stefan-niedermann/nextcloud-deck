package it.niedermann.nextcloud.deck.database.dao;

import androidx.room.Dao;
import androidx.room.Query;

import it.niedermann.nextcloud.deck.model.JoinCardWithDependentCard;

@Dao
public interface JoinBoardWithDependentCardDao extends GenericDao<JoinCardWithDependentCard> {

    @Query("DELETE FROM DependentCards WHERE  localCardId = :localCardId and status == 1")
    void deleteDependentsOfCard(long localCardId);
    @Query("select * FROM DependentCards WHERE  localCardId = :localCardId and dependentRemoteCardId = :remoteCardId")
    JoinCardWithDependentCard getDependentsOfCard(long localCardId, long remoteCardId);

    @Query("DELETE FROM joinboardwithlabel WHERE boardId = :localId")
    void deleteByBoardId(long localId);
}