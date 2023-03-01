package it.niedermann.android.reactivelivedata.merge;

import androidx.annotation.NonNull;
import androidx.core.util.Supplier;
import androidx.lifecycle.LiveData;

import it.niedermann.android.reactivelivedata.ReactiveLiveData;

public class MergeLiveData<T> extends ReactiveLiveData<T> {

    public MergeLiveData(@NonNull LiveData<T> source, @NonNull Supplier<LiveData<T>> secondSource) {
        addSource(source, this::setValue);
        addSource(secondSource.get(), this::setValue);
    }
}
