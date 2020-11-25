package it.niedermann.android.markdown;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.lifecycle.LiveData;

import java.util.Map;

@RestrictTo(value = RestrictTo.Scope.LIBRARY)
public abstract class AbstractMarkdownEditor<T extends View & MarkdownEditor> extends FrameLayout implements MarkdownEditor {
    private MarkdownEditor editor;


    public AbstractMarkdownEditor(@NonNull Context context, T impl) {
        super(context);
        init(impl);
    }

    public AbstractMarkdownEditor(@NonNull Context context, @Nullable AttributeSet attrs, T impl) {
        super(context, attrs);
        init(impl);
    }

    public AbstractMarkdownEditor(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, T impl) {
        super(context, attrs, defStyleAttr);
        init(impl);
    }

    private void init(T markdownEditor) {
        this.editor = markdownEditor;
        addView(markdownEditor);
    }

    @Override
    public void setMarkdownString(CharSequence text) {
        editor.setMarkdownString(text);
    }

    @Override
    public LiveData<CharSequence> getMarkdownString() {
        return editor.getMarkdownString();
    }

    @Override
    public void setEnabled(boolean enabled) {
        editor.setEnabled(enabled);
    }

    @Override
    public void setMentions(@NonNull Map<String, String> mentions) {
        editor.setMentions(mentions);
    }
}
