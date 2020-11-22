package it.niedermann.android.markdown.simplemde;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import it.niedermann.android.markdown.AbstractWebViewMarkdownEditor;
import it.niedermann.android.markdown.MarkdownEditor;

@Deprecated
public class SimpleMDEMarkdownViewer extends AbstractWebViewMarkdownEditor implements MarkdownEditor {

    public SimpleMDEMarkdownViewer(@NonNull Context context) {
        super(context);
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public SimpleMDEMarkdownViewer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public SimpleMDEMarkdownViewer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    protected String getUrlToIndex() {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}