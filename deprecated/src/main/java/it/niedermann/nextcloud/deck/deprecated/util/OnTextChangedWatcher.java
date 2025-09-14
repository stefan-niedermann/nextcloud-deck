package it.niedermann.nextcloud.deck.deprecated.util;

import android.text.Editable;
import android.text.TextWatcher;

import androidx.annotation.NonNull;

import java.util.function.Consumer;

/**
 * Simple {@link TextWatcher} which only listens on {@link #onTextChanged(CharSequence, int, int, int)} and is therefore usable as {@link FunctionalInterface}
 */
public class OnTextChangedWatcher implements TextWatcher {

    private final Consumer<String> consumer;

    public OnTextChangedWatcher(@NonNull Consumer<String> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        consumer.accept(s.toString());
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
