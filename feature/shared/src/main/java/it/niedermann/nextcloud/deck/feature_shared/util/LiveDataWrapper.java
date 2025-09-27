package it.niedermann.nextcloud.deck.feature_shared.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;

public abstract class LiveDataWrapper<T extends Serializable> implements Serializable {

    @Nullable
    protected final T value;
    @Nullable
    protected final Throwable error;

    protected LiveDataWrapper(@Nullable T value, @Nullable Throwable error) {
        this.value = value;
        this.error = error;
    }

    protected LiveDataWrapper(@NonNull T value) {
        this(value, null);
    }

    protected LiveDataWrapper(@NonNull Throwable error) {
        this(null, error);
    }

    public boolean hasValue() {
        return this.value != null;
    }

    public boolean hasNoValue() {
        return this.value == null;
    }

    @Nullable
    public T getValue() {
        return value;
    }

    public boolean hasError() {
        return this.error != null;
    }

    public boolean hasNoError() {
        return this.error == null;
    }

    @Nullable
    public Throwable getError() {
        return error;
    }
}
