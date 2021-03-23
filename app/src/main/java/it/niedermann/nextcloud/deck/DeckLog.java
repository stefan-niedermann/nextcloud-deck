package it.niedermann.nextcloud.deck;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import it.niedermann.nextcloud.deck.util.MimeTypeUtil;

public class DeckLog {

    private static final StringBuffer DEBUG_LOG = new StringBuffer();
    private static boolean PERSIST_LOGS = false;
    private static final String TAG = DeckLog.class.getSimpleName();
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void enablePeristentLogs(boolean persistLogs) {
        PERSIST_LOGS = persistLogs;
        if (!persistLogs) {
            clearDebugLog();
        }
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
        final String print = "(" + caller.getFileName() + ":" + caller.getLineNumber() + ") " + caller.getMethodName() + "() → " + message;
        if (PERSIST_LOGS) {
            DEBUG_LOG
                    .append(dtf.format(Instant.now().atZone(ZoneId.systemDefault())))
                    .append(" ")
                    .append(severity.name())
                    .append(" ")
                    .append(print)
                    .append("\n");
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
        DEBUG_LOG.setLength(0);
    }

    /**
     * Writes the current log to a temporary file and starts a share intent.
     */
    public static void shareLogAsFile(@NonNull Context context) throws IOException {
        Toast.makeText(context, R.string.copying_logs_to_file, Toast.LENGTH_LONG).show();
        final File logFile = new File(context.getCacheDir().getAbsolutePath() + "/log.txt");
        final FileWriter writer = new FileWriter(logFile);
        writer.write(DeckLog.getDebugLog());
        writer.close();
        context.startActivity(new Intent(Intent.ACTION_SEND)
                .putExtra(Intent.EXTRA_TITLE, context.getString(R.string.log_file))
                .putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", logFile))
                .setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                .setType(MimeTypeUtil.TEXT_PLAIN));
    }
}
