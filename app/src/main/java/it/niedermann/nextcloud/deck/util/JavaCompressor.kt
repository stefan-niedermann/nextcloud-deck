package it.niedermann.nextcloud.deck.util

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.Constraint
import id.zelory.compressor.constraint.default
import kotlinx.coroutines.launch
import java.io.File

fun AppCompatActivity.compress(
        inputFile: File,
        javaCompressorCallback: JavaCompressorCallback,
        vararg constraints: Constraint
) {
    val context = this
    var outputFile: File? = null
    lifecycleScope.launch {
        try {
            outputFile = Compressor.compress(context, inputFile) {
                if (constraints.isEmpty()) {
                    default()
                } else {
                    for (con in constraints)
                        constraint(con)
                }
            }.also {
                javaCompressorCallback.onComplete(true, it)
            }
        } catch (e: Exception) {
            javaCompressorCallback.onComplete(false, outputFile)
        }
    }
}


class JavaCompressor {
    companion object {
        @JvmStatic
        fun compress(activity: AppCompatActivity, inputFile: File,
                     javaCompressorCallback: JavaCompressorCallback,
                     vararg constraints: Constraint) {
            activity.compress(inputFile, javaCompressorCallback, *constraints)
        }
    }
}

interface JavaCompressorCallback {
    fun onComplete(status: Boolean, file: File?)
}