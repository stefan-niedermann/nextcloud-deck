package it.niedermann.android.markdown.markwon.plugins;

import android.content.Context;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.MarkwonPlugin;
import it.niedermann.android.markdown.R;
import it.niedermann.android.markdown.markwon.MarkwonMarkdownUtil;

public class SearchHighlightPlugin extends AbstractMarkwonPlugin {

    private CharSequence searchText;
    private Integer current;
    private int color;

    public SearchHighlightPlugin(@NonNull Context context) {
        color = ContextCompat.getColor(context, R.color.search_color);
    }

    public static MarkwonPlugin create(@NonNull Context context) {
        return new SearchHighlightPlugin(context);
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
    public void afterSetText(@NonNull TextView textView) {
        super.afterSetText(textView);
        final Editable coloredContent = new SpannableStringBuilder(textView.getText());
        MarkwonMarkdownUtil.searchAndColor(coloredContent, searchText, textView.getContext(), current, color);
        textView.setText(coloredContent, TextView.BufferType.SPANNABLE);
    }
}
