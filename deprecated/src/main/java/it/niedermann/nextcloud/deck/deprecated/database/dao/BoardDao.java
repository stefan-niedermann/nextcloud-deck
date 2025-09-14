package it.niedermann.nextcloud.deck.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.full.FullBoard;

@Dao
public interface BoardDao extends GenericDao<Board> {

    @Transaction
    @Query("SELECT * FROM board WHERE accountId = :accountId and archived = :archived and (deletedAt = 0 or deletedAt is null) and status <> 3 order by title asc")
    LiveData<List<Board>> getNotDeletedBoards(long accountId, int archived);

    @Transaction
    @Query("SELECT * FROM board WHERE accountId = :accountId and archived = :archived and (deletedAt = 0 or deletedAt is null) and status <> 3 order by title asc")
    List<Board> getNotDeletedBoardsDirectly(long accountId, int archived);

    @Transaction
    @Query("SELECT * FROM board WHERE accountId = :accountId and archived = :archived and (deletedAt = 0 or deletedAt is null) and status <> 3 order by title asc")
    LiveData<List<FullBoard>> getNotDeletedFullBoards(long accountId, int archived);

    @Query("SELECT * FROM board WHERE accountId = :accountId and id = :remoteId")
    LiveData<Board> getBoardByRemoteId(final long accountId, final long remoteId);

    @Query("SELECT * FROM board WHERE accountId = :accountId and id = :remoteId")
    Board getBoardByRemoteIdDirectly(long accountId, long remoteId);

    @Query("SELECT * FROM board WHERE localId = :localId")
    Board getBoardByLocalIdDirectly(long localId);

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

    @Query("SELECT b.* FROM board b JOIN stack s ON s.boardId = b.localId JOIN card c ON s.localId = c.stackId where c.localId = :localCardId")
    Board getBoardByLocalCardIdDirectly(long localCardId);

    @Query("SELECT b.localId FROM board b JOIN stack s ON s.boardId = b.localId JOIN card c ON s.localId = c.stackId where c.localId = :localCardId")
    Long getBoardLocalIdByLocalCardIdDirectly(long localCardId);

    @Transaction
    @Query("SELECT b.* FROM board b JOIN stack s ON s.boardId = b.localId JOIN card c ON c.localId = :localCardId and c.stackId = s.localId")
    FullBoard getFullBoardByLocalCardIdDirectly(long localCardId);

    @Transaction
    @Query("SELECT * FROM board WHERE accountId = :accountId")
    List<FullBoard> getAllFullBoards(long accountId);

    @Query("SELECT * FROM board WHERE accountId = :accountId and archived = 0 and permissionEdit = 1 and (deletedAt = 0 or deletedAt is null) and status <> 3 order by title asc")
    LiveData<List<Board>> getBoardsWithEditPermissionsForAccount(long accountId);

    @Query("SELECT s.boardId " +
            "FROM card c " +
            "inner join stack s on s.localId = c.stackId " +
            "WHERE c.id = :cardRemoteId and c.accountId =  :accountId")
    LiveData<Long> getLocalBoardIdByCardRemoteIdAndAccountId(long cardRemoteId, long accountId);

    @Query("SELECT s.boardId " +
            "FROM card c " +
            "inner join stack s on s.localId = c.stackId " +
            "WHERE c.id = :cardRemoteId and c.accountId =  :accountId")
    Long getBoardLocalIdByAccountAndCardRemoteIdDirectly(long accountId, long cardRemoteId);

    @Query("SELECT count(*) FROM board WHERE accountId = :accountId and archived = 1 and (deletedAt = 0 or deletedAt is null) and status <> 3")
    LiveData<Integer> countArchivedBoards(long accountId);

    @Query("SELECT * FROM board WHERE accountId = :accountId and title = :title")
    Board getBoardForAccountByNameDirectly(long accountId, String title);

    @Query("SELECT b.color FROM board b where b.localId = :localBoardId and b.accountId = :accountId")
    Integer getBoardColorByLocalIdDirectly(long accountId, long localBoardId);

    @Query("SELECT b.color FROM board b where b.localId = :localBoardId and b.accountId = :accountId")
    LiveData<Integer> getBoardColor(long accountId, long localBoardId);
}