package it.niedermann.nextcloud.deck.data.local.dao

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Update
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import it.niedermann.nextcloud.deck.data.local.entity.AccountEntity

@Dao
interface AccountDao {

    @Query("SELECT a.id FROM Account a WHERE a.accountName = :accountName")
    fun findAccountId(accountName: String): Single<Long>

    @Query("SELECT EXISTS(SELECT id FROM Account)")
    fun hasAccount(): Flowable<Boolean>

    @Query("SELECT EXISTS(SELECT id FROM Account WHERE id = :id)")
    fun accountExists(id: Long): Flowable<Boolean>

    @Query("SELECT * FROM Account a WHERE a.id = :id")
    fun getAccount(id: Long): Flowable<AccountEntity>

    @Query("SELECT * FROM Account")
    fun getAccounts(): Flowable<List<AccountEntity>>

    @Query("DELETE FROM Account WHERE Account.id = :id")
    fun deleteAccount(id: Long): Single<Void>

    @Insert
    fun insert(entity: AccountEntity): Single<Long>

    @Insert
    suspend fun insert(vararg entity: AccountEntity): List<Long>

    @Update
    suspend fun update(vararg entity: AccountEntity)

    @Delete
    suspend fun delete(vararg entity: AccountEntity)
}