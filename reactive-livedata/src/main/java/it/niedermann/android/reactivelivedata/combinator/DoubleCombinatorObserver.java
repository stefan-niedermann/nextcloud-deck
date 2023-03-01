package it.niedermann.android.reactivelivedata.combinator;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.core.util.Pair;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

class DoubleCombinatorObserver<T, Y> implements Observer<T> {
    private final MediatorLiveData<Pair<T, Y>> mediator;
    private final Function<T, LiveData<Y>> secondSourceFunction;
    private T value1;
    private Y value2;

    private LiveData<Y> secondSource;

    private boolean value1emitted = false;
    private boolean value2emitted = false;

    public DoubleCombinatorObserver(@NonNull MediatorLiveData<Pair<T, Y>> mediator, @NonNull Function<T, LiveData<Y>> secondSourceFunction) {
        this.mediator = mediator;
        this.secondSourceFunction = secondSourceFunction;
    }

    @Override
    public void onChanged(T emittedValue1) {
        value1 = emittedValue1;
        value1emitted = true;
        if (value2emitted) {
            mediator.setValue(new Pair<>(value1, value2));
        }

        if (secondSource == null) {
            secondSource = secondSourceFunction.apply(emittedValue1);
            mediator.addSource(secondSource, val2 -> {
                value2 = val2;
                value2emitted = true;
                if (value1emitted) {
                    mediator.setValue(new Pair<>(value1, value2));
                }
            });
        }
    }
}
