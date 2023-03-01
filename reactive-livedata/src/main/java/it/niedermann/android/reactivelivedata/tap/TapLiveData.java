package it.niedermann.android.reactivelivedata.tap;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.lifecycle.LiveData;

import java.util.concurrent.ExecutorService;

import it.niedermann.android.reactivelivedata.map.MapLiveData;

public class TapLiveData<T> extends MapLiveData<T, T> {

    public TapLiveData(@NonNull LiveData<T> source, @NonNull Runnable runnable) {
        this(source, val -> runnable.run());
    }

    public TapLiveData(@NonNull LiveData<T> source, @NonNull Consumer<T> consumer) {
        super(source, val -> {
            consumer.accept(val);
            return val;
        });
    }

    public TapLiveData(@NonNull LiveData<T> source, @NonNull Runnable runnable, @NonNull ExecutorService executor) {
        this(source, val -> runnable.run(), executor);
    }

    public TapLiveData(@NonNull LiveData<T> source, @NonNull Consumer<T> consumer, @NonNull ExecutorService executor) {
        super(source, val -> {
            consumer.accept(val);
            return val;
        }, executor);
    }
}
