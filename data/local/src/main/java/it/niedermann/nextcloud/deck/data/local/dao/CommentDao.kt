package it.niedermann.nextcloud.deck.data.local.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import io.reactivex.rxjava3.core.Flowable
import it.niedermann.nextcloud.deck.data.local.entity.CommentEntity
import java.util.concurrent.CompletableFuture

@Dao
interface CommentDao : GenericDao<CommentEntity> {

    @Query("SELECT * FROM Comment WHERE cardId = :cardId")
    fun getCommentsByCard(cardId: Long): Flowable<List<CommentEntity>>

    @Query("SELECT * FROM Comment WHERE cardId = :cardId")
    fun getCommentsByCardRx(cardId: Long): CompletableFuture<List<CommentEntity>>

    @Query("SELECT * FROM Comment WHERE accountId = :accountId AND status != 1")
    fun getChangedComments(accountId: Long): CompletableFuture<List<CommentEntity>>

    @Query("SELECT * FROM Comment WHERE accountId = :accountId AND remoteId = :remoteId")
    fun getCommentByRemoteId(accountId: Long, remoteId: Long): CompletableFuture<CommentEntity?>

    @Query("SELECT * FROM Comment WHERE localId = :localId")
    fun getCommentByLocalId(localId: Long): CompletableFuture<CommentEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplace(entity: CommentEntity): CompletableFuture<Long>

    @Query("DELETE FROM Comment WHERE localId = :localId")
    fun deleteById(localId: Long): CompletableFuture<Void?>
}
