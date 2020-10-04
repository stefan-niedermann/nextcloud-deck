package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import it.niedermann.nextcloud.deck.DeckLog;

/**
 * Extends a {@link MutableLiveData} with an error state
 *
 * @param <T>
 */
public class WrappedLiveData<T> extends MutableLiveData<T> {
    @Nullable
    private Throwable error = null;

    public boolean hasError() {
        return error != null;
    }

    public void setError(@Nullable Throwable error) {
        this.error = error;
    }

    @Nullable
    public Throwable getError() {
        return error;
    }

    public void postError(@Nullable Throwable error) {
        postError(error, null);
    }
    public void postError(@Nullable Throwable error, @NonNull T locallyCreatedEntity) {
        if (error == null) {
            DeckLog.warn("Given error is null");
        }
        setError(error);
        postValue(locallyCreatedEntity);
    }
}
