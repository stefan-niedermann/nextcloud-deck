package it.niedermann.nextcloud.deck.data.local.dao

import androidx.room3.Dao
import androidx.room3.Query
import io.reactivex.rxjava3.core.Flowable
import it.niedermann.nextcloud.deck.data.local.entity.LabelEntity
import java.util.concurrent.CompletableFuture

@Dao
interface LabelDao : GenericDao<LabelEntity> {

    @Query("SELECT * FROM Label WHERE boardId = :boardId")
    fun getLabelsByBoard(boardId: Long): Flowable<List<LabelEntity>>

    @Query("SELECT * FROM Label WHERE accountId = :accountId AND remoteId = :remoteId")
    fun getLabelByRemoteId(accountId: Long, remoteId: Long): CompletableFuture<LabelEntity?>

    @Query("SELECT * FROM Label WHERE accountId = :accountId AND status != 1")
    fun getChangedLabels(accountId: Long): CompletableFuture<List<LabelEntity>>

    @Query("DELETE FROM Label WHERE localId = :localId")
    fun deleteById(localId: Long): CompletableFuture<Void?>
}
