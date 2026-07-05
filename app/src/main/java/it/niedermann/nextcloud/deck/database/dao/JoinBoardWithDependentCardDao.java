package it.niedermann.nextcloud.deck.database.dao;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import it.niedermann.nextcloud.deck.model.JoinCardWithDependentCard;

@Dao
public interface JoinBoardWithDependentCardDao extends GenericDao<JoinCardWithDependentCard> {

    @Query("DELETE FROM DependentCards WHERE  localCardId = :localCardId and status == 1")
    void deleteDependentsOfCard(long localCardId);
    @Query("select * FROM DependentCards WHERE  localCardId = :localCardId and dependentRemoteCardId = :remoteCardId")
    JoinCardWithDependentCard getDependentsOfCard(long localCardId, long remoteCardId);
    @Query("Update DependentCards set status = :status WHERE localCardId = :localCardId and dependentRemoteCardId = :dependantRemoteId")
    void setDbStatus(long localCardId, long dependantRemoteId, int status);

    @Query("DELETE FROM DependentCards WHERE  localCardId = :localCardId and dependentRemoteCardId = :dependentRemoteCardId")
    void deleteJoinedDependentForCardPhysically(Long localCardId, Long dependentRemoteCardId);

    @Query("select dc.* FROM DependentCards dc join card c on c.localId = dc.localCardId where c.accountId = :accountId and dc.status <> 1")
    List<JoinCardWithDependentCard> getAllChangedDependentJoinsForAccount(long accountId);
}