package it.niedermann.nextcloud.deck.data.local.dao

import androidx.room3.Dao
import androidx.room3.Query
import io.reactivex.rxjava3.core.Flowable
import it.niedermann.nextcloud.deck.data.local.entity.AccessControlEntity
import java.util.concurrent.CompletableFuture

@Dao
interface AccessControlDao : GenericDao<AccessControlEntity> {

    @Query("SELECT * FROM AccessControl WHERE boardId = :boardId")
    fun getAclByBoard(boardId: Long): Flowable<List<AccessControlEntity>>

    @Query("SELECT * FROM AccessControl WHERE accountId = :accountId AND status != 1")
    fun getChangedAcl(accountId: Long): CompletableFuture<List<AccessControlEntity>>

    @Query("DELETE FROM AccessControl WHERE localId = :localId")
    fun deleteById(localId: Long): CompletableFuture<Void?>
}
