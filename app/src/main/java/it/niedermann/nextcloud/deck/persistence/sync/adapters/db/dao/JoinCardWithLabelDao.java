package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import it.niedermann.nextcloud.deck.model.JoinCardWithLabel;

@Dao
public interface JoinCardWithLabelDao extends GenericDao<JoinCardWithLabel> {
    @Query("DELETE FROM joincardwithlabel WHERE cardId = :localId")
    void deleteByCardId(long localId);
}