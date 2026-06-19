package it.niedermann.nextcloud.deck.data.local

import java.util.function.Consumer

interface KeyValueStore {

    fun putString(key: String, value: String)
    fun putLong(key: String, value: Long)
    fun putBoolean(key: String, value: Boolean)

    fun getString(key: String): String?
    fun getLong(key: String): Long?
    fun getBoolean(key: String): Boolean?

    fun registerStringChangeListener(key: String, consumer: Consumer<String>)
    fun registerLongChangeListener(key: String, consumer: Consumer<Long>)
    fun registerBooleanChangeListener(key: String, consumer: Consumer<Boolean>)

    fun unregisterStringChangeListener(consumer: Consumer<String>)
    fun unregisterLongChangeListener(consumer: Consumer<Long>)
    fun unregisterBooleanChangeListener(consumer: Consumer<Boolean>)

    fun clear();
    fun remove(key: String)

}