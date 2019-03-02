package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import java.util.List;

import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.full.FullBoard;

@Dao
public interface BoardDao extends GenericDao<Board> {

    @Query("SELECT * FROM board WHERE accountId = :accountId and deletedAt = 0 order by title asc")
    LiveData<List<Board>> getBoardsForAccount(final long accountId);

    @Query("SELECT * FROM board WHERE accountId = :accountId and id = :remoteId")
    LiveData<Board> getBoardByRemoteId(final long accountId, final long remoteId);

    @Query("SELECT * FROM board WHERE accountId = :accountId and id = :remoteId")
    Board getBoardByRemoteIdDirectly(long accountId, long remoteId);

    @Query("SELECT * FROM board WHERE accountId = :accountId and localId = :localId")
    Board getBoardByIdDirectly(long accountId, long localId);

    @Transaction
    @Query("SELECT * FROM board WHERE accountId = :accountId and id = :remoteId")
    FullBoard getFullBoardByRemoteIdDirectly(long accountId, long remoteId);

    @Query("SELECT * FROM board WHERE accountId = :accountId and localId = :localId")
    LiveData<FullBoard> getFullBoardById(final long accountId, final long localId);
}