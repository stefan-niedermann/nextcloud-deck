package it.niedermann.nextcloud.deck.data.local.dao

import androidx.room3.Dao
import androidx.room3.Query
import it.niedermann.nextcloud.deck.data.local.entity.JoinBoardWithLabelEntity
import java.util.concurrent.CompletableFuture

@Dao
interface JoinBoardWithLabelDao : GenericDao<JoinBoardWithLabelEntity> {
    @Query("DELETE FROM JoinBoardWithLabel WHERE boardId = :boardId")
    fun deleteByBoardId(boardId: Long): CompletableFuture<Void?>
}
