package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.Transaction;
import androidx.sqlite.db.SupportSQLiteQuery;

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

    @Query("SELECT * FROM card WHERE accountId = :accountId and localId = :localId")
    Card getCardByLocalIdDirectly(final long accountId, final long localId);

    @Transaction
    @Query("SELECT * FROM card WHERE accountId = :accountId and localId = :localId")
    FullCard getFullCardByLocalIdDirectly(final long accountId, final long localId);

    @Transaction                                                                                // v not deleted!
    @Query("SELECT * FROM card WHERE accountId = :accountId AND stackId = :localStackId and status<>3 order by `order`, createdAt asc")
    LiveData<List<FullCard>> getFullCardsForStack(final long accountId, final long localStackId);

    @Transaction                                                                                // v not deleted!
    @RawQuery
    LiveData<List<FullCard>> getFilteredFullCardsForStack(SupportSQLiteQuery query);

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

    @Transaction
    @Query("SELECT * FROM card WHERE accountId = :accountId and (status<>1 or id is null or lastModified <> lastModifiedLocal)")
    List<FullCard> getLocallyChangedCardsDirectly(long accountId);

    @Transaction
    @Query("SELECT * FROM card WHERE accountId = :accountId and stackId = :localStackId and (status<>1 or id is null or lastModified <> lastModifiedLocal)")
    List<FullCard> getLocallyChangedCardsByLocalStackIdDirectly(long accountId, long localStackId);

    @Query("SELECT * FROM card c WHERE accountId = :accountId and exists ( select 1 from DeckComment dc where dc.objectId = c.localId and dc.status<>1)")
    List<Card> getCardsWithLocallyChangedCommentsDirectly(Long accountId);
}