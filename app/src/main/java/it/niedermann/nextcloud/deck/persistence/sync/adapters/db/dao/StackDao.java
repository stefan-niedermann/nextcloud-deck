package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.model.full.FullStack;

@Dao
public interface StackDao extends GenericDao<Stack> {

    @Query("SELECT * FROM stack WHERE accountId = :accountId AND boardId = :localBoardId")
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
    @Query("SELECT * FROM stack WHERE accountId = :accountId AND boardId = :localBoardId")
    LiveData<List<FullStack>> getFullStacksForBoard(final long accountId, final long localBoardId);

    @Transaction
    @Query("SELECT * FROM stack WHERE accountId = :accountId and boardId = :localBoardId and id = :remoteId")
    LiveData<FullStack> getFullStackByRemoteId(final long accountId, final long localBoardId, final long remoteId);

    @Transaction
    @Query("SELECT * FROM stack WHERE accountId = :accountId and localId = :localId")
    LiveData<FullStack> getFullStack(long accountId, long localId);

    @Transaction
    @Query("SELECT * FROM stack WHERE accountId = :accountId and (status<>1 or id is null or lastModified <> lastModifiedLocal)")
    List<FullStack> getLocallyChangedStacksDirectly(long accountId);
}