package it.niedermann.android.reactivelivedata.map;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import java.util.concurrent.ExecutorService;

import it.niedermann.android.reactivelivedata.ReactiveLiveData;
import kotlin.jvm.functions.Function1;

public class MapLiveData<T, Y> extends ReactiveLiveData<Y> {

    public MapLiveData(@NonNull LiveData<T> source, @NonNull Function1<T, Y> mapFunction) {
        super(Transformations.map(source, mapFunction));
    }

    public MapLiveData(@NonNull LiveData<T> source, @NonNull Function1<T, Y> mapFunction, @NonNull ExecutorService executor) {
        addSource(source, val -> executor.submit(() -> postValue(mapFunction.invoke(val))));
    }
}
