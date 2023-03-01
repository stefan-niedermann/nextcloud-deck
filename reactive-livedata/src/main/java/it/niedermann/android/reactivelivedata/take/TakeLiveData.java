package it.niedermann.android.reactivelivedata.take;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import it.niedermann.android.reactivelivedata.ReactiveLiveData;

public class TakeLiveData<T> extends ReactiveLiveData<T> {

    public TakeLiveData(@NonNull LiveData<T> source, int limit) {
        addSource(source, new TakeObserver<>(this, limit));
    }
}
