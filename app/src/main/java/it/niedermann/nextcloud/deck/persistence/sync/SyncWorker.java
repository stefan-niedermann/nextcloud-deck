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

    private static final String TAG = SyncWorker.class.getSimpleName();

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
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        PeriodicWorkRequest work = new PeriodicWorkRequest.Builder(SyncWorker.class, PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS, TimeUnit.MILLISECONDS)
                .setConstraints(constraints)
                .build();
        WorkManager.getInstance(context.getApplicationContext()).enqueueUniquePeriodicWork(SyncWorker.TAG, ExistingPeriodicWorkPolicy.KEEP, work);
    }

    public static void deregister(@NonNull Context context) {
        WorkManager.getInstance(context.getApplicationContext()).cancelAllWorkByTag(TAG);
    }
}
