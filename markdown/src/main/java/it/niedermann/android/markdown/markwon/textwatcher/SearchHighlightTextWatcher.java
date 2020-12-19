package it.niedermann.android.markdown.markwon.textwatcher;

import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import it.niedermann.android.markdown.markwon.MarkwonMarkdownEditor;
import it.niedermann.android.markdown.markwon.MarkwonMarkdownUtil;

public class SearchHighlightTextWatcher extends InterceptorTextWatcher {

    private final MarkwonMarkdownEditor editText;
    private CharSequence searchText;

    public SearchHighlightTextWatcher(@NonNull TextWatcher originalWatcher, @NonNull MarkwonMarkdownEditor editText) {
        super(originalWatcher);
        this.editText = editText;
    }

    public void setSearchText(@Nullable CharSequence searchText) {
        this.searchText = searchText;
    }

    @Override
    public void afterTextChanged(Editable s) {
        originalWatcher.afterTextChanged(s);
        MarkwonMarkdownUtil.searchAndColor(s, searchText, editText.getContext(), 0, Color.MAGENTA, Color.GREEN);
    }
}
