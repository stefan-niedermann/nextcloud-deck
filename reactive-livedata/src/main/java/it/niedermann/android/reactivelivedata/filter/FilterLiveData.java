package it.niedermann.android.reactivelivedata.filter;

import androidx.annotation.NonNull;
import androidx.core.util.Predicate;
import androidx.core.util.Supplier;
import androidx.lifecycle.LiveData;

import it.niedermann.android.reactivelivedata.ReactiveLiveData;

public class FilterLiveData<T> extends ReactiveLiveData<T> {

    public FilterLiveData(@NonNull LiveData<T> source, @NonNull Supplier<Boolean> supplier) {
        this(source, val -> supplier.get());
    }

    public FilterLiveData(@NonNull LiveData<T> source, @NonNull Predicate<T> predicate) {
        addSource(source, val -> {
            if (predicate.test(val)) {
                setValue(val);
            }
        });
    }
}
