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

    @Query("SELECT * FROM Card WHERE columnId = :columnId AND status != 3 ORDER BY `order` ASC")
    fun getCardsByColumn(columnId: Long): Flowable<List<CardEntity>>

    @Query("SELECT * FROM Card WHERE columnId = :columnId")
    fun getCardsByColumnRx(columnId: Long): CompletableFuture<List<CardEntity>>

    @Query("SELECT * FROM Card WHERE columnId = :columnId")
    fun getCardsByColumnSync(columnId: Long): CompletableFuture<List<CardEntity>>

    @Query("SELECT Card.* FROM Card INNER JOIN Column ON Card.columnId = Column.localId WHERE Column.boardId = :boardId AND Card.status != 3")
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

    @Query("SELECT done FROM Card WHERE localId = :localCardId")
    fun getDoneStateOfCard(localCardId: Long): CompletableFuture<java.time.OffsetDateTime?>

    @Query("UPDATE Card SET done = :done, status = :status WHERE localId = :localCardId")
    fun setDoneStateOfCard(localCardId: Long, done: java.time.OffsetDateTime?, status: Int): CompletableFuture<Void?>

    @Query("SELECT Board.remoteId FROM Card JOIN Column ON Column.localId = Card.columnId JOIN Board ON Board.localId = Column.boardId WHERE Card.localId = :localId")
    fun getBoardRemoteIdByLocalId(localId: Long): CompletableFuture<Long?>

    @Query("SELECT Column.remoteId FROM Card JOIN Column ON Column.localId = Card.columnId WHERE Card.localId = :localId")
    fun getStackRemoteIdByLocalId(localId: Long): CompletableFuture<Long?>
}
