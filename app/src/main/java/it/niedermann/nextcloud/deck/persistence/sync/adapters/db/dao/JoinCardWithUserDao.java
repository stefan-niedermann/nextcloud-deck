package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao;

import androidx.room.Dao;
import androidx.room.Query;
import it.niedermann.nextcloud.deck.model.JoinCardWithUser;

@Dao
public interface JoinCardWithUserDao extends GenericDao<JoinCardWithUser> {
    @Query("DELETE FROM joincardwithuser WHERE cardId = :localId and status=1") // 1 = up_to_date
    void deleteByCardId(long localId);

    @Query("Update joincardwithuser set status = :status WHERE cardId = :localCardId and userId = :localUserId")
    void setDbStatus(long localCardId, long localUserId, int status);
}