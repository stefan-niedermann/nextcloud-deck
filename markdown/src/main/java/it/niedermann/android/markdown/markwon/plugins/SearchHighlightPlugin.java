package it.niedermann.android.markdown.markwon.plugins;

import android.graphics.Color;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.MarkwonPlugin;
import it.niedermann.android.markdown.markwon.MarkwonMarkdownUtil;

public class SearchHighlightPlugin extends AbstractMarkwonPlugin {

    private CharSequence searchText;
    private Integer current;

    public static MarkwonPlugin create() {
        return new SearchHighlightPlugin();
    }

    public void setSearchText(@Nullable CharSequence searchText) {
        this.searchText = searchText;
    }

    public void setCurrent(@Nullable Integer current) {
        this.current = current;
    }

    @Override
    public void afterSetText(@NonNull TextView textView) {
        super.afterSetText(textView);
        final Editable coloredContent = new SpannableStringBuilder(textView.getText());
        MarkwonMarkdownUtil.searchAndColor(coloredContent, searchText, textView.getContext(), current, Color.BLUE, Color.YELLOW);
        textView.setText(coloredContent, TextView.BufferType.SPANNABLE);
    }
}
