package it.niedermann.nextcloud.deck.ui.exception

import android.content.Context
import android.util.Log

class ExceptionHandler(
    private val context: Context
) : Thread.UncaughtExceptionHandler {

    override fun uncaughtException(t: Thread, e: Throwable) {
        try {
            Log.e("ExceptionHandler", "Uncaught exception in thread ${t.name}", e)
            val intent = ExceptionActivity.createIntent(context, e)
            context.startActivity(intent)
        } catch (error: Exception) {
            Log.e("ExceptionHandler", "Error in ExceptionHandler", error)
        } finally {
            Runtime.getRuntime().exit(1)
        }
    }

    companion object {
        fun initialize(context: Context) {
            val currentHandler = Thread.getDefaultUncaughtExceptionHandler()
            if (currentHandler is ExceptionHandler) return
            Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler(context))
        }
    }
}
