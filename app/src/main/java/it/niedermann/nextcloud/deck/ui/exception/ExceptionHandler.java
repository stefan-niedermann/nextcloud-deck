package it.niedermann.nextcloud.deck.ui.exception;

import android.app.Activity;
import android.content.Intent;

import it.niedermann.nextcloud.deck.DeckLog;

public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

    private Activity context;

    public ExceptionHandler(Activity context) {
        super();
        this.context = context;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {

//        Intent intent = new Intent(context, ExceptionActivity.class);


        Intent intent = new Intent(context.getApplicationContext(), ExceptionActivity.class);
//        intent.setAction("**.ui.exception.ExceptionActivity"); // see step 5.
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        intent.putExtra("logs", errorLogs.toString());
        DeckLog.log("uce - startActivity");
        context.getApplicationContext().startActivity(intent);

        DeckLog.log("uce - finish activity");
        context.finish();
        DeckLog.log("uce - kill Process");
        android.os.Process.killProcess(android.os.Process.myPid());
        DeckLog.log("uce - System.exit(10)");
        System.exit(10);

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
    }
}
