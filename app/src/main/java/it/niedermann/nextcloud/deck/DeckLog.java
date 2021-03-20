package it.niedermann.nextcloud.deck;

import android.util.Log;

import androidx.annotation.Nullable;

import java.io.PrintWriter;
import java.io.StringWriter;

public class DeckLog {

    private static StringBuffer DEBUG_LOG = new StringBuffer();
    private static boolean PERSIST_LOGS = false;
    private static final String TAG = DeckLog.class.getSimpleName();

    public static void enablePeristentLogs(boolean persistLogs) {
        PERSIST_LOGS = persistLogs;
    }

    public enum Severity {
        VERBOSE, DEBUG, LOG, INFO, WARN, ERROR
    }

    public static void verbose(String message) {
        log(message, Severity.VERBOSE, 4);
    }

    public static void log(String message) {
        log(message, Severity.DEBUG, 4);
    }

    public static void info(String message) {
        log(message, Severity.INFO, 4);
    }

    public static void warn(String message) {
        log(message, Severity.WARN, 4);
    }

    public static void error(String message) {
        log(message, Severity.ERROR, 4);
    }

    public static void log(String message, Severity severity) {
        log(message, severity, 3);
    }

    private static void log(String message, Severity severity, int stackTracePosition) {
        if (!(PERSIST_LOGS || BuildConfig.DEBUG)) {
           return;
        }
        final StackTraceElement caller = Thread.currentThread().getStackTrace()[stackTracePosition];
        final String print = caller.getMethodName() + "() (" + caller.getFileName() + ":" + caller.getLineNumber() + ") → " + message;
        if (PERSIST_LOGS) {
            DEBUG_LOG.append(print).append("\n");
        }
        switch (severity) {
            case DEBUG:
                Log.d(TAG, print);
                break;
            case INFO:
                Log.i(TAG, print);
                break;
            case WARN:
                Log.w(TAG, print);
                break;
            case ERROR:
                Log.e(TAG, print);
                break;
            default:
                Log.v(TAG, print);
                break;
        }
    }

    public static void logError(@Nullable Throwable e) {
        if (e == null) {
            error("Could not log error because given error was null");
            return;
        }
        final StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        String stacktrace = sw.toString(); // stack trace as a string
        final StackTraceElement caller = Thread.currentThread().getStackTrace()[3];
        final String source = caller.getMethodName() + "() (" + caller.getFileName() + ":" + caller.getLineNumber() + ") -> ";
        Log.e(TAG, source + stacktrace);
    }

    public static void printCurrentStacktrace() {
        log(getCurrentStacktrace(4));
    }

    public static String getCurrentStacktrace() {
        return getCurrentStacktrace(4);
    }

    private static String getCurrentStacktrace(@SuppressWarnings("SameParameterValue") int offset) {
        final StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        final StringBuilder buff = new StringBuilder();
        for (int i = offset; i < elements.length; i++) {
            final StackTraceElement s = elements[i];
            buff.append("\tat ");
            buff.append(s.getClassName());
            buff.append(".");
            buff.append(s.getMethodName());
            buff.append("(");
            buff.append(s.getFileName());
            buff.append(":");
            buff.append(s.getLineNumber());
            buff.append(")\n");
        }
        return buff.toString();
    }

    public static String getDebugLog() {
        return DEBUG_LOG.toString();
    }

    public static void clearDebugLog() {
        DEBUG_LOG = new StringBuffer();
    }
}
