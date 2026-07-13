package it.niedermann.nextcloud.deck.data.local

import java.util.concurrent.Flow

interface KeyValueStore {

    fun putString(key: String, value: String)
    fun putLong(key: String, value: Long)
    fun putBoolean(key: String, value: Boolean)

    fun getString(key: String): Flow.Publisher<String>
    fun getLong(key: String): Flow.Publisher<Long>
    fun getBoolean(key: String): Flow.Publisher<Boolean>

    fun containsKey(key: String): Boolean
    fun clear()
    fun remove(key: String)

}