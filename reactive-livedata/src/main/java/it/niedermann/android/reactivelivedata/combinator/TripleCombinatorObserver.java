package it.niedermann.android.reactivelivedata.combinator;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import kotlin.Triple;

class TripleCombinatorObserver<T, Y, Z> implements Observer<T> {
    private final MediatorLiveData<Triple<T, Y, Z>> mediator;
    private final Function<T, LiveData<Y>> secondSourceFunction;
    private final Function<T, LiveData<Z>> thirdSourceFunction;
    private T value1;
    private Y value2;
    private Z value3;

    private LiveData<Y> secondSource;
    private LiveData<Z> thirdSource;

    private boolean value1emitted = false;
    private boolean value2emitted = false;
    private boolean value3emitted = false;

    public TripleCombinatorObserver(@NonNull MediatorLiveData<Triple<T, Y, Z>> mediator, @NonNull Function<T, LiveData<Y>> secondSourceFunction, @NonNull Function<T, LiveData<Z>> thirdSourceFunction) {
        this.mediator = mediator;
        this.secondSourceFunction = secondSourceFunction;
        this.thirdSourceFunction = thirdSourceFunction;
    }

    @Override
    public void onChanged(T emittedValue1) {
        value1 = emittedValue1;
        value1emitted = true;
        if (value2emitted && value3emitted) {
            mediator.setValue(new Triple<>(value1, value2, value3));
        }

        if (secondSource == null) {
            secondSource = secondSourceFunction.apply(emittedValue1);
            mediator.addSource(secondSource, val2 -> {
                value2 = val2;
                value2emitted = true;
                if (value1emitted && value3emitted) {
                    mediator.setValue(new Triple<>(value1, value2, value3));
                }
            });
        }

        if (thirdSource == null) {
            thirdSource = thirdSourceFunction.apply(emittedValue1);
            mediator.addSource(thirdSource, val3 -> {
                value3 = val3;
                value3emitted = true;
                if (value1emitted && value2emitted) {
                    mediator.setValue(new Triple<>(value1, value2, value3));
                }
            });
        }
    }
}
