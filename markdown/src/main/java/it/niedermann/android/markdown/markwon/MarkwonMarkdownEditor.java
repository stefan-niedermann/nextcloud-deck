package it.niedermann.android.markdown.markwon;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import io.noties.markwon.Markwon;
import io.noties.markwon.editor.MarkwonEditor;
import io.noties.markwon.editor.handler.EmphasisEditHandler;
import io.noties.markwon.editor.handler.StrongEmphasisEditHandler;
import it.niedermann.android.markdown.MarkdownEditor;
import it.niedermann.android.markdown.markwon.handler.BlockQuoteEditHandler;
import it.niedermann.android.markdown.markwon.handler.CodeBlockEditHandler;
import it.niedermann.android.markdown.markwon.handler.CodeEditHandler;
import it.niedermann.android.markdown.markwon.handler.HeadingEditHandler;
import it.niedermann.android.markdown.markwon.handler.StrikethroughEditHandler;
import it.niedermann.android.markdown.markwon.textwatcher.AutoContinuationTextWatcher;

public class MarkwonMarkdownEditor extends AppCompatEditText implements MarkdownEditor {

    private final MutableLiveData<CharSequence> unrenderedText$ = new MutableLiveData<>();

    public MarkwonMarkdownEditor(@NonNull Context context) {
        this(context, null);
    }

    public MarkwonMarkdownEditor(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public MarkwonMarkdownEditor(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final Markwon markwon = MarkwonMarkdownUtil.initMarkwonEditor(context).build();
        final MarkwonEditor editor = MarkwonEditor.builder(markwon)
                .useEditHandler(new EmphasisEditHandler())
                .useEditHandler(new StrongEmphasisEditHandler())
                .useEditHandler(new StrikethroughEditHandler())
                .useEditHandler(new CodeEditHandler())
                .useEditHandler(new CodeBlockEditHandler())
                .useEditHandler(new BlockQuoteEditHandler())
                .useEditHandler(new HeadingEditHandler())
                .build();

        // addTextChangedListener(MarkwonEditorTextWatcher.withPreRender(editor, Executors.newSingleThreadExecutor(), this));
        addTextChangedListener(new AutoContinuationTextWatcher(editor, this));
    }

    @Override
    public void setMarkdownString(CharSequence text) {
        setText(text);
        unrenderedText$.setValue(text == null ? "" : text.toString());
    }

    @Override
    public LiveData<CharSequence> getMarkdownString() {
        return unrenderedText$;
    }
}
