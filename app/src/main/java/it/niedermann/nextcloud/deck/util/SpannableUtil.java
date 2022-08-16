package it.niedermann.nextcloud.deck.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
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
    
    private SpannableUtil() {
        throw new UnsupportedOperationException("This class must not get instantiated");
    }

    public static SpannableString strong(@NonNull CharSequence text) {
        final var spannable = new SpannableString(text);
        spannable.setSpan(new StyleSpan(Typeface.BOLD), 0, spannable.length(), 0);
        return spannable;
    }

    public static SpannableString disabled(@NonNull CharSequence text, @NonNull Context context) {
        final var spannable = new SpannableString(text);
        spannable.setSpan(new StyleSpan(Typeface.ITALIC), 0, spannable.length(), 0);
        spannable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.fg_secondary)), 0, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }

    public static SpannableString url(@NonNull CharSequence text, @NonNull String target) {
        final var spannable = new SpannableString(text);
        spannable.setSpan(new URLSpan(target), 0, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }

    public static void setTextWithURL(@NonNull TextView textView, @NonNull Resources resources, @StringRes int containerTextId, @StringRes int linkLabelId, @StringRes int urlId) {
        final String linkLabel = resources.getString(linkLabelId);
        final String finalText = resources.getString(containerTextId, linkLabel);
        final var spannable = new SpannableString(finalText);
        spannable.setSpan(new URLSpan(resources.getString(urlId)), finalText.indexOf(linkLabel), finalText.indexOf(linkLabel) + linkLabel.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spannable);
        textView.setMovementMethod(new LinkMovementMethod());
    }
}
