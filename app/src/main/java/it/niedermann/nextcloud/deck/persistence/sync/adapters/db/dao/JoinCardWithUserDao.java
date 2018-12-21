package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import it.niedermann.nextcloud.deck.model.JoinCardWithUser;

@Dao
public interface JoinCardWithUserDao extends GenericDao<JoinCardWithUser> {
    @Query("DELETE FROM joincardwithuser WHERE cardId = :localId")
    void deleteByCardId(long localId);

}