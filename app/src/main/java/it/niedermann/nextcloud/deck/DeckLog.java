package it.niedermann.nextcloud.deck;

import android.util.Log;

public class DeckLog {

    public static void log(String message) {
        StackTraceElement caller = Thread.currentThread().getStackTrace()[3];
        String source = caller.getMethodName()+"() ("+caller.getFileName()+":"+caller.getLineNumber()+") -> ";
        Log.d(DeckConsts.DEBUG_TAG, source + message);
    }
}
