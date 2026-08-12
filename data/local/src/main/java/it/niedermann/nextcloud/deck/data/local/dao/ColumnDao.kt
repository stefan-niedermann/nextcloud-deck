package it.niedermann.nextcloud.deck.data.local.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import io.reactivex.rxjava3.core.Flowable
import it.niedermann.nextcloud.deck.data.local.entity.ColumnEntity
import java.util.concurrent.CompletableFuture

@Dao
interface ColumnDao : GenericDao<ColumnEntity> {

    @Query("SELECT * FROM `Column` WHERE boardId = :boardId ORDER BY `order` ASC")
    fun getColumnsByBoard(boardId: Long): Flowable<List<ColumnEntity>>

    @Query("SELECT * FROM `Column` WHERE accountId = :accountId AND remoteId = :remoteId")
    fun getColumnByRemoteId(accountId: Long, remoteId: Long): CompletableFuture<ColumnEntity?>

    @Query("SELECT * FROM `Column` WHERE accountId = :accountId AND status != 1")
    fun getChangedColumns(accountId: Long): CompletableFuture<List<ColumnEntity>>

    @Query("SELECT * FROM `Column` WHERE localId = :localId")
    fun getColumnById(localId: Long): CompletableFuture<ColumnEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplace(entity: ColumnEntity): CompletableFuture<Long>

    @Query("DELETE FROM `Column` WHERE boardId = :boardId")
    fun deleteByBoardId(boardId: Long): CompletableFuture<Void?>

    @Query("DELETE FROM `Column` WHERE localId = :localId")
    fun deleteById(localId: Long): CompletableFuture<Void?>
}
