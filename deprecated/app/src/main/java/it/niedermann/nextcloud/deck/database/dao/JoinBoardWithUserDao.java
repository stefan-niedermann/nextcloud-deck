package it.niedermann.nextcloud.deck.database.dao;

import androidx.room.Dao;
import androidx.room.Query;

import it.niedermann.nextcloud.deck.model.JoinBoardWithUser;

@Dao
public interface JoinBoardWithUserDao extends GenericDao<JoinBoardWithUser> {
    @Query("DELETE FROM joinboardwithuser WHERE boardId = :localId")
    void deleteByBoardId(long localId);

}