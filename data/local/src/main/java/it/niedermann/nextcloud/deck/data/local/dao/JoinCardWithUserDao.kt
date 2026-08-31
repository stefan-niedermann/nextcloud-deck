package it.niedermann.nextcloud.deck.data.local.dao

import androidx.room3.Dao
import androidx.room3.Query
import it.niedermann.nextcloud.deck.data.local.entity.JoinCardWithUserEntity
import java.util.concurrent.CompletableFuture

@Dao
interface JoinCardWithUserDao : GenericDao<JoinCardWithUserEntity> {
    @Query("DELETE FROM JoinCardWithUser WHERE cardId = :cardId")
    fun deleteByCardId(cardId: Long): CompletableFuture<Void?>

    @Query("DELETE FROM JoinCardWithUser WHERE cardId = :cardId AND userId = :userId")
    fun deleteByCardIdAndUserId(cardId: Long, userId: Long): CompletableFuture<Void?>

    @Query("SELECT * FROM JoinCardWithUser WHERE cardId = :cardId AND status != 3")
    fun getActiveJoinsByCardId(cardId: Long): CompletableFuture<List<JoinCardWithUserEntity>>

    @Query("SELECT * FROM JoinCardWithUser WHERE cardId = :cardId")
    fun getJoinsByCardId(cardId: Long): CompletableFuture<List<JoinCardWithUserEntity>>

    @Query("SELECT * FROM JoinCardWithUser WHERE cardId = :cardId AND userId = :userId")
    fun getJoin(cardId: Long, userId: Long): CompletableFuture<JoinCardWithUserEntity?>

    @Query("SELECT JoinCardWithUser.* FROM JoinCardWithUser INNER JOIN Card ON JoinCardWithUser.cardId = Card.localId WHERE Card.accountId = :accountId AND JoinCardWithUser.status != 1")
    fun getChangedJoinsForAccount(accountId: Long): CompletableFuture<List<JoinCardWithUserEntity>>

    @Query("UPDATE JoinCardWithUser SET status = 3 WHERE cardId = :cardId AND status != 3")
    fun softDeleteByCardId(cardId: Long): CompletableFuture<Void?>

    @Query("DELETE FROM JoinCardWithUser WHERE cardId = :cardId AND userId = :userId")
    fun deletePhysically(cardId: Long, userId: Long): CompletableFuture<Void?>
}
