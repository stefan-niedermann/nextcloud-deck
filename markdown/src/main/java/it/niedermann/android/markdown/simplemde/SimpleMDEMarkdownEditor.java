package it.niedermann.android.markdown.simplemde;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import it.niedermann.android.markdown.AbstractWebViewMarkdownEditor;
import it.niedermann.android.markdown.MarkdownEditor;

@Deprecated
public class SimpleMDEMarkdownEditor extends AbstractWebViewMarkdownEditor implements MarkdownEditor {

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
        return "file:///android_asset/web/simplemde/index.html";
    }
}