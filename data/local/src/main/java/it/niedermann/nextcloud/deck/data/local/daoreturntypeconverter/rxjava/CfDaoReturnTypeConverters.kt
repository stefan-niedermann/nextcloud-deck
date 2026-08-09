package it.niedermann.nextcloud.deck.data.local.daoreturntypeconverter.rxjava

import androidx.room3.DaoReturnTypeConverter
import androidx.room3.OperationType
import androidx.room3.RoomDatabase
import kotlinx.coroutines.AbstractCoroutine
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.InternalCoroutinesApi
import java.util.concurrent.CompletableFuture
import kotlin.coroutines.CoroutineContext

class CfDaoReturnTypeConverters {
    @OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
    @DaoReturnTypeConverter(operations = [OperationType.READ, OperationType.WRITE])
    public fun <T> convertCompletableFuture(
        database: RoomDatabase,
        executeAndConvert: suspend () -> T,
    ): CompletableFuture<T> {
        val cf = CompletableFuture<T>()
        val context = Dispatchers.IO + GlobalScope.coroutineContext
        
        val coroutine = CfCoroutine<T>(context, cf)
        coroutine.start(CoroutineStart.DEFAULT, coroutine) {
            executeAndConvert.invoke()
        }

        return cf
    }
}

@OptIn(InternalCoroutinesApi::class)
private class CfCoroutine<T>(
    parentContext: CoroutineContext,
    private val cf: CompletableFuture<T>
) : AbstractCoroutine<T>(parentContext, false, true) {
    override fun onCompleted(value: T) {
        try {
            cf.complete(value)
        } catch (e: Throwable) {
            cf.completeExceptionally(e)
        }
    }

    override fun onCancelled(cause: Throwable, handled: Boolean) {
        cf.completeExceptionally(cause)
    }
}
