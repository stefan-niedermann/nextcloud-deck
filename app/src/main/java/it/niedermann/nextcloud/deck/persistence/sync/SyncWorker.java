package it.niedermann.nextcloud.deck.persistence.sync;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import it.niedermann.nextcloud.deck.DeckLog;

public class SyncWorker extends Worker {

    public static final String TAG = SyncWorker.class.getSimpleName();

    public SyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        SyncManager syncManager = new SyncManager(getApplicationContext(), null);
        DeckLog.log("Starting background synchronization");
        boolean success = syncManager.synchronizeEverything();
        DeckLog.log("Finishing background synchronization with result " + success);
        return success ? Result.failure() : Result.success();
    }
}
