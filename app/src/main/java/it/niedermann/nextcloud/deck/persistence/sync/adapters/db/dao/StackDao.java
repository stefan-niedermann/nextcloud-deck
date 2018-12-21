package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import java.util.List;

import it.niedermann.nextcloud.deck.model.Stack;

@Dao
public interface StackDao extends GenericDao<Stack> {

    @Query("SELECT * FROM stack WHERE accountId = :accountId AND boardId = :localBoardId")
    LiveData<List<Stack>> getStacksForBoard(final long accountId, final long localBoardId);

    @Query("SELECT * FROM stack WHERE accountId = :accountId and boardId = :localBoardId and id = :remoteId")
    LiveData<Stack> getStackByRemoteId(final long accountId, final long localBoardId, final long remoteId);

}