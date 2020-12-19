package it.niedermann.android.markdown.markwon.textwatcher;

import android.text.Editable;
import android.text.TextWatcher;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import it.niedermann.android.markdown.R;
import it.niedermann.android.markdown.markwon.MarkwonMarkdownEditor;
import it.niedermann.android.markdown.markwon.MarkwonMarkdownUtil;

public class SearchHighlightTextWatcher extends InterceptorTextWatcher {

    private final MarkwonMarkdownEditor editText;
    private CharSequence searchText;
    private Integer current;
    private int color;

    public SearchHighlightTextWatcher(@NonNull TextWatcher originalWatcher, @NonNull MarkwonMarkdownEditor editText) {
        super(originalWatcher);
        this.editText = editText;
        this.color = ContextCompat.getColor(editText.getContext(), R.color.search_color);
    }

    public void setSearchText(@Nullable CharSequence searchText) {
        this.searchText = searchText;
    }

    public void setCurrent(@Nullable Integer current) {
        this.current = current;
    }

    public void setSearchColor(@ColorInt int color) {
        this.color = color;
    }

    @Override
    public void afterTextChanged(Editable s) {
        originalWatcher.afterTextChanged(s);
        MarkwonMarkdownUtil.searchAndColor(s, searchText, editText.getContext(), current, color);
    }
}
