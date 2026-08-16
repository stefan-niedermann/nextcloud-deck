package it.niedermann.nextcloud.deck.domain.state

import java.util.concurrent.CompletableFuture
import java.util.concurrent.Flow

interface KeyValueStore {

    fun putString(key: String, value: String): CompletableFuture<Void>
    fun putLong(key: String, value: Long): CompletableFuture<Void>
    fun putBoolean(key: String, value: Boolean): CompletableFuture<Void>

    fun getString(key: String, defaultValue: String): Flow.Publisher<String>
    fun getString(key: String): Flow.Publisher<String> = getString(key, "")

    fun getLong(key: String, defaultValue: Long): Flow.Publisher<Long>
    fun getLong(key: String): Flow.Publisher<Long> = getLong(key, -1L)

    fun getBoolean(key: String, defaultValue: Boolean): Flow.Publisher<Boolean>
    fun getBoolean(key: String): Flow.Publisher<Boolean> = getBoolean(key, false)

    fun containsKey(key: String): CompletableFuture<Boolean>
    fun clear(): CompletableFuture<Void>
    fun remove(key: String): CompletableFuture<Void>

}
