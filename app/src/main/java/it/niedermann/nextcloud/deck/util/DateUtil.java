package it.niedermann.nextcloud.deck.util;

import android.content.Context;
import android.text.format.DateUtils;

import androidx.annotation.NonNull;

import java.time.ZonedDateTime;

import it.niedermann.nextcloud.deck.R;

public final class DateUtil {
    private static final int DATE_TIME_PARTS_SIZE = 2;

    private DateUtil() {
    }

    public static CharSequence getRelativeDateTimeString(@NonNull Context context, long time) {
        long now = ZonedDateTime.now().toInstant().toEpochMilli();
        if ((now - time) < 60 * 1000 && now > time) {
            // < 60 seconds -> seconds ago
            return context.getString(R.string.seconds_ago);
        } else {
            // in the future or past (larger than 60 seconds)
            CharSequence dateString = DateUtils.getRelativeDateTimeString(
                    context,
                    time,
                    DateUtils.SECOND_IN_MILLIS,
                    DateUtils.WEEK_IN_MILLIS,
                    0
            );
            String[] parts = dateString.toString().split(",");
            if (parts.length == DATE_TIME_PARTS_SIZE) {
                if (parts[1].contains(":") && !parts[0].contains(":")) {
                    return parts[0];
                } else if (parts[0].contains(":") && !parts[1].contains(":")) {
                    return parts[1];
                }
            }
            // dateString contains unexpected format.
            // fallback: use relative date time string from android api as is.
            return dateString.toString();
        }
    }
}
