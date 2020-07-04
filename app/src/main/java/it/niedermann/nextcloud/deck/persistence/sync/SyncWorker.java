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

    private static final String WORKER_TAG = "it.niedermann.nextcloud.deck.background_synchronization";
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
            DeckLog.info("Starting background synchronization");
            sharedPreferencesEditor.putLong(getApplicationContext().getString(R.string.shared_preference_last_background_sync), System.currentTimeMillis());
            sharedPreferencesEditor.apply();
            boolean success = syncManager.synchronizeEverything();
            DeckLog.info("Finishing background synchronization with result " + success);
            return success ? Result.failure() : Result.success();
        }
        return Result.success();
    }

    public static void update(@NonNull Context context) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        update(context, sharedPreferences.getString(context.getString(R.string.pref_key_background_sync), context.getString(R.string.pref_value_background_15_minutes)));
    }

    public static void update(@NonNull Context context, String preferenceValue) {
        deregister(context);
        int repeatInterval = -1;
        TimeUnit unit = null;
        if (context.getString(R.string.pref_value_background_15_minutes).equals(preferenceValue)) {
            repeatInterval = 15;
            unit = TimeUnit.MINUTES;
        } else if (context.getString(R.string.pref_value_background_1_hour).equals(preferenceValue)) {
            repeatInterval = 1;
            unit = TimeUnit.HOURS;
        } else if (context.getString(R.string.pref_value_background_6_hours).equals(preferenceValue)) {
            repeatInterval = 6;
            unit = TimeUnit.HOURS;
        }
        if (unit == null) {
            DeckLog.info("Do not register a new " + SyncWorker.class.getSimpleName() + " because setting " + preferenceValue + " is not a valid time frame");
        } else {
            final PeriodicWorkRequest work = new PeriodicWorkRequest.Builder(SyncWorker.class, repeatInterval, unit)
                    .setConstraints(constraints).build();
            DeckLog.info("Registering " + SyncWorker.class.getSimpleName() + " running each " + repeatInterval + " " + unit);
            WorkManager.getInstance(context.getApplicationContext()).enqueueUniquePeriodicWork(SyncWorker.WORKER_TAG, ExistingPeriodicWorkPolicy.REPLACE, work);
        }
    }

    private static void deregister(@NonNull Context context) {
        DeckLog.info("Deregistering all " + SyncWorker.class.getSimpleName() + " with tag \"" + WORKER_TAG + "\"");
        WorkManager.getInstance(context.getApplicationContext()).cancelAllWorkByTag(WORKER_TAG);
    }
}
