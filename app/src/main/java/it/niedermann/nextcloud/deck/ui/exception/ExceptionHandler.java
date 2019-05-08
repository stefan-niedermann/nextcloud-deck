package it.niedermann.nextcloud.deck.ui.exception;

import android.content.Context;
import android.content.Intent;
import android.view.View;

public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

    private View view;
    private Context context;

    public ExceptionHandler(View view, Context context) {
        super();
        this.view = view;
        this.context = context;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {

        Intent intent = new Intent(context, ExceptionActivity.class);
        context.startActivity(intent);
//        new Thread() {
//            @Override
//            public void run() {
//                Looper.prepare();
//
//                DeckLog.log("Uncaught exception");
////                Snackbar.make(view, "TOAST", Snackbar.LENGTH_LONG).setAction("INFO", (event) -> {
////                }).show();
//
//                Intent exceptionActivity = new Intent(context, ExceptionActivity.class);
//                exceptionActivity.putExtra(KEY_THROWABLE, e);
//
//                AlarmManager alarm = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
//                PendingIntent pi = PendingIntent.getActivity(context, 12345, exceptionActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK),
//                        PendingIntent.FLAG_ONE_SHOT);
//                alarm.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 0, pi);
//                Looper.loop();
//            }
//        }.start();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(10);
    }
}
