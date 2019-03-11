package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao;

import androidx.room.Dao;
import androidx.room.Query;

import it.niedermann.nextcloud.deck.model.JoinCardWithUser;

@Dao
public interface JoinCardWithUserDao extends GenericDao<JoinCardWithUser> {
    @Query("DELETE FROM joincardwithuser WHERE cardId = :localId")
    void deleteByCardId(long localId);

}