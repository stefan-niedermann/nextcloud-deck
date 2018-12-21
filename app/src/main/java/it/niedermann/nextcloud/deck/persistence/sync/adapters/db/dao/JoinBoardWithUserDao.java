package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import it.niedermann.nextcloud.deck.model.JoinBoardWithUser;

@Dao
public interface JoinBoardWithUserDao extends GenericDao<JoinBoardWithUser> {
    @Query("DELETE FROM joinboardwithuser WHERE boardId = :localId")
    void deleteByBoardId(long localId);

}