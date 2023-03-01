package it.niedermann.android.reactivelivedata.flatmap;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.core.util.Supplier;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import it.niedermann.android.reactivelivedata.ReactiveLiveData;

public class FlatMapLiveData<T, Y> extends ReactiveLiveData<Y> {

    public FlatMapLiveData(@NonNull LiveData<T> source, @NonNull Supplier<LiveData<Y>> switchMapSupplier) {
        this(source, val -> switchMapSupplier.get());
    }

    public FlatMapLiveData(@NonNull LiveData<T> source, @NonNull Function<T, LiveData<Y>> flatMapFunction) {
        super(Transformations.switchMap(source, flatMapFunction));
    }
}
