package it.niedermann.nextcloud.deck;

import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;

public class DeckLog {
    public static final String TAG = DeckLog.class.getCanonicalName();

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
        StackTraceElement caller = Thread.currentThread().getStackTrace()[stackTracePosition];
        String source = caller.getMethodName() + "() (" + caller.getFileName() + ":" + caller.getLineNumber() + ") → " + message;
        switch (severity) {
            case VERBOSE:
                Log.v(TAG, source);
                break;
            case DEBUG:
                Log.d(TAG, source);
                break;
            case INFO:
                Log.i(TAG, source);
                break;
            case WARN:
                Log.w(TAG, source);
                break;
            case ERROR:
                Log.e(TAG, source);
                break;
        }
    }

    public static void logError(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String stacktrace = sw.toString(); // stack trace as a string
        StackTraceElement caller = Thread.currentThread().getStackTrace()[3];
        String source = caller.getMethodName() + "() (" + caller.getFileName() + ":" + caller.getLineNumber() + ") -> ";
        Log.d(TAG, source + stacktrace);
    }

    public static void printCurrentStacktrace() {
        log(getCurrentStacktrace(4));
    }

    public static String getCurrentStacktrace() {
        return getCurrentStacktrace(4);
    }

    private static String getCurrentStacktrace(@SuppressWarnings("SameParameterValue") int offset) {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        StringBuffer buff = new StringBuffer();
        for (int i = offset; i < elements.length; i++) {
            StackTraceElement s = elements[i];
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
