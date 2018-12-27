package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import java.util.List;

import it.niedermann.nextcloud.deck.model.Board;

@Dao
public interface BoardDao extends GenericDao<Board> {

    @Query("SELECT * FROM board WHERE accountId = :accountId")
    LiveData<List<Board>> getBoardsForAccount(final long accountId);

    @Query("SELECT * FROM board WHERE accountId = :accountId and id = :remoteId")
    LiveData<Board> getBoardByRemoteId(final long accountId, final long remoteId);

    @Query("SELECT * FROM board WHERE accountId = :accountId and id = :remoteId")
    Board getBoardByRemoteIdDirectly(long accountId, long remoteId);
}