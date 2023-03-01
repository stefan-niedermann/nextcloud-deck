package it.niedermann.android.reactivelivedata.debounce;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.time.temporal.ChronoUnit;

import it.niedermann.android.reactivelivedata.ReactiveLiveData;

public class DebounceLiveData<T> extends ReactiveLiveData<T> {

    public DebounceLiveData(@NonNull LiveData<T> source, long timeout) {
        this(source, timeout, ChronoUnit.MILLIS);
    }

    public DebounceLiveData(@NonNull LiveData<T> source, long timeout, @NonNull ChronoUnit timeUnit) {
        addSource(source, new DebounceObserver<>(this, timeout, timeUnit));
    }
}
