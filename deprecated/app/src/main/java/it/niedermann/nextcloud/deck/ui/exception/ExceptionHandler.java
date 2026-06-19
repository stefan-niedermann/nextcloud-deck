package it.niedermann.nextcloud.deck.ui.exception;

import android.app.Activity;

import androidx.annotation.NonNull;

import it.niedermann.nextcloud.deck.DeckLog;

public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

    @NonNull
    private final Activity activity;

    public ExceptionHandler(@NonNull Activity activity) {
        this.activity = activity;
    }

    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
        DeckLog.logError(e);
        activity.getApplicationContext().startActivity(ExceptionActivity.createIntent(activity, e));
        activity.finish();
        Runtime.getRuntime().exit(0);
    }
}
