package it.niedermann.android.markdown.markwon.textwatcher;

import android.text.TextWatcher;

import androidx.annotation.NonNull;

abstract public class InterceptorTextWatcher implements TextWatcher {

    @NonNull
    protected final TextWatcher originalWatcher;

    public InterceptorTextWatcher(@NonNull TextWatcher originalWatcher) {
        this.originalWatcher = originalWatcher;
    }
}
