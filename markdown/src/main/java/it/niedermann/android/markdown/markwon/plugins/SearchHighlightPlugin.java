package it.niedermann.android.markdown.markwon.plugins;

import android.graphics.Color;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.widget.TextView;

import androidx.annotation.NonNull;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.MarkwonPlugin;

import static it.niedermann.android.markdown.markwon.MarkwonMarkdownUtil.searchAndColor;

public class SearchHighlightPlugin extends AbstractMarkwonPlugin {

    public static MarkwonPlugin create() {
        return new SearchHighlightPlugin();
    }

    @Override
    public void afterSetText(@NonNull TextView textView) {
        super.afterSetText(textView);
        final Editable coloredContent = new SpannableStringBuilder(textView.getText());
        searchAndColor(coloredContent, "new", textView.getContext(), 0, Color.BLUE, Color.YELLOW);
        textView.setText(coloredContent, TextView.BufferType.SPANNABLE);
    }
}
