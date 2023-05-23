package it.niedermann.android.reactivelivedata;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.util.Function;
import androidx.core.util.Consumer;
import androidx.core.util.Pair;
import androidx.core.util.Predicate;
import androidx.core.util.Supplier;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;

import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutorService;

import it.niedermann.android.reactivelivedata.combinator.DoubleCombinatorLiveData;
import it.niedermann.android.reactivelivedata.combinator.TripleCombinatorLiveData;
import it.niedermann.android.reactivelivedata.debounce.DebounceLiveData;
import it.niedermann.android.reactivelivedata.distinct.DistinctUntilChangedLiveData;
import it.niedermann.android.reactivelivedata.filter.FilterLiveData;
import it.niedermann.android.reactivelivedata.flatmap.FlatMapLiveData;
import it.niedermann.android.reactivelivedata.map.MapLiveData;
import it.niedermann.android.reactivelivedata.merge.MergeLiveData;
import it.niedermann.android.reactivelivedata.take.TakeLiveData;
import it.niedermann.android.reactivelivedata.tap.TapLiveData;
import kotlin.Triple;
import kotlin.jvm.functions.Function1;

/**
 * @see ReactiveLiveDataBuilder
 */
public class ReactiveLiveData<T> extends MediatorLiveData<T> implements ReactiveLiveDataBuilder<T> {

    public ReactiveLiveData(@Nullable LiveData<T> source) {
        if (source == null) {
            setValue(null);
        } else {
            addSource(source, this::setValue);
        }
    }

    public ReactiveLiveData(@NonNull T value) {
        setValue(value);
    }

    public ReactiveLiveData() {
        super();
    }

    /**
     * Observe without getting notified about the emitted values.
     */
    public void observe(@NonNull LifecycleOwner owner) {
        super.observe(owner, val -> {
            // Nothing to do…
        });
    }

    /**
     * Observe without getting getting the emitted value.
     */
    public void observe(@NonNull LifecycleOwner owner, @NonNull Runnable runnable) {
        super.observe(owner, val -> runnable.run());
    }

    /**
     * Cancel observation directly after one value has been emitted.
     */
    public void observeOnce(@NonNull LifecycleOwner owner, @NonNull Observer<T> observer) {
        final var internalObserver = new Observer<T>() {
            @Override
            public void onChanged(T result) {
                removeObserver(this);
                observer.onChanged(result);
            }
        };

        observe(owner, internalObserver);
    }

    /**
     * @see Transformations#map(LiveData, Function1)
     */
    @NonNull
    @Override
    public <Y> ReactiveLiveData<Y> map(@NonNull Function1<T, Y> mapFunction) {
        return new MapLiveData<>(this, mapFunction);
    }

    /**
     * @see #map(Function1) but the mapFunction will be executed on the given executor
     */
    public <Y> ReactiveLiveData<Y> map(@NonNull Function1<T, Y> mapFunction, @NonNull ExecutorService executor) {
        return new MapLiveData<>(this, mapFunction, executor);
    }

    /**
     * @see Transformations#switchMap(LiveData, Function1)
     */
    @NonNull
    @Override
    public <Y> ReactiveLiveData<Y> flatMap(@NonNull Function1<T, LiveData<Y>> flatMapFunction) {
        return new FlatMapLiveData<>(this, flatMapFunction);
    }

    @NonNull
    @Override
    public <Y> ReactiveLiveData<Y> flatMap(@NonNull Supplier<LiveData<Y>> switchMapSupplier) {
        return new FlatMapLiveData<>(this, switchMapSupplier);
    }

    /**
     * @see Transformations#distinctUntilChanged(LiveData)
     */
    @NonNull
    @Override
    public ReactiveLiveData<T> distinctUntilChanged() {
        return new DistinctUntilChangedLiveData<>(this);
    }

