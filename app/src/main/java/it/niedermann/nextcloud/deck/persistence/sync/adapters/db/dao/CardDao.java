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
import it.niedermann.nextcloud.deck.model.full.FullCardWithProjects;

@Dao
public interface CardDao extends GenericDao<Card> {

    String QUERY_UPCOMING_CARDS = "SELECT c.* FROM card c " +
                "join stack s on s.localId = c.stackId " +
                "join board b on b.localId = s.boardId " +
            "WHERE b.archived = 0 and c.archived = 0 and b.status <> 3 and s.status <> 3 and c.status <> 3 " +
                // FUll Logic: (hasDueDate AND isIn_PRIVATE_Board) OR (isInSharedBoard AND (assignedToMe OR (hasDueDate AND noAssignees)))
                "and (" +
                    "(c.dueDate is not null AND NOT exists(select 1 from AccessControl ac where ac.boardId = b.localId and ac.status <> 3))" + //(hasDueDate AND isInPrivateBoard)
                    "OR (" +
                        "exists(select 1 from AccessControl ac where ac.boardId = b.localId and ac.status <> 3) " + //OR (isInSharedBoard AND
                        "AND (" +
                            "(c.dueDate is not null AND not exists(select 1 from JoinCardWithUser j where j.cardId = c.localId)) " + // hasDueDate AND noAssignees OR
                            "OR exists(select 1 from JoinCardWithUser j where j.cardId = c.localId and j.userId in (select u.localId from user u where u.uid in (select a.userName from Account a)))" + //(assignedToMe
                        ")" +
                    ")" +
                ")" +
            "ORDER BY c.dueDate asc";

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
    @Query("SELECT * FROM card WHERE accountId = :accountId AND archived = 0 AND stackId = :localStackId and status<>3 order by `order`, createdAt asc")
    LiveData<List<FullCard>> getFullCardsForStack(final long accountId, final long localStackId);

    @Transaction
    @RawQuery(observedEntities = Card.class)
    LiveData<List<FullCard>> getFilteredFullCardsForStack(SupportSQLiteQuery query);

    @Transaction
    @RawQuery(observedEntities = Card.class)
    List<FullCard> getFilteredFullCardsForStackDirectly(SupportSQLiteQuery query);

    @Transaction
    @Query("SELECT * FROM card WHERE accountId = :accountId AND stackId = :localStackId order by `order`, createdAt asc")
    List<FullCard> getFullCardsForStackDirectly(final long accountId, final long localStackId);

    @Transaction
    @Query("SELECT * FROM card WHERE accountId = :accountId and localId = :localCardId")
    LiveData<FullCard> getFullCardByLocalId(final long accountId, final long localCardId);
    @Transaction
    @Query("SELECT * FROM card WHERE accountId = :accountId and localId = :localCardId")
    LiveData<FullCardWithProjects> getFullCardWithProjectsByLocalId(final long accountId, final long localCardId);

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

    @Query("SELECT * FROM card c WHERE stackId = :localStackId and exists ( select 1 from DeckComment dc where dc.objectId = c.localId and dc.status<>1)")
    List<Card> getCardsWithLocallyChangedCommentsForStackDirectly(Long localStackId);

    @Query("SELECT count(*) FROM card c WHERE accountId = :accountId and stackId = :localStackId and status <> 3")
    int countCardsInStackDirectly(long accountId, long localStackId);

    @Query("SELECT coalesce(MAX(`order`), -1) FROM card c WHERE  stackId = :localStackId and status <> 3")
    Integer getHighestOrderInStack(Long localStackId);

    @Query("SELECT c.stackId FROM card c WHERE  localId = :localCardId")
    Long getLocalStackIdByLocalCardId(Long localCardId);

    @Transaction
    @Query("SELECT * FROM card c WHERE " +
            "exists(select 1 from Stack s join Board b on s.boardId = b.localId where s.localId = c.stackId " +
            "and b.archived = 0 " +
            "and not exists(select 1 from AccessControl ac where ac.boardId = b.localId and status <> 3)) " +
            "and dueDate is not null " +
            "and (coalesce(:accountIds, null) is null or accountId in (:accountIds)) " +
            "and status <> 3 " +
            "and archived = 0")
    List<FullCard> getFullCardsForNonSharedBoardsWithDueDateForUpcomingCardsWidgetDirectly(List<Long> accountIds);

    @Transaction
    @Query(QUERY_UPCOMING_CARDS)
    LiveData<List<FullCard>> getUpcomingCards();

    @Transaction
    @Query(QUERY_UPCOMING_CARDS)
    List<FullCard> getUpcomingCardsDirectly();
}