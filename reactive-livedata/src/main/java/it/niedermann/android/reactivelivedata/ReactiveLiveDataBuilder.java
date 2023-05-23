package it.niedermann.android.reactivelivedata;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.core.util.Consumer;
import androidx.core.util.Pair;
import androidx.core.util.Predicate;
import androidx.core.util.Supplier;
import androidx.lifecycle.LiveData;

import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import kotlin.Triple;
import kotlin.jvm.functions.Function1;

/**
 * Partial implementation of <a href="https://reactivex.io/documentation/operators.html">ReactiveX</a> features
 */
public interface ReactiveLiveDataBuilder<T> {

    /**
     * @see <a href="https://reactivex.io/documentation/operators/map.html">ReactiveX#map</a>
     */
    @NonNull
    <Y> ReactiveLiveDataBuilder<Y> map(@NonNull Function1<T, Y> mapFunction);

    /**
     * @see <a href="https://reactivex.io/documentation/operators/flatmap.html">ReactiveX#flatmap</a>
     */
    @NonNull
    <Y> ReactiveLiveDataBuilder<Y> flatMap(@NonNull Function1<T, LiveData<Y>> flatMapFunction);

    /**
     * @see #flatMap(Function1)
     */
    @NonNull
    <Y> ReactiveLiveDataBuilder<Y> flatMap(@NonNull Supplier<LiveData<Y>> flatMapSupplier);

    /**
     * @see <a href="https://reactivex.io/documentation/operators/distinct.html">ReactiveX#distinct</a>
     */
    @NonNull
    ReactiveLiveDataBuilder<T> distinctUntilChanged();

    /**
     * @see <a href="https://reactivex.io/documentation/operators/filter.html">ReactiveX#filter</a>
     */
    @NonNull
    ReactiveLiveDataBuilder<T> filter(@NonNull Predicate<T> predicate);

    /**
     * @see #filter(Predicate)
     */
    @NonNull
    ReactiveLiveDataBuilder<T> filter(@NonNull Supplier<Boolean> supplier);

    /**
     * @see <a href="https://reactivex.io/documentation/operators/do.html">ReactiveX#do</a>
     */
    @NonNull
    ReactiveLiveDataBuilder<T> tap(@NonNull Consumer<T> consumer);

    /**
     * @see #tap(Consumer)
     */
    @NonNull
    ReactiveLiveDataBuilder<T> tap(@NonNull Runnable runnable);

    /**
     * @see <a href="https://reactivex.io/documentation/operators/merge.html">ReactiveX#merge</a>
     */
    @NonNull
    ReactiveLiveData<T> merge(@NonNull Supplier<LiveData<T>> liveData);

    /**
     * @see <a href="https://reactivex.io/documentation/operators/take.html">ReactiveX#take</a>
     */
    @NonNull
    ReactiveLiveDataBuilder<T> take(int limit);

    /**
     * @see <a href="https://reactivex.io/documentation/operators/combinelatest.html">ReactiveX#combinelatest</a>
     */
    @NonNull
    <Y> ReactiveLiveDataBuilder<Pair<T, Y>> combineWith(@NonNull Function<T, LiveData<Y>> secondSourceFunction);

    /**
     * @see #combineWith(Function)
     */
    @NonNull
    <Y> ReactiveLiveDataBuilder<Pair<T, Y>> combineWith(@NonNull Supplier<LiveData<Y>> secondSourceSupplier);

    /**
     * @see <a href="https://reactivex.io/documentation/operators/combinelatest.html">ReactiveX#combinelatest</a>
     */
    @NonNull
    <Y, Z> ReactiveLiveDataBuilder<Triple<T, Y, Z>> combineWith(@NonNull Function<T, LiveData<Y>> secondSourceFunction, @NonNull Function<T, LiveData<Z>> thirdSourceFunction);

    /**
     * @see #combineWith(Function)
     */
    @NonNull
    <Y, Z> ReactiveLiveDataBuilder<Triple<T, Y, Z>> combineWith(@NonNull Function<T, LiveData<Y>> secondSourceFunction, @NonNull Supplier<LiveData<Z>> thirdSourceSupplier);

    /**
     * @see #combineWith(Function)
     */
    @NonNull
    <Y, Z> ReactiveLiveDataBuilder<Triple<T, Y, Z>> combineWith(@NonNull Supplier<LiveData<Y>> secondSourceSupplier, @NonNull Function<T, LiveData<Z>> thirdSourceFunction);

    /**
     * @see #combineWith(Function)
     */
    @NonNull
    <Y, Z> ReactiveLiveDataBuilder<Triple<T, Y, Z>> combineWith(@NonNull Supplier<LiveData<Y>> secondSourceSupplier, @NonNull Supplier<LiveData<Z>> thirdSourceSupplier);

    /**
     * @see <a href="https://reactivex.io/documentation/operators/debounce.html">ReactiveX#debounce</a>>
     */
    @NonNull
    ReactiveLiveDataBuilder<T> debounce(long timeout, @NonNull ChronoUnit timeUnit);

    /**
     * @param timeout defaults to {@link TimeUnit#MILLISECONDS}
     *
     * @see #debounce(long, ChronoUnit)
     */
    @NonNull
    ReactiveLiveDataBuilder<T> debounce(long timeout);
}
