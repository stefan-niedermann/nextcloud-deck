package it.niedermann.nextcloud.deck.util;

import android.content.Context;
import android.os.Build;
import android.text.format.DateUtils;

import androidx.annotation.NonNull;

import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.niedermann.nextcloud.deck.R;

public final class DateUtil {
    private static final int DATE_TIME_PARTS_SIZE = 2;
    private static final String ISO3_LANGUAGE_FRENCH = "fra";
    private static final Pattern FRENCH_TIME_SUFFIX = Pattern.compile(" ([^0-9] )?[0-9]{1,2}:[0-9]{2}$");

    private DateUtil() {
        throw new UnsupportedOperationException("This class must not get instantiated");
    }

    public static CharSequence getRelativeDateTimeString(@NonNull Context context, long time) {
        final long now = ZonedDateTime.now().toInstant().toEpochMilli();
        if ((now - time) < 60_000 && now > time) {
            // < 60 seconds → seconds ago
            return context.getString(R.string.seconds_ago);
        } else {
            // in the future or past (larger than 60 seconds)
            final String dateTimeString = DateUtils.getRelativeDateTimeString(
                    context,
                    time,
                    DateUtils.SECOND_IN_MILLIS,
                    DateUtils.WEEK_IN_MILLIS,
                    0
            ).toString().trim();

            return getRelativeDateStringWithoutTime(dateTimeString)
                    .orElse(dateTimeString);
        }
    }

    /**
     * Tries to strip the time part of a relative, human readable, localized date time string.
     */
    private static Optional<String> getRelativeDateStringWithoutTime(@NonNull String dateTimeString) {
        final String[] parts = dateTimeString.split(",");
        if (parts.length == DATE_TIME_PARTS_SIZE) {
            if (parts[1].contains(":") && !parts[0].contains(":")) {
                return Optional.of(parts[0]);
            } else if (parts[0].contains(":") && !parts[1].contains(":")) {
                return Optional.of(parts[1]);
            }
        }

        /*
         * Date and time are note separated by a <code>,</code>.
         *
         * Relative date time strings on Android <= 11 do not have a <code>,</code> to separate the date from the time in french language.
         * To provide a similar result, we try to find this case and work around the Android limitation.
         *
         * @see <a href="https://github.com/stefan-niedermann/nextcloud-deck/issues/1034">GitHub issue</a>
         */
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
            if (ISO3_LANGUAGE_FRENCH.equalsIgnoreCase(Locale.getDefault().getISO3Language())) {
                final Matcher matcher = FRENCH_TIME_SUFFIX.matcher(dateTimeString);
                if (matcher.find()) {
                    return Optional.of(dateTimeString.substring(0, dateTimeString.length() - matcher.group().length()));
                }
            }
        }
        return Optional.empty();
    }
}
