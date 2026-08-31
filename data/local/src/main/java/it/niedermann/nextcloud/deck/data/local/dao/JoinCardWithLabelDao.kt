package it.niedermann.nextcloud.deck.data.local.dao

import androidx.room3.Dao
import androidx.room3.Query
import it.niedermann.nextcloud.deck.data.local.entity.JoinCardWithLabelEntity
import java.util.concurrent.CompletableFuture

@Dao
interface JoinCardWithLabelDao : GenericDao<JoinCardWithLabelEntity> {
    @Query("DELETE FROM JoinCardWithLabel WHERE cardId = :cardId")
    fun deleteByCardId(cardId: Long): CompletableFuture<Void?>

    @Query("DELETE FROM JoinCardWithLabel WHERE cardId = :cardId AND labelId = :labelId")
    fun deleteByCardIdAndLabelId(cardId: Long, labelId: Long): CompletableFuture<Void?>

    @Query("SELECT * FROM JoinCardWithLabel WHERE cardId = :cardId AND status != 3")
    fun getActiveJoinsByCardId(cardId: Long): CompletableFuture<List<JoinCardWithLabelEntity>>

    @Query("SELECT * FROM JoinCardWithLabel WHERE cardId = :cardId")
    fun getJoinsByCardId(cardId: Long): CompletableFuture<List<JoinCardWithLabelEntity>>

    @Query("SELECT * FROM JoinCardWithLabel WHERE cardId = :cardId AND labelId = :labelId")
    fun getJoin(cardId: Long, labelId: Long): CompletableFuture<JoinCardWithLabelEntity?>

    @Query("SELECT JoinCardWithLabel.* FROM JoinCardWithLabel INNER JOIN Card ON JoinCardWithLabel.cardId = Card.localId WHERE Card.accountId = :accountId AND JoinCardWithLabel.status != 1")
    fun getChangedJoinsForAccount(accountId: Long): CompletableFuture<List<JoinCardWithLabelEntity>>

    @Query("UPDATE JoinCardWithLabel SET status = 3 WHERE cardId = :cardId AND status != 3")
    fun softDeleteByCardId(cardId: Long): CompletableFuture<Void?>

    @Query("DELETE FROM JoinCardWithLabel WHERE cardId = :cardId AND labelId = :labelId")
    fun deletePhysically(cardId: Long, labelId: Long): CompletableFuture<Void?>
}
