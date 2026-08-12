package it.niedermann.nextcloud.deck.data.local.dao

import androidx.room3.Dao
import androidx.room3.Query
import it.niedermann.nextcloud.deck.data.local.entity.JoinBoardWithUserEntity
import java.util.concurrent.CompletableFuture

@Dao
interface JoinBoardWithUserDao : GenericDao<JoinBoardWithUserEntity> {
    @Query("DELETE FROM JoinBoardWithUser WHERE boardId = :boardId")
    fun deleteByBoardId(boardId: Long): CompletableFuture<Void?>
}
