package it.niedermann.nextcloud.deck.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

import it.niedermann.nextcloud.deck.R;

public class SpannableUtil {
    public static SpannableString strong(@NonNull CharSequence text) {
        final SpannableString span = new SpannableString(text);
        span.setSpan(new StyleSpan(Typeface.BOLD), 0, span.length(), 0);
        return span;
    }

    public static SpannableString disabled(@NonNull CharSequence text, @NonNull Context context) {
        final SpannableString span = new SpannableString(text);
        span.setSpan(new StyleSpan(Typeface.ITALIC), 0, span.length(), 0);
        span.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.fg_secondary)), 0, span.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return span;
    }

    public static SpannableString url(@NonNull CharSequence text, @NonNull String target) {
        final SpannableString span = new SpannableString(text);
        span.setSpan(new URLSpan(target), 0, span.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return span;
    }

    public static void setTextWithURL(@NonNull TextView textView, @NonNull Resources resources, @StringRes int containerTextId, @StringRes int linkLabelId, @StringRes int urlId) {
        final String linkLabel = resources.getString(linkLabelId);
        final String finalText = resources.getString(containerTextId, linkLabel);
        final SpannableStringBuilder finalTextBuilder = new SpannableStringBuilder(finalText);
        finalTextBuilder.setSpan(new URLSpan(resources.getString(urlId)), finalText.indexOf(linkLabel), finalText.indexOf(linkLabel) + linkLabel.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(finalTextBuilder);
        textView.setMovementMethod(new LinkMovementMethod());
    }
}
