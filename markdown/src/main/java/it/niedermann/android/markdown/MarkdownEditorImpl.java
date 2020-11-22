package it.niedermann.android.markdown;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import it.niedermann.android.markdown.rxmarkdown.RxMarkdownEditor;

public class MarkdownEditorImpl extends AbstractMarkdownEditor<RxMarkdownEditor> {

    public MarkdownEditorImpl(@NonNull Context context) {
        super(context, new RxMarkdownEditor(context));
    }

    public MarkdownEditorImpl(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, new RxMarkdownEditor(context, attrs));
    }

    public MarkdownEditorImpl(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr, new RxMarkdownEditor(context, attrs, defStyleAttr));
    }
}
