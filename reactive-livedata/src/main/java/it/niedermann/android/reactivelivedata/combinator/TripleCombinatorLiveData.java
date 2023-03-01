package it.niedermann.android.reactivelivedata.combinator;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.core.util.Supplier;
import androidx.lifecycle.LiveData;

import it.niedermann.android.reactivelivedata.ReactiveLiveData;
import kotlin.Triple;

public class TripleCombinatorLiveData<T, Y, Z> extends ReactiveLiveData<Triple<T, Y, Z>> {

    public TripleCombinatorLiveData(@NonNull LiveData<T> source, @NonNull Supplier<LiveData<Y>> secondSourceSupplier, @NonNull Supplier<LiveData<Z>> thirdSourceSupplier) {
        this(source, val -> secondSourceSupplier.get(), val -> thirdSourceSupplier.get());
    }

    public TripleCombinatorLiveData(@NonNull LiveData<T> source, @NonNull Function<T, LiveData<Y>> secondSourceFunction, @NonNull Supplier<LiveData<Z>> thirdSourceSupplier) {
        this(source, secondSourceFunction, val -> thirdSourceSupplier.get());
    }

    public TripleCombinatorLiveData(@NonNull LiveData<T> source, @NonNull Supplier<LiveData<Y>> secondSourceSupplier, @NonNull Function<T, LiveData<Z>> thirdSourceFunction) {
        this(source, val -> secondSourceSupplier.get(), thirdSourceFunction);
    }

    public TripleCombinatorLiveData(@NonNull LiveData<T> source, @NonNull Function<T, LiveData<Y>> secondSourceFunction, @NonNull Function<T, LiveData<Z>> thirdSourceFunction) {
        addSource(source, new TripleCombinatorObserver<>(this, secondSourceFunction, thirdSourceFunction));
    }
}
