package it.niedermann.nextcloud.deck.persistence.sync;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
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
    private static final Constraints constraints = new Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build();

    public SyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        SyncManager syncManager = new SyncManager(getApplicationContext(), null);
        if (syncManager.hasInternetConnection()) {
            DeckLog.log("Starting background synchronization");
            sharedPreferencesEditor.putLong(getApplicationContext().getString(R.string.shared_preference_last_background_sync), System.currentTimeMillis());
            sharedPreferencesEditor.apply();
            boolean success = syncManager.synchronizeEverything();
            DeckLog.log("Finishing background synchronization with result " + success);
            return success ? Result.failure() : Result.success();
        }
        return Result.success();
    }

    public static void update(@NonNull Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        update(context, sharedPreferences.getString(context.getString(R.string.pref_key_background_sync), context.getString(R.string.pref_value_background_15_minutes)));
    }

    public static void update(@NonNull Context context, String preferenceValue) {
        deregister(context);
        if (!context.getString(R.string.pref_value_background_sync_off).equals(preferenceValue)) {
            int repeatInterval = 15;
            TimeUnit unit = TimeUnit.MINUTES;
            if (context.getString(R.string.pref_value_background_1_hour).equals(preferenceValue)) {
                repeatInterval = 1;
                unit = TimeUnit.HOURS;
            } else if (context.getString(R.string.pref_value_background_6_hours).equals(preferenceValue)) {
                repeatInterval = 6;
                unit = TimeUnit.HOURS;
            }
            PeriodicWorkRequest work = new PeriodicWorkRequest.Builder(SyncWorker.class, repeatInterval, unit)
                    .setConstraints(constraints).build();
            DeckLog.log("Registering worker running each " + repeatInterval + " " + unit);
            WorkManager.getInstance(context.getApplicationContext()).enqueueUniquePeriodicWork(SyncWorker.TAG, ExistingPeriodicWorkPolicy.REPLACE, work);
        }
    }

    private static void deregister(@NonNull Context context) {
        DeckLog.log("Deregistering all workers with tag \"" + TAG + "\"");
        WorkManager.getInstance(context.getApplicationContext()).cancelAllWorkByTag(TAG);
    }
}
