package it.niedermann.nextcloud.deck.util;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import it.niedermann.nextcloud.deck.R;

public class SpannableUtil {
    public static SpannableString strong(@NonNull CharSequence text) {
        SpannableString span = new SpannableString(text);
        span.setSpan(new StyleSpan(Typeface.BOLD), 0, span.length(), 0);
        return span;
    }

    public static SpannableString disabled(@NonNull CharSequence text, @NonNull Context context) {
        SpannableString span = new SpannableString(text);
        span.setSpan(new StyleSpan(Typeface.ITALIC), 0, span.length(), 0);
        span.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.fg_secondary)), 0, span.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return span;
    }

    public static SpannableString url(@NonNull CharSequence text, @NonNull String target) {
        SpannableString span = new SpannableString(text);
        span.setSpan(new URLSpan(target), 0, span.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return span;
    }

    public static SpannableString url(@NonNull String target) {
        return url(target, target);
    }
}
