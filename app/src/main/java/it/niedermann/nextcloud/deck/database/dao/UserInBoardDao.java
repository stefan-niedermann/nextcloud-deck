package it.niedermann.nextcloud.deck.database.dao;

import androidx.room.Dao;
import androidx.room.Query;

import it.niedermann.nextcloud.deck.model.relations.UserInBoard;

@Dao
public interface UserInBoardDao extends GenericDao<UserInBoard> {
    @Query("DELETE FROM userinboard WHERE boardId = :localId")
    void deleteByBoardId(long localId);
}