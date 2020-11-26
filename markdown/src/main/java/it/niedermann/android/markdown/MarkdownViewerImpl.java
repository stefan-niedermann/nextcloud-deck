package it.niedermann.android.markdown;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import it.niedermann.android.markdown.markwon.MarkwonMarkdownViewer;

public class MarkdownViewerImpl extends AbstractMarkdownEditor<MarkwonMarkdownViewer> {

    public MarkdownViewerImpl(@NonNull Context context) {
        super(context, new MarkwonMarkdownViewer(context));
    }

    public MarkdownViewerImpl(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, new MarkwonMarkdownViewer(context, attrs));
    }

    public MarkdownViewerImpl(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr, new MarkwonMarkdownViewer(context, attrs, defStyleAttr));
    }
}
