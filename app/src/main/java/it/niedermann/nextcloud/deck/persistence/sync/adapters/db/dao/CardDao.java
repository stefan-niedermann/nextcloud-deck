package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import java.util.List;

import it.niedermann.nextcloud.deck.model.Card;

@Dao
public interface CardDao extends GenericDao<Card> {

    @Query("SELECT * FROM card WHERE stackId = :localStackId")
    LiveData<List<Card>> getCardsForStack(final long localStackId);

    @Query("SELECT * FROM card WHERE accountId = :accountId and id = :remoteId")
    LiveData<Card> getCardByRemoteId(final long accountId, final long remoteId);

}