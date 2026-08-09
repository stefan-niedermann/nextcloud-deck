package it.niedermann.nextcloud.deck.data.local.dao

import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Update
import java.util.concurrent.CompletableFuture

interface GenericDao<T> {

    @Insert
    fun insert(entity: T): CompletableFuture<Long>

    @Insert
    fun insert(vararg entity: T): CompletableFuture<List<Long>>

    @Update
    fun updateRx(vararg entity: T): CompletableFuture<Void?>

    @Delete
    fun deleteRx(vararg entity: T): CompletableFuture<Void?>

    @Update
    suspend fun update(vararg entity: T)

    @Delete
    suspend fun delete(vararg entity: T)
}
