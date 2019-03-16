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
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String stacktrace = sw.toString(); // stack trace as a string
        StackTraceElement caller = Thread.currentThread().getStackTrace()[3];
        String source = caller.getMethodName() + "() (" + caller.getFileName() + ":" + caller.getLineNumber() + ") -> ";
        Log.d(DeckConsts.DEBUG_TAG, source + stacktrace);
    }

    public static void printCurrentStacktrace(){
        log(getCurrentStacktrace(4));
    }

    public static String getCurrentStacktrace(){
        return getCurrentStacktrace(4);
    }

    private static String getCurrentStacktrace(int offset){
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
