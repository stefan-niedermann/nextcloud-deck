package it.niedermann.nextcloud.deck.data.local.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import io.reactivex.rxjava3.core.Flowable
import it.niedermann.nextcloud.deck.data.local.entity.AttachmentEntity
import java.util.concurrent.CompletableFuture

@Dao
interface AttachmentDao : GenericDao<AttachmentEntity> {

    @Query("SELECT * FROM Attachment WHERE cardId = :cardId")
    fun getAttachmentsByCard(cardId: Long): Flowable<List<AttachmentEntity>>

    @Query("SELECT * FROM Attachment WHERE accountId = :accountId AND status != 1")
    fun getChangedAttachments(accountId: Long): CompletableFuture<List<AttachmentEntity>>

    @Query("SELECT * FROM Attachment WHERE accountId = :accountId AND remoteId = :remoteId")
    fun getAttachmentByRemoteId(accountId: Long, remoteId: Long): CompletableFuture<AttachmentEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplace(entity: AttachmentEntity): CompletableFuture<Long>

    @Query("DELETE FROM Attachment WHERE localId = :localId")
    fun deleteById(localId: Long): CompletableFuture<Void?>
}
