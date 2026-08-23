package it.niedermann.nextcloud.deck.data.local.dao

import androidx.room3.Dao
import androidx.room3.Query
import it.niedermann.nextcloud.deck.data.local.entity.JoinCardWithDependentCardEntity
import java.util.concurrent.CompletableFuture

@Dao
interface JoinCardWithDependentCardDao : GenericDao<JoinCardWithDependentCardEntity> {

    @Query("DELETE FROM JoinCardWithDependentCard WHERE cardId = :cardId AND status = 1")
    fun deleteDependentsOfCard(cardId: Long): CompletableFuture<Void?>

    @Query("SELECT * FROM JoinCardWithDependentCard WHERE cardId = :cardId AND dependentRemoteId = :remoteId")
    fun getJoin(cardId: Long, remoteId: Long): CompletableFuture<JoinCardWithDependentCardEntity?>

    @Query("UPDATE JoinCardWithDependentCard SET status = :status WHERE cardId = :cardId AND dependentRemoteId = :dependantRemoteId")
    fun setStatus(cardId: Long, dependantRemoteId: Long, status: Int): CompletableFuture<Void?>

    @Query("DELETE FROM JoinCardWithDependentCard WHERE cardId = :cardId AND dependentRemoteId = :dependentRemoteId")
    fun deletePhysically(cardId: Long, dependentRemoteId: Long): CompletableFuture<Void?>

    @Query("SELECT dc.* FROM JoinCardWithDependentCard dc JOIN Card c ON c.localId = dc.cardId WHERE c.accountId = :accountId AND dc.status != 1")
    fun getChangedJoinsForAccount(accountId: Long): CompletableFuture<List<JoinCardWithDependentCardEntity>>

    @Query("DELETE FROM JoinCardWithDependentCard WHERE cardId = :cardId")
    fun deleteByCardId(cardId: Long): CompletableFuture<Void?>
}
