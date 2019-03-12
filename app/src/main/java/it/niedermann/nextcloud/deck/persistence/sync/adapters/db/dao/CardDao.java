package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import java.util.List;

import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.full.FullCard;

@Dao
public interface CardDao extends GenericDao<Card> {

    @Query("SELECT * FROM card WHERE stackId = :localStackId order by `order`, createdAt asc")
    LiveData<List<Card>> getCardsForStack(final long localStackId);

    @Query("SELECT * FROM card WHERE accountId = :accountId and id = :remoteId")
    LiveData<Card> getCardByRemoteId(final long accountId, final long remoteId);

    @Transaction
    @Query("SELECT * FROM card WHERE accountId = :accountId and id = :remoteId")
    FullCard getFullCardByRemoteIdDirectly(final long accountId, final long remoteId);

    @Transaction
    @Query("SELECT * FROM card WHERE accountId = :accountId AND stackId = :localStackId order by `order`, createdAt asc")
    LiveData<List<FullCard>> getFullCardsForStack(final long accountId, final long localStackId);

    @Transaction
    @Query("SELECT * FROM card WHERE accountId = :accountId AND stackId = :localStackId order by `order`, createdAt asc")
    List<FullCard> getFullCardsForStackDirectly(final long accountId, final long localStackId);

    @Transaction
    @Query("SELECT * FROM card WHERE accountId = :accountId and localId = :localCardId")
    LiveData<FullCard> getFullCardByLocalId(final long accountId, final long localCardId);

    @Transaction
    @Query("SELECT * FROM card WHERE accountId = :accountId and id = :remoteId")
    LiveData<FullCard> getFullCardByRemoteId(final long accountId, final long remoteId);

    @Query("SELECT * FROM card WHERE accountId = :accountId and id = :remoteId")
    Card getCardByRemoteIdDirectly(long accountId, long remoteId);
}