package it.niedermann.nextcloud.deck.data.local.dao

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Update
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import it.niedermann.nextcloud.deck.data.local.entity.AccountEntity

@Dao
interface AccountDao {

    @Query("SELECT a.id FROM Account a WHERE a.accountName = :accountName")
    fun findAccountId(accountName: String): Maybe<Long>

    @Query("SELECT EXISTS(SELECT id FROM Account WHERE id != -1 LIMIT 1)")
    fun hasAccount(): Flowable<Boolean>

    @Query("SELECT EXISTS(SELECT id FROM Account WHERE id = :id)")
    fun accountExists(id: Long): Flowable<Boolean>

    @Query("SELECT * FROM Account a WHERE a.id = :id")
    fun getAccount(id: Long): Flowable<AccountEntity>

    @Query("SELECT * FROM Account a WHERE a.id = :id")
    fun getAccountSingle(id: Long): Maybe<AccountEntity>

    @Query("SELECT * FROM Account a WHERE a.id = :id")
    fun getAccountById(id: Long): java.util.concurrent.CompletableFuture<AccountEntity?>

    @Query("SELECT id FROM Account a WHERE id != -1 LIMIT 1")
    fun getAnyAccount(): Maybe<Long>

    @Query("SELECT * FROM Account WHERE id != -1")
    fun getAccounts(): Flowable<List<AccountEntity>>

    @Query("DELETE FROM Account WHERE Account.id = :id")
    fun deleteAccount(id: Long): Completable

    @Insert
    fun insert(entity: AccountEntity): Single<Long>

    @Update
    fun updateRx(entity: AccountEntity): Completable

    @Insert
    suspend fun insert(vararg entity: AccountEntity): List<Long>

    @Update
    suspend fun update(vararg entity: AccountEntity)

    @Delete
    suspend fun delete(vararg entity: AccountEntity)
}