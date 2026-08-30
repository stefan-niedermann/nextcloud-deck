package it.niedermann.nextcloud.deck.data.local.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import io.reactivex.rxjava3.core.Flowable
import it.niedermann.nextcloud.deck.data.local.entity.LabelEntity
import java.util.concurrent.CompletableFuture

@Dao
interface LabelDao : GenericDao<LabelEntity> {

    @Query("SELECT * FROM Label WHERE boardId = :boardId AND status != 3 ORDER BY title ASC")
    fun getLabelsByBoard(boardId: Long): Flowable<List<LabelEntity>>

    @Query("SELECT * FROM Label WHERE boardId = :boardId")
    fun getLabelsByBoardSync(boardId: Long): CompletableFuture<List<LabelEntity>>

    @Query("SELECT * FROM Label WHERE accountId = :accountId AND remoteId = :remoteId")
    fun getLabelByRemoteId(accountId: Long, remoteId: Long): CompletableFuture<LabelEntity?>

    @Query("SELECT * FROM Label WHERE accountId = :accountId AND status != 1")
    fun getChangedLabels(accountId: Long): CompletableFuture<List<LabelEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplace(entity: LabelEntity): CompletableFuture<Long>

    @Query("DELETE FROM Label WHERE boardId = :boardId")
    fun deleteByBoardId(boardId: Long): CompletableFuture<Void?>

    @Query("DELETE FROM Label WHERE localId = :localId")
    fun deleteById(localId: Long): CompletableFuture<Void?>

    @Query("SELECT * FROM Label WHERE localId = :localId")
    fun getLabelById(localId: Long): CompletableFuture<LabelEntity?>

    @Query("SELECT * FROM Label WHERE localId = :localId")
    fun getLabelByIdRx(localId: Long): Flowable<LabelEntity>

    @Query("SELECT Label.* FROM Label INNER JOIN JoinCardWithLabel ON Label.localId = JoinCardWithLabel.labelId WHERE JoinCardWithLabel.cardId = :cardId")
    fun getLabelsByCard(cardId: Long): Flowable<List<LabelEntity>>

    @Query("SELECT * FROM Label WHERE title LIKE '%' || :userText || '%' AND status != 3 ORDER BY title ASC")
    fun find(userText: String): Flowable<List<LabelEntity>>
}
