package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import it.niedermann.nextcloud.deck.model.JoinBoardWithLabel;

@Dao
public interface JoinBoardWithLabelDao extends GenericDao<JoinBoardWithLabel> {
    @Query("DELETE FROM joinboardwithlabel WHERE boardId = :localId")
    void deleteByBoardId(long localId);
}