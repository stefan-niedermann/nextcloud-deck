package it.niedermann.nextcloud.deck.data.local.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import io.reactivex.rxjava3.core.Flowable
import it.niedermann.nextcloud.deck.data.local.entity.UserEntity
import java.util.concurrent.CompletableFuture

@Dao
interface UserDao : GenericDao<UserEntity> {

    @Query("SELECT * FROM User WHERE accountId = :accountId")
    fun getUsersByAccount(accountId: Long): Flowable<List<UserEntity>>

    @Query("SELECT * FROM User WHERE remoteId = :userId")
    fun getUserById(userId: String): Flowable<List<UserEntity>>

    @Query("SELECT * FROM User WHERE displayName LIKE '%' || :query || '%' OR remoteId LIKE '%' || :query || '%'")
    fun findUsers(query: String): Flowable<List<UserEntity>>

    @Query("SELECT * FROM User WHERE accountId = :accountId AND remoteId = :userId")
    fun getUserByRemoteId(accountId: Long, userId: String): CompletableFuture<UserEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplace(entity: UserEntity): CompletableFuture<Long>
}
