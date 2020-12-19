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
    private Integer current;

    public SearchHighlightTextWatcher(@NonNull TextWatcher originalWatcher, @NonNull MarkwonMarkdownEditor editText) {
        super(originalWatcher);
        this.editText = editText;
    }

    public void setSearchText(@Nullable CharSequence searchText) {
        this.searchText = searchText;
    }

    public void setCurrent(@Nullable Integer current) {
        this.current = current;
    }

    @Override
    public void afterTextChanged(Editable s) {
        originalWatcher.afterTextChanged(s);
        MarkwonMarkdownUtil.searchAndColor(s, searchText, editText.getContext(), current, Color.MAGENTA, Color.GREEN);
    }
}
