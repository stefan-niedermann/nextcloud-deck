package it.niedermann.nextcloud.deck.data.local.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import io.reactivex.rxjava3.core.Flowable
import it.niedermann.nextcloud.deck.data.local.entity.ActivityEntity
import java.util.concurrent.CompletableFuture

@Dao
interface ActivityDao : GenericDao<ActivityEntity> {

    @Query("SELECT * FROM Activity WHERE cardId = :cardId")
    fun getActivitiesByCard(cardId: Long): Flowable<List<ActivityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplace(entity: ActivityEntity): CompletableFuture<Long>
}
