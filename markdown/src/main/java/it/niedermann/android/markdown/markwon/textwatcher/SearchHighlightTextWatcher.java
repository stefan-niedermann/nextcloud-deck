package it.niedermann.android.markdown.markwon.textwatcher;

import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.annotation.NonNull;

import it.niedermann.android.markdown.markwon.MarkwonMarkdownEditor;
import it.niedermann.android.markdown.markwon.MarkwonMarkdownUtil;

public class SearchHighlightTextWatcher extends InterceptorTextWatcher {

    private final MarkwonMarkdownEditor editText;

    public SearchHighlightTextWatcher(@NonNull TextWatcher originalWatcher, @NonNull MarkwonMarkdownEditor editText) {
        super(originalWatcher);
        this.editText = editText;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        this.originalWatcher.beforeTextChanged(s, start, count, after);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        this.originalWatcher.onTextChanged(s, start, before, count);
    }

    @Override
    public void afterTextChanged(Editable s) {
        originalWatcher.afterTextChanged(s);
        MarkwonMarkdownUtil.searchAndColor(s, "new", editText.getContext(), 0, Color.MAGENTA, Color.GREEN);
    }
}
