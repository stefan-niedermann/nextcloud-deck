package it.niedermann.nextcloud.deck.util;

import android.content.Context;
import android.widget.TextView;

import java.util.Date;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import it.niedermann.nextcloud.deck.R;

public final class ThemeUtil {
    private ThemeUtil() {}

    public static void themeDueDate(Context context, TextView cardDueDate, Date dueDate) {
        long diff = DateUtil.getDayDifference(new Date(), dueDate);

        int backgroundDrawable = 0;
        int textColor = R.color.default_text_color;
        int icon = R.drawable.calendar_blank_grey600_24dp;

        if (diff == 1) {
            // due date: tomorrow
            backgroundDrawable = R.drawable.due_tomorrow_background;
        } else if (diff == 0) {
            // due date: today
            backgroundDrawable = R.drawable.due_today_background;
        } else if (diff < 0) {
            // due date: overdue
            backgroundDrawable = R.drawable.due_overdue_background;
            textColor = R.color.overdue_text_color;
            icon = R.drawable.calendar_blank_white_24dp;
        }

        themeDueDate(context, cardDueDate, backgroundDrawable, textColor, icon);
    }

    private static void themeDueDate(Context context, TextView cardDueDate, @DrawableRes int background, @ColorRes int textColor, @DrawableRes int icon) {
        cardDueDate.setBackgroundResource(background);
        cardDueDate.setTextColor(context.getResources().getColor(textColor));

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            cardDueDate.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    context.getResources().getDrawable(icon),
                    null,
                    null,
                    null
            );
        } else {
            cardDueDate.setCompoundDrawablesWithIntrinsicBounds(
                    context.getResources().getDrawable(icon),
                    null,
                    null,
                    null
            );
        }
    }
}
