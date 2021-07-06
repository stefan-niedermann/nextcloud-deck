package it.niedermann.nextcloud.deck.util;

import android.content.Context;
import android.os.Build;
import android.text.format.DateUtils;

import androidx.annotation.NonNull;

import java.time.ZonedDateTime;
import java.util.Locale;

import it.niedermann.nextcloud.deck.R;

public final class DateUtil {
    private static final int DATE_TIME_PARTS_SIZE = 2;

    private DateUtil() {
        throw new UnsupportedOperationException("This class must not get instantiated");
    }

    public static CharSequence getRelativeDateTimeString(@NonNull Context context, long time) {
        long now = ZonedDateTime.now().toInstant().toEpochMilli();
        if ((now - time) < 60 * 1000 && now > time) {
            // < 60 seconds -> seconds ago
            return context.getString(R.string.seconds_ago);
        } else {
            // in the future or past (larger than 60 seconds)
            final String dateString = DateUtils.getRelativeDateTimeString(
                    context,
                    time,
                    DateUtils.SECOND_IN_MILLIS,
                    DateUtils.WEEK_IN_MILLIS,
                    0
            ).toString().trim();

            // https://github.com/stefan-niedermann/nextcloud-deck/issues/1034
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R && Locale.getDefault().getDisplayLanguage().startsWith("fr_")) {
                if (dateString.matches("\\. ([^0-9] )?[0-9]{1,2}:[0-9]{2}$")) {
                    return dateString.substring(0, dateString.length() - 8);
                }
            }

            final String[] parts = dateString.split(",");
            if (parts.length == DATE_TIME_PARTS_SIZE) {
                if (parts[1].contains(":") && !parts[0].contains(":")) {
                    return parts[0];
                } else if (parts[0].contains(":") && !parts[1].contains(":")) {
                    return parts[1];
                }
            }
            // dateString contains unexpected format.
            // fallback: use relative date time string from android api as is.
            return dateString;
        }
    }
}
