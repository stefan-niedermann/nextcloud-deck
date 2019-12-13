package it.niedermann.nextcloud.deck.persistence.sync;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.concurrent.TimeUnit;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;

public class SyncWorker extends Worker {

    private static final String TAG = "it.niedermann.nextcloud.deck.background_synchronization";

    public SyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        SyncManager syncManager = new SyncManager(getApplicationContext(), null);
        if (syncManager.hasInternetConnection()) {
            DeckLog.log("Starting background synchronization");
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
            editor.putLong(getApplicationContext().getString(R.string.shared_preference_last_background_sync), System.currentTimeMillis());
            editor.apply();
            boolean success = syncManager.synchronizeEverything();
            DeckLog.log("Finishing background synchronization with result " + success);
            return success ? Result.failure() : Result.success();
        }
        return Result.success();
    }

    public static void register(@NonNull Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        String backgroundSync = sharedPreferences.getString(context.getString(R.string.pref_key_background_sync), context.getString(R.string.pref_value_background_15_minutes));
        if (!context.getString(R.string.pref_value_background_sync_off).equals(backgroundSync)) {
            int repeatInterval = 15;
            TimeUnit unit = TimeUnit.MINUTES;
            if (context.getString(R.string.pref_value_background_1_hour).equals(backgroundSync)) {
                repeatInterval = 1;
                unit = TimeUnit.HOURS;
            } else if (context.getString(R.string.pref_value_background_6_hours).equals(backgroundSync)) {
                repeatInterval = 6;
                unit = TimeUnit.HOURS;
            }
            Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build();
            PeriodicWorkRequest work = new PeriodicWorkRequest.Builder(SyncWorker.class, repeatInterval, unit)
                    .setConstraints(constraints).build();
            WorkManager.getInstance(context.getApplicationContext()).enqueueUniquePeriodicWork(SyncWorker.TAG, ExistingPeriodicWorkPolicy.REPLACE, work);
        } else {
            deregister(context);
        }
    }

    private static void deregister(@NonNull Context context) {
        WorkManager.getInstance(context.getApplicationContext()).cancelAllWorkByTag(TAG);
    }
}
