package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;
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

    @Query("SELECT * FROM board WHERE localId = :localId")
    Board getBoardByIdDirectly(long localId);

    @Transaction
    @Query("SELECT * FROM board WHERE accountId = :accountId and id = :remoteId")
    FullBoard getFullBoardByRemoteIdDirectly(long accountId, long remoteId);

    @Transaction
    @Query("SELECT * FROM board WHERE accountId = :accountId and (id is null or lastModified <> lastModifiedLocal)")
    FullBoard getLocallyChangedBoardsDirectly(long accountId);

    @Transaction
    @Query("SELECT * FROM board WHERE accountId = :accountId and localId = :localId")
    LiveData<FullBoard> getFullBoardById(final long accountId, final long localId);


}