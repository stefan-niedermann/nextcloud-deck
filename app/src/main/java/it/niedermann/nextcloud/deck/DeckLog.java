package it.niedermann.nextcloud.deck;

import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;

public class DeckLog {

    public static void log(String message) {
        StackTraceElement caller = Thread.currentThread().getStackTrace()[3];
        String source = caller.getMethodName() + "() (" + caller.getFileName() + ":" + caller.getLineNumber() + ") -> ";
        Log.d(DeckConsts.DEBUG_TAG, source + message);
    }
    public static void logError(Throwable e) {
        //TODO: reuse pw?
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String stacktrace = sw.toString(); // stack trace as a string
        StackTraceElement caller = Thread.currentThread().getStackTrace()[3];
        String source = caller.getMethodName() + "() (" + caller.getFileName() + ":" + caller.getLineNumber() + ") -> ";
        Log.d(DeckConsts.DEBUG_TAG, source + stacktrace);
    }
}
