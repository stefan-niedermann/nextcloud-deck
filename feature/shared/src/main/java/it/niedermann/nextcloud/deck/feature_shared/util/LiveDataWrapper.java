package it.niedermann.nextcloud.deck.feature_shared.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;

/// Wrapper for `error` and `completed` meta information usually provided by reactive libraries and not provided by [androidx.lifecycle.LiveData]
public class LiveDataWrapper<T extends Serializable> implements Serializable {

    @Nullable
    private final T value;
    @Nullable
    private final Throwable error;
    private final boolean completed;
    private final boolean pristine;

    private LiveDataWrapper(@Nullable T value,
                            @Nullable Throwable error,
                            boolean completed,
                            boolean pristine) {
        this.value = value;
        this.error = error;
        this.completed = completed;
        this.pristine = pristine;
    }

    public static <T extends Serializable> LiveDataWrapper<T> create() {
        return new LiveDataWrapper<>(null, null, false, true);
    }

    public static <T extends Serializable> LiveDataWrapper<T> next(@NonNull T value) {
        return new LiveDataWrapper<>(value, null, false, false);
    }

    public static <T extends Serializable> LiveDataWrapper<T> error(@NonNull Throwable error) {
        return new LiveDataWrapper<>(null, error, true, false);
    }

    public static <T extends Serializable> LiveDataWrapper<T> completed() {
        return new LiveDataWrapper<>(null, null, true, false);
    }

    public boolean hasValue() {
        return this.value != null;
    }

    @Nullable
    public T getValue() {
        return value;
    }

    public boolean hasError() {
        return this.error != null;
    }

    @Nullable
    public Throwable getError() {
        return error;
    }

    public boolean isCompleted() {
        return completed;
    }

    /// @return `true` if no value has been emitted yet, nor the source has been `completed` or thrown an `error`.
    public boolean isPristine() {
        return pristine;
    }
}
