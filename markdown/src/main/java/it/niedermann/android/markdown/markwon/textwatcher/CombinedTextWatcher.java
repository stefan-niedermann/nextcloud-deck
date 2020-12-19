package it.niedermann.android.markdown.markwon.textwatcher;

import android.text.Editable;
import android.text.TextWatcher;

import androidx.annotation.NonNull;

import java.util.concurrent.Executors;

import io.noties.markwon.editor.MarkwonEditor;
import io.noties.markwon.editor.MarkwonEditorTextWatcher;
import it.niedermann.android.markdown.markwon.MarkwonMarkdownEditor;

public class CombinedTextWatcher implements TextWatcher {

    private final TextWatcher watcher;

    public CombinedTextWatcher(@NonNull MarkwonEditor editor, @NonNull MarkwonMarkdownEditor editText) {
        final TextWatcher markwonTextWatcher = MarkwonEditorTextWatcher.withPreRender(editor, Executors.newSingleThreadExecutor(), editText);
        final TextWatcher autoContinuationTextWatcher = new AutoContinuationTextWatcher(markwonTextWatcher, editText);
        watcher = new SearchHighlightTextWatcher(autoContinuationTextWatcher, editText);
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        watcher.beforeTextChanged(s, start, count, after);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        watcher.onTextChanged(s, start, before, count);
    }

    @Override
    public void afterTextChanged(Editable s) {
        watcher.afterTextChanged(s);
    }
}
