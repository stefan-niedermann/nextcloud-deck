package it.niedermann.android.reactivelivedata.combinator;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.core.util.Pair;
import androidx.core.util.Supplier;
import androidx.lifecycle.LiveData;

import it.niedermann.android.reactivelivedata.ReactiveLiveData;

public class DoubleCombinatorLiveData<T, Y> extends ReactiveLiveData<Pair<T, Y>> {

    public DoubleCombinatorLiveData(@NonNull LiveData<T> source, @NonNull Supplier<LiveData<Y>> secondSourceSupplier) {
        this(source, val -> secondSourceSupplier.get());
    }

    public DoubleCombinatorLiveData(@NonNull LiveData<T> source, @NonNull Function<T, LiveData<Y>> secondSourceFunction) {
        addSource(source, new DoubleCombinatorObserver<>(this, secondSourceFunction));
    }
}
