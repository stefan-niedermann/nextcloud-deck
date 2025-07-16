package it.niedermann.nextcloud.deck.repository.sync;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.preference.PreferenceManager;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ListenableWorker;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.remote.api.ResponseCallback;
import it.niedermann.nextcloud.deck.repository.BaseRepository;
import it.niedermann.nextcloud.deck.repository.SyncRepository;
import okhttp3.Headers;

public class SyncWorker extends Worker {

    private static final String WORKER_TAG = "it.niedermann.nextcloud.deck.background_synchronization";
    private static final Constraints constraints = new Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build();

    private final BaseRepository baseRepository;
    private final SharedPreferences.Editor editor;

    public SyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.baseRepository = new BaseRepository(context);
        this.editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
    }

    @NonNull
    @Override
    public Result doWork() {
        DeckLog.info("Starting background synchronization");
        editor.putLong(getApplicationContext().getString(R.string.shared_preference_last_background_sync), System.currentTimeMillis());
        editor.apply();

        try {
            return synchronizeEverything(getApplicationContext(), baseRepository.readAccountsDirectly());
        } catch (NextcloudFilesAppAccountNotFoundException e) {
            return Result.failure();
        } finally {
            DeckLog.info("Finishing background synchronization.");
        }
    }

    @WorkerThread
    private ListenableWorker.Result synchronizeEverything(@NonNull Context context, @NonNull List<Account> accounts) throws NextcloudFilesAppAccountNotFoundException {
        if (accounts.isEmpty()) {
            return Result.success();
        }
        final var success = new AtomicBoolean(true);
        final var latch = new CountDownLatch(accounts.size());

        try {
            for (Account account : accounts) {
                new SyncRepository(context, account).synchronize(new ResponseCallback<>(account) {
                    @Override
                    public void onResponse(Boolean response, Headers headers) {
                        success.set(success.get() && Boolean.TRUE.equals(response));
                        latch.countDown();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        success.set(false);
                        super.onError(throwable);
                        latch.countDown();
                    }
                });
            }
            latch.await();
            return success.get() ? Result.success() : Result.failure();
        } catch (InterruptedException e) {
            DeckLog.logError(e);
            return Result.failure();
        }
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
            DeckLog.info("Do not register a new", SyncWorker.class.getSimpleName(), "because setting", preferenceValue, "is not a valid time frame");
        } else {
            final PeriodicWorkRequest work = new PeriodicWorkRequest.Builder(SyncWorker.class, repeatInterval, unit)
                    .setConstraints(constraints).build();
            DeckLog.info("Registering", SyncWorker.class.getSimpleName(), "running each", repeatInterval, unit);
            WorkManager.getInstance(context.getApplicationContext()).enqueueUniquePeriodicWork(SyncWorker.WORKER_TAG, ExistingPeriodicWorkPolicy.REPLACE, work);
        }
    }

    private static void deregister(@NonNull Context context) {
        DeckLog.info("Deregistering all", SyncWorker.class.getSimpleName(), "with tag", WORKER_TAG);
        WorkManager.getInstance(context.getApplicationContext()).cancelAllWorkByTag(WORKER_TAG);
    }
}
