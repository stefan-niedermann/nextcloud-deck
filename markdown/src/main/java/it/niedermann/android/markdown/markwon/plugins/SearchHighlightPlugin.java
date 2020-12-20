package it.niedermann.android.markdown.markwon.plugins;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.util.Log;
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

    private static final String TAG = SearchHighlightPlugin.class.getSimpleName();

    private boolean searchActive = false;
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
        this.searchActive = true;
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
        if (this.searchActive) {
            final CharSequence content = textView.getText();
            if (content.getClass() == SpannableString.class || content instanceof Spannable) {
                MarkwonMarkdownUtil.searchAndColor((Spannable) content, searchText, textView.getContext(), current, color);
            } else {
                Log.w(TAG, "Expected " + TextView.class.getSimpleName() + " content to be of type " + Spannable.class.getSimpleName() + ", but was of type " + content.getClass() + ". Search highlighting will be not performant.");
                final Spannable coloredContent = new SpannableStringBuilder(content);
                textView.setText(coloredContent, TextView.BufferType.SPANNABLE);
                MarkwonMarkdownUtil.searchAndColor(coloredContent, searchText, textView.getContext(), current, color);
            }
        }
    }
}
