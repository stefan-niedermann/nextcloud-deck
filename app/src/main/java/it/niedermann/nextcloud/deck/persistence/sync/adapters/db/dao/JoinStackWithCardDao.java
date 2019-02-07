package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import it.niedermann.nextcloud.deck.model.JoinStackWithCard;

@Dao
public interface JoinStackWithCardDao extends GenericDao<JoinStackWithCard> {
    @Query("DELETE FROM joinstackwithcard WHERE stackId = :localStackId")
    void deleteByStackId(long localStackId);

    @Query("DELETE FROM joinstackwithcard WHERE cardId = :localCardId")
    void deleteByCardId(long localCardId);
}

