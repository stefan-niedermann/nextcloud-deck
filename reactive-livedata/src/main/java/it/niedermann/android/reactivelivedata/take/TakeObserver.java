package it.niedermann.android.reactivelivedata.take;

import androidx.annotation.NonNull;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

class TakeObserver<T> implements Observer<T> {
    private final MediatorLiveData<T> mediator;
    private final int limit;
    private int counter = 0;

    public TakeObserver(@NonNull MediatorLiveData<T> mediator, int limit) {
        if (limit == Integer.MAX_VALUE) {
            throw new RuntimeException("limit must be lower than Integer.MAX_VALUE");
        }

        if (limit < 1) {
            throw new RuntimeException("limit must be 1 or higher");
        }

        this.mediator = mediator;
        this.limit = limit;
    }

    @Override
    public void onChanged(T value) {
        if (counter < limit) {
            mediator.setValue(value);
        }
        counter++;

        // Prevent integer overflow
        if (counter == limit + 1) {
            counter = limit;
        }
    }
}