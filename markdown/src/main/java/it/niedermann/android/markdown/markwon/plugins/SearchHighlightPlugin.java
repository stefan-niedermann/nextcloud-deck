package it.niedermann.android.markdown.markwon.plugins;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.MetricAffectingSpan;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.MarkwonPlugin;
import it.niedermann.android.util.ColorUtil;

public class SearchHighlightPlugin extends AbstractMarkwonPlugin {

    public static MarkwonPlugin create() {
        return new SearchHighlightPlugin();
    }

    @Override
    public void afterSetText(@NonNull TextView textView) {
        super.afterSetText(textView);
        Spannable coloredContent = searchAndColor(textView.getText(), "new", textView.getContext(), 0, Color.BLUE, Color.YELLOW);
        textView.setText(coloredContent, TextView.BufferType.SPANNABLE);
    }

    public static Spannable searchAndColor(CharSequence sequence, CharSequence searchText, @NonNull Context context, @Nullable Integer current, @ColorInt int mainColor, @ColorInt int textColor) {
        SpannableStringBuilder spannable = new SpannableStringBuilder(sequence);
        CharSequence text = spannable.toString();

        Object[] spansToRemove = spannable.getSpans(0, text.length(), Object.class);
        for (Object span : spansToRemove) {
            if (span instanceof SearchSpan)
                spannable.removeSpan(span);
        }

        if (TextUtils.isEmpty(text) || TextUtils.isEmpty(searchText)) {
            return spannable;
        }

        Matcher m = Pattern.compile(searchText.toString(), Pattern.CASE_INSENSITIVE | Pattern.LITERAL)
                .matcher(text);

        int i = 1;
        while (m.find()) {
            int start = m.start();
            int end = m.end();
            spannable.setSpan(new SearchSpan(context, mainColor, textColor, (current != null && i == current)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            i++;
        }

        return spannable;
    }

    static class SearchSpan extends MetricAffectingSpan {

        private final boolean current;
        @NonNull
        Context context;
        @ColorInt
        private final int mainColor;
        @ColorInt
        private final int textColor;
        @ColorInt
        private final int highlightColor;

        SearchSpan(@NonNull Context context, @ColorInt int mainColor, @ColorInt int textColor, boolean current) {
            this.context = context;
            this.mainColor = mainColor;
            this.textColor = textColor;
            this.current = current;
            this.highlightColor = Color.RED;// context.getResources().getColor(R.color.bg_highlighted);
        }

        @Override
        public void updateDrawState(TextPaint tp) {
            if (current) {
                if (isDarkThemeActive(context)) {
                    if (ColorUtil.INSTANCE.isColorDark(mainColor)) {
                        tp.bgColor = Color.WHITE;
                        tp.setColor(mainColor);
                    } else {
                        tp.bgColor = mainColor;
                        tp.setColor(Color.BLACK);
                    }
                } else {
                    if (ColorUtil.INSTANCE.isColorDark(mainColor)) {
                        tp.bgColor = mainColor;
                        tp.setColor(Color.WHITE);
                    } else {
//                        if (NotesColorUtil.contrastRatioIsSufficient(mainColor, highlightColor)) {
//                            tp.bgColor = highlightColor;
//                        } else {
                            tp.bgColor = Color.BLACK;
//                        }
                        tp.setColor(mainColor);
                    }
                }
            } else {
                tp.bgColor = highlightColor;
                tp.setColor(/*BrandingUtil.getSecondaryForegroundColorDependingOnTheme(context, */mainColor/*)*/);
            }
            tp.setFakeBoldText(true);
        }

        @Override
        public void updateMeasureState(@NonNull TextPaint tp) {
            tp.setFakeBoldText(true);
        }
    }

    private static boolean isDarkThemeActive(Context context) {
        int uiMode = context.getResources().getConfiguration().uiMode;
        return (uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
    }
}
