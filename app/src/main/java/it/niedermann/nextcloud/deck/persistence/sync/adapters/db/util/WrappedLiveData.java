package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

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
        setError(error);
        postValue(null);
    }
}
