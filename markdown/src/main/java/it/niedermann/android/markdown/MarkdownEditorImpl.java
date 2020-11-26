package it.niedermann.android.markdown;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import it.niedermann.android.markdown.markwon.MarkwonMarkdownEditor;

public class MarkdownEditorImpl extends AbstractMarkdownEditor<MarkwonMarkdownEditor> {

    public MarkdownEditorImpl(@NonNull Context context) {
        this(context, null);
    }

    public MarkdownEditorImpl(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MarkdownEditorImpl(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr, new MarkwonMarkdownEditor(context, attrs, defStyleAttr));
    }
}
