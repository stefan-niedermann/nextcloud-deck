package it.niedermann.nextcloud.deck.domain.state

import java.util.concurrent.CompletableFuture
import java.util.concurrent.Flow

interface KeyValueStore {

    fun putString(key: String, value: String): CompletableFuture<Void>
    fun putLong(key: String, value: Long): CompletableFuture<Void>
    fun putBoolean(key: String, value: Boolean): CompletableFuture<Void>

    fun getString(key: String): Flow.Publisher<String>
    fun getLong(key: String): Flow.Publisher<Long>
    fun getBoolean(key: String): Flow.Publisher<Boolean>

    fun containsKey(key: String): CompletableFuture<Boolean>
    fun clear(): CompletableFuture<Void>
    fun remove(key: String): CompletableFuture<Void>

}
