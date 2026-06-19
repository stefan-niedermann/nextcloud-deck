package it.niedermann.nextcloud.deck.data.local.dao

import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Update
import io.reactivex.rxjava3.core.Flowable

interface GenericDao<T> {

    @Insert
    fun insert(entity: T): Flowable<Long>

    @Insert
    fun insert(vararg entity: T): Flowable<List<Long>>

    @Update
    suspend fun update(vararg entity: T)

    @Delete
    suspend fun delete(vararg entity: T)
}

