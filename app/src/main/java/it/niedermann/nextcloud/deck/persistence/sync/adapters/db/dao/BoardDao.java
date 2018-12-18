package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;
import java.util.Set;

import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.User;

@Dao
public interface BoardDao extends GenericDao<Board> {

    @Query("SELECT * FROM board WHERE accountId = :accountId")
    Set<Board> getBoardsForAccount(final long accountId);

    @Query("SELECT * FROM board WHERE accountId = :accountId and id = :remoteId")
    Board getBoardByRemoteId(final long accountId, final long remoteId);

}