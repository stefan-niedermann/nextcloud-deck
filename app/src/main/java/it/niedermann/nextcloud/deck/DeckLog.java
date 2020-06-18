package it.niedermann.nextcloud.deck;

import android.util.Log;

import androidx.annotation.Nullable;

import java.io.PrintWriter;
import java.io.StringWriter;

public class DeckLog {
    private static final String TAG = DeckLog.class.getSimpleName();

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
        final String print;
        if (BuildConfig.DEBUG) {
            final StackTraceElement caller = Thread.currentThread().getStackTrace()[stackTracePosition];
            print = caller.getMethodName() + "() (" + caller.getFileName() + ":" + caller.getLineNumber() + ") → " + message;
        } else {
            print = message;
        }
        switch (severity) {
            case VERBOSE:
                Log.v(TAG, print);
                break;
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
}
