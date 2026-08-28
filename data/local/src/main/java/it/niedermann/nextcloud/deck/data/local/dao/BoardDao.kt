package it.niedermann.nextcloud.deck.data.local.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import io.reactivex.rxjava3.core.Flowable
import it.niedermann.nextcloud.deck.data.local.entity.BoardEntity
import java.util.concurrent.CompletableFuture

@Dao
interface BoardDao : GenericDao<BoardEntity> {

    @Query("SELECT * FROM Board WHERE accountId = :accountId AND status != 3 ORDER BY archived ASC, title ASC")
    fun getBoardsByAccount(accountId: Long): Flowable<List<BoardEntity>>

    @Query("SELECT * FROM Board WHERE accountId = :accountId")
    fun getAllBoardsByAccount(accountId: Long): CompletableFuture<List<BoardEntity>>

    @Query("SELECT * FROM Board WHERE accountId = :accountId AND remoteId = :remoteId")
    fun getBoardByRemoteId(accountId: Long, remoteId: Long): CompletableFuture<BoardEntity?>

    @Query("SELECT * FROM Board WHERE accountId = :accountId AND status != 1") // 1 = UP_TO_DATE
    fun getChangedBoards(accountId: Long): CompletableFuture<List<BoardEntity>>

    @Query("SELECT * FROM Board WHERE localId = :localId")
    fun getBoardById(localId: Long): CompletableFuture<BoardEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplace(entity: BoardEntity): CompletableFuture<Long>

    @Query("DELETE FROM Board WHERE localId = :localId")
    fun deleteById(localId: Long): CompletableFuture<Void?>
}
