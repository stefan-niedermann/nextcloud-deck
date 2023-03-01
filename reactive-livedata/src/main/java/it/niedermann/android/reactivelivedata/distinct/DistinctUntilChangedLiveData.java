package it.niedermann.android.reactivelivedata.distinct;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import it.niedermann.android.reactivelivedata.ReactiveLiveData;

public class DistinctUntilChangedLiveData<T> extends ReactiveLiveData<T> {

    public DistinctUntilChangedLiveData(@NonNull LiveData<T> source) {
        super(Transformations.distinctUntilChanged(source));
    }
}
