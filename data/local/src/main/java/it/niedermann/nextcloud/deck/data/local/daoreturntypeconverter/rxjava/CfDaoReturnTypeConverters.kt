package it.niedermann.nextcloud.deck.data.local.daoreturntypeconverter.rxjava

import androidx.room3.DaoReturnTypeConverter
import androidx.room3.OperationType
import androidx.room3.RoomDatabase
import kotlinx.coroutines.AbstractCoroutine
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.newCoroutineContext
import java.util.concurrent.CompletableFuture
import kotlin.coroutines.CoroutineContext

class CfDaoReturnTypeConverters {
    @OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
    @DaoReturnTypeConverter(operations = [OperationType.READ, OperationType.WRITE])
    public fun <T : Any> convertCompletableFuture(
        database: RoomDatabase,
        executeAndConvert: suspend () -> T?,
    ): CompletableFuture<T> {
        val cf = CompletableFuture<T>()
        val scope = GlobalScope
        val context = database.getCoroutineScope().coroutineContext.minusKey(Job)
        val newContext = scope.newCoroutineContext(context)

        val coroutine = CfCoroutine<T>(newContext, cf)
        coroutine.start(CoroutineStart.DEFAULT, coroutine) {
            executeAndConvert.invoke()
                ?: throw EmptyResultSetException("Query returned null, but CompletableFuture<T> was expected.")
        }

        return cf
    }
}

@OptIn(InternalCoroutinesApi::class)
private class CfCoroutine<T : Any>(
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
        try {
            cf.cancel(true)
        } catch (e: Throwable) {
            cf.completeExceptionally(e)
        }
    }
}
