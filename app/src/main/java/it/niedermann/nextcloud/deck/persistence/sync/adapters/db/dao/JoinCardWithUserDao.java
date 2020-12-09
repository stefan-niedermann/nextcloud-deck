package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import it.niedermann.nextcloud.deck.model.JoinCardWithUser;

@Dao
public interface JoinCardWithUserDao extends GenericDao<JoinCardWithUser> {
    @Query("DELETE FROM joincardwithuser WHERE cardId = :localId and status=1") // 1 = UP_TO_DATE
    void deleteByCardId(long localId);

    @Query("Update joincardwithuser set status = :status WHERE cardId = :localCardId and userId = :localUserId")
    void setDbStatus(long localCardId, long localUserId, int status);

    @Query("DELETE FROM joincardwithuser WHERE cardId = :localCardId and userId = :localUserId")
    void deleteByCardIdAndUserIdPhysically(long localCardId, long localUserId);

    @Query("select * FROM joincardwithuser WHERE cardId = :localCardId and userId = :localUserId")
    JoinCardWithUser getJoin(Long localUserId, Long localCardId);

    @Query("select u.localId as userId, c.id as cardId, j.status from joincardwithuser j " +
            "inner join card c on j.cardId = c.localId " +
            "inner join user u on j.userId = u.localId " +
            "WHERE j.status <> 1") // not UP_TO_DATE
    List<JoinCardWithUser> getChangedJoinsWithRemoteIDs();

    @Query("select u.localId as userId, c.id as cardId, j.status from joincardwithuser j " +
            "inner join card c on j.cardId = c.localId " +
            "inner join user u on j.userId = u.localId " +
            "WHERE c.stackId = :localStackId " +
            "AND j.status <> 1") // not UP_TO_DATE
    List<JoinCardWithUser> getChangedJoinsWithRemoteIDsForStack(Long localStackId);

    @Query("delete from joincardwithuser " +
            "where cardId = (select c.localId from card c where c.accountId = :accountId and c.id = :remoteCardId) " +
            "and userId = (select u.localId from user u where u.accountId = :accountId and u.uid = :userUid)")
    void deleteJoinedUserForCardPhysicallyByRemoteIDs(Long accountId, Long remoteCardId, String userUid);

    @Query("select userId from joincardwithuser WHERE cardId = :localCardId and userId IN (:assignedUserIDs) and status <> 3") // not LOCAL_DELETED
    List<Long> filterDeleted(long localCardId, List<Long> assignedUserIDs);
}