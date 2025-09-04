package it.niedermann.nextcloud.deck.repository.sync.report;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import it.niedermann.nextcloud.deck.model.Account;

public class LiveDataReporter extends LiveData<SyncStatus> implements SyncStatusReporter {

    /// Given [#setValue] can only happen on the main thread and [#postValue] can omit intermediate status,
    /// we will ensure that we always rely on the latest [SyncStatus] before applying the [Reducer] logic
    /// by storing a duplicate of the [SyncStatus] locally to apply the [Reducer] logic on, but not emitting all intermediates.
    private SyncStatus currentSyncStatus;

    public LiveDataReporter(@NonNull Account account) {
        super(new SyncStatus(account));
        this.currentSyncStatus = getValue();
    }

    @Override
    public boolean report(@NonNull Reducer reducer) {
        synchronized (this) {
            if (currentSyncStatus.isFinished()) {
                return false;
            }

            currentSyncStatus = reducer.apply(currentSyncStatus);
            postValue(currentSyncStatus);
        }

        return true;
    }
}