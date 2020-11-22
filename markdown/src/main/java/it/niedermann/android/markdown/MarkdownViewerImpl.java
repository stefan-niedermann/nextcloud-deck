package it.niedermann.android.markdown;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import it.niedermann.android.markdown.rxmarkdown.RxMarkdownViewer;

public class MarkdownViewerImpl extends AbstractMarkdownEditor<RxMarkdownViewer> {

    public MarkdownViewerImpl(@NonNull Context context) {
        super(context, new RxMarkdownViewer(context));
    }

    public MarkdownViewerImpl(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, new RxMarkdownViewer(context, attrs));
    }

    public MarkdownViewerImpl(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr, new RxMarkdownViewer(context, attrs, defStyleAttr));
    }
}