    @NonNull
    public ReactiveLiveData<T> filter(@NonNull Predicate<T> predicate) {
        return new FilterLiveData<>(this, predicate);
    }

    @NonNull
    @Override
    public ReactiveLiveData<T> filter(@NonNull Supplier<Boolean> supplier) {
        return new FilterLiveData<>(this, supplier);
    }

    @NonNull
    @Override
    public ReactiveLiveData<T> tap(@NonNull Consumer<T> consumer) {
        return new TapLiveData<>(this, consumer);
    }

    @NonNull
    @Override
    public ReactiveLiveData<T> tap(@NonNull Runnable runnable) {
        return new TapLiveData<>(this, runnable);
    }

    /**
     * @see #tap(Consumer) but the tap consumer will be executed on the given executor
     */
    public ReactiveLiveData<T> tap(@NonNull Consumer<T> consumer, @NonNull ExecutorService executor) {
        return new TapLiveData<>(this, consumer, executor);
    }

    public ReactiveLiveData<T> tap(@NonNull Runnable runnable, @NonNull ExecutorService executor) {
        return new TapLiveData<>(this, runnable, executor);
    }

    @NonNull
    @Override
    public ReactiveLiveData<T> merge(@NonNull Supplier<LiveData<T>> secondSource) {
        return new MergeLiveData<>(this, secondSource);
    }

    @NonNull
    @Override
    public ReactiveLiveData<T> take(int limit) {
        return new TakeLiveData<>(this, limit);
    }

    @NonNull
    @Override
    public <Y> ReactiveLiveData<Pair<T, Y>> combineWith(@NonNull Function<T, LiveData<Y>> secondSourceFunction) {
        return new DoubleCombinatorLiveData<>(this, secondSourceFunction);
    }

    @NonNull
    @Override
    public <Y> ReactiveLiveData<Pair<T, Y>> combineWith(@NonNull Supplier<LiveData<Y>> secondSourceSupplier) {
        return new DoubleCombinatorLiveData<>(this, secondSourceSupplier);
    }

    @NonNull
    @Override
    public <Y, Z> ReactiveLiveData<Triple<T, Y, Z>> combineWith(@NonNull Function<T, LiveData<Y>> secondSourceFunction, @NonNull Function<T, LiveData<Z>> thirdSourceFunction) {
        return new TripleCombinatorLiveData<>(this, secondSourceFunction, thirdSourceFunction);
    }

    @NonNull
    @Override
    public <Y, Z> ReactiveLiveData<Triple<T, Y, Z>> combineWith(@NonNull Function<T, LiveData<Y>> secondSourceFunction, @NonNull Supplier<LiveData<Z>> thirdSourceSupplier) {
        return new TripleCombinatorLiveData<>(this, secondSourceFunction, thirdSourceSupplier);
    }

    @NonNull
    @Override
    public <Y, Z> ReactiveLiveData<Triple<T, Y, Z>> combineWith(@NonNull Supplier<LiveData<Y>> secondSourceSupplier, @NonNull Function<T, LiveData<Z>> thirdSourceFunction) {
        return new TripleCombinatorLiveData<>(this, secondSourceSupplier, thirdSourceFunction);
    }

    @NonNull
    @Override
    public <Y, Z> ReactiveLiveData<Triple<T, Y, Z>> combineWith(@NonNull Supplier<LiveData<Y>> secondSourceSupplier, @NonNull Supplier<LiveData<Z>> thirdSourceSupplier) {
        return new TripleCombinatorLiveData<>(this, secondSourceSupplier, thirdSourceSupplier);
    }

    @NonNull
    @Override
    public ReactiveLiveData<T> debounce(long timeout, @NonNull ChronoUnit timeUnit) {
        return new DebounceLiveData<>(this, timeout, timeUnit);
    }

    @NonNull
    @Override
    public ReactiveLiveData<T> debounce(long timeout) {
        return new DebounceLiveData<>(this, timeout);
    }
}
