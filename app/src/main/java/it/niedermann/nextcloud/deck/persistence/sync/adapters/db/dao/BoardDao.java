package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.full.FullBoard;

@Dao
public interface BoardDao extends GenericDao<Board> {

    @Query("SELECT * FROM board WHERE accountId = :accountId and (deletedAt = 0 or deletedAt is null) and status <> 3 order by title asc")
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
    @Query("SELECT * FROM board WHERE accountId = :accountId and localId = :localId")
    FullBoard getFullBoardByLocalIdDirectly(long accountId, long localId);

    @Transaction
    @Query("SELECT * FROM board WHERE accountId = :accountId and (status<>1 or id is null or lastModified <> lastModifiedLocal)")
    List<FullBoard> getLocallyChangedBoardsDirectly(long accountId);

    @Transaction
    @Query("SELECT * FROM board WHERE accountId = :accountId and localId = :localId")
    LiveData<FullBoard> getFullBoardById(final long accountId, final long localId);


    @Query("SELECT b.* FROM board b JOIN stack s ON s.boardId = b.localId JOIN card c ON c.localId = :localCardId")
    Board getBoardByLocalCardIdDirectly(long localCardId);
}