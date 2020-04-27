package it.niedermann.nextcloud.deck.ui.exception;

import android.app.Activity;

import org.jetbrains.annotations.NotNull;

public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

    private Activity context;

    public ExceptionHandler(Activity context) {
        super();
        this.context = context;
    }

    @Override
    public void uncaughtException(@NotNull Thread t, Throwable e) {
        context.getApplicationContext().startActivity(ExceptionActivity.createIntent(context, e));
        context.finish();
        Runtime.getRuntime().exit(0);
    }
}
