package it.niedermann.nextcloud.deck.data.local.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import io.reactivex.rxjava3.core.Flowable
import it.niedermann.nextcloud.deck.data.local.entity.CardEntity
import java.util.concurrent.CompletableFuture

@Dao
interface CardDao : GenericDao<CardEntity> {

    @Query("SELECT * FROM Card WHERE columnId = :columnId ORDER BY `order` ASC")
    fun getCardsByColumn(columnId: Long): Flowable<List<CardEntity>>

    @Query("SELECT Card.* FROM Card INNER JOIN Column ON Card.columnId = Column.localId WHERE Column.boardId = :boardId")
    fun getCardsByBoard(boardId: Long): Flowable<List<CardEntity>>

    @Query("SELECT * FROM Card WHERE accountId = :accountId AND remoteId = :remoteId")
    fun getCardByRemoteId(accountId: Long, remoteId: Long): CompletableFuture<CardEntity?>

    @Query("SELECT * FROM Card WHERE accountId = :accountId AND status != 1") // 1 = UP_TO_DATE
    fun getChangedCards(accountId: Long): CompletableFuture<List<CardEntity>>

    @Query("SELECT * FROM Card WHERE localId = :localId")
    fun getCardById(localId: Long): CompletableFuture<CardEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplace(entity: CardEntity): CompletableFuture<Long>

    @Query("DELETE FROM Card WHERE localId = :localId")
    fun deleteById(localId: Long): CompletableFuture<Void?>
}
