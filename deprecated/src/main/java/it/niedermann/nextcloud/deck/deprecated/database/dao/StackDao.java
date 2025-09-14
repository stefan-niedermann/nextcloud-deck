package it.niedermann.nextcloud.deck.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.model.full.FullStack;

@Dao
public interface StackDao extends GenericDao<Stack> {

    @Query("SELECT * FROM stack WHERE accountId = :accountId AND boardId = :localBoardId and status<>3 and (deletedAt is null or deletedAt = 0) order by `order` asc")
    LiveData<List<Stack>> getStacksForBoard(final long accountId, final long localBoardId);

    @Query("SELECT * FROM stack WHERE accountId = :accountId and boardId = :localBoardId and id = :remoteId")
    LiveData<Stack> getStackByRemoteId(final long accountId, final long localBoardId, final long remoteId);

    @Query("SELECT * FROM stack WHERE localId = :localStackId")
    Stack getStackByLocalIdDirectly(final long localStackId);

    @Transaction
    @Query("SELECT * FROM stack WHERE localId = :localStackId")
    FullStack getFullStackByLocalIdDirectly(final long localStackId);

    @Transaction
    @Query("SELECT * FROM stack WHERE accountId = :accountId and boardId = :localBoardId and id = :remoteId")
    FullStack getFullStackByRemoteIdDirectly(final long accountId, final long localBoardId, final long remoteId);

    @Transaction
    @Query("SELECT * FROM stack WHERE accountId = :accountId and boardId = :localBoardId and id = :remoteId")
    LiveData<FullStack> getFullStackByRemoteId(final long accountId, final long localBoardId, final long remoteId);

    @Transaction
    @Query("SELECT * FROM stack WHERE accountId = :accountId and localId = :localId")
    LiveData<FullStack> getFullStack(long accountId, long localId);

    @Query("SELECT localId FROM stack WHERE accountId = :accountId")
    List<Long> getLocalStackIdsByAccountIdDirectly(long accountId);

    @Query("SELECT localId FROM stack WHERE boardId = :localBoardId")
    List<Long> getLocalStackIdsByLocalBoardIdDirectly(long localBoardId);

    @Transaction
    @Query("SELECT * FROM stack WHERE accountId = :accountId and boardId = :localBoardId and (status<>1 or id is null or lastModified <> lastModifiedLocal)")
    List<FullStack> getLocallyChangedStacksForBoardDirectly(long accountId, long localBoardId);

    @Transaction
    @Query("SELECT * FROM stack WHERE accountId = :accountId and (status<>1 or id is null or lastModified <> lastModifiedLocal)")
    List<FullStack> getLocallyChangedStacksDirectly(long accountId);

    @Transaction
    @Query("SELECT * FROM stack WHERE accountId = :accountId AND boardId = :localBoardId and status<>3 and (deletedAt is null or deletedAt = 0) order by `order` asc")
    List<FullStack> getFullStacksForBoardDirectly(long accountId, long localBoardId);

    @Query("SELECT localId FROM stack s WHERE accountId = :accountId and id = :stackId")
    Long getLocalStackIdByRemoteStackIdDirectly(long accountId, Long stackId);

    @Query("SELECT coalesce(MAX(`order`), -1) FROM stack s WHERE boardId = :localBoardId")
    Integer getHighestStackOrderInBoard(long localBoardId);

    @Query("SELECT exists(select 1 from Stack s join Board b on s.boardId = b.localId where s.localId = :localStackId and exists(select 1 from AccessControl ac where ac.boardId = b.localId and status <> 3))")
    boolean isStackOnSharedBoardDirectly(Long localStackId);

    @Query("SELECT s.localId FROM stack s join Board b on s.boardId = b.localId where b.archived <> 0 and b.accountId in (:accountIds)")
    List<Long> getLocalStackIdsInArchivedBoardsByAccountIdsDirectly(List<Long> accountIds);

    @Query("SELECT s.localId FROM stack s")
    List<Long>  getAllIDs();
}