package it.niedermann.nextcloud.deck.data.local.dao

import androidx.room3.Dao
import androidx.room3.Query
import it.niedermann.nextcloud.deck.data.local.entity.JoinBoardWithPermissionEntity
import java.util.concurrent.CompletableFuture

@Dao
interface JoinBoardWithPermissionDao : GenericDao<JoinBoardWithPermissionEntity> {
    @Query("DELETE FROM JoinBoardWithPermission WHERE boardId = :boardId")
    fun deleteByBoardId(boardId: Long): CompletableFuture<Void?>
}
