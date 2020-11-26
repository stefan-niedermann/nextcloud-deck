package it.niedermann.android.markdown;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import it.niedermann.android.markdown.markwon.MarkwonMarkdownViewer;

public class MarkdownViewerImpl extends AbstractMarkdownEditor<MarkwonMarkdownViewer> {

    public MarkdownViewerImpl(@NonNull Context context) {
        this(context, null);
    }

    public MarkdownViewerImpl(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MarkdownViewerImpl(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr, new MarkwonMarkdownViewer(context, attrs, defStyleAttr));
    }
}
