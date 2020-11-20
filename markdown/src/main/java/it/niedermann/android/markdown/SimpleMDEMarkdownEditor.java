package it.niedermann.android.markdown;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SimpleMDEMarkdownEditor extends WebViewMarkdownEditor implements MarkdownEditor {

    public SimpleMDEMarkdownEditor(@NonNull Context context) {
        super(context);
    }

    public SimpleMDEMarkdownEditor(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SimpleMDEMarkdownEditor(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected String getUrlToIndex() {
        throw new UnsupportedOperationException(getClass().getSimpleName() + " has not been implemented yet.");
    }
}