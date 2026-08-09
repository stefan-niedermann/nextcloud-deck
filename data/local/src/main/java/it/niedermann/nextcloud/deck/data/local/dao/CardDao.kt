package it.niedermann.nextcloud.deck.data.local.dao

import androidx.room3.Dao
import androidx.room3.Query
import io.reactivex.rxjava3.core.Flowable
import it.niedermann.nextcloud.deck.data.local.entity.CardEntity
import java.util.concurrent.CompletableFuture

@Dao
interface CardDao : GenericDao<CardEntity> {

    @Query("SELECT * FROM Card WHERE columnId = :columnId")
    fun getCardsByColumn(columnId: Long): Flowable<List<CardEntity>>

    @Query("SELECT * FROM Card WHERE accountId = :accountId AND remoteId = :remoteId")
    fun getCardByRemoteId(accountId: Long, remoteId: Long): CompletableFuture<CardEntity?>

    @Query("SELECT * FROM Card WHERE accountId = :accountId AND status != 1") // 1 = UP_TO_DATE
    fun getChangedCards(accountId: Long): CompletableFuture<List<CardEntity>>

    @Query("SELECT * FROM Card WHERE localId = :localId")
    fun getCardById(localId: Long): CompletableFuture<CardEntity?>

    @Query("DELETE FROM Card WHERE localId = :localId")
    fun deleteById(localId: Long): CompletableFuture<Void?>
}
