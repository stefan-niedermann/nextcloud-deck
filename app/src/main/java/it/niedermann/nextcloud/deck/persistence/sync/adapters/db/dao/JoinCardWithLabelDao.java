package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao;

import androidx.room.Dao;
import androidx.room.Query;

import it.niedermann.nextcloud.deck.model.JoinCardWithLabel;

@Dao
public interface JoinCardWithLabelDao extends GenericDao<JoinCardWithLabel> {
    @Query("DELETE FROM joincardwithlabel WHERE  cardId = :localCardId")
    void deleteByCardId(long localCardId);
}