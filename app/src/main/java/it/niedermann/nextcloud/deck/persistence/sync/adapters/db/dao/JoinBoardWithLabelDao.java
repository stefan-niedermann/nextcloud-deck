package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao;

import androidx.room.Dao;
import androidx.room.Query;

import it.niedermann.nextcloud.deck.model.JoinBoardWithLabel;

@Dao
public interface JoinBoardWithLabelDao extends GenericDao<JoinBoardWithLabel> {
    @Query("DELETE FROM joinboardwithlabel WHERE boardId = :localId")
    void deleteByBoardId(long localId);
}