package it.niedermann.nextcloud.deck.util;

import android.content.Context;
import android.text.format.DateUtils;

import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import it.niedermann.nextcloud.deck.R;

public final class DateUtil {
    private static final int DATE_TIME_PARTS_SIZE = 2;

    private DateUtil() {
    }

    public static Date nowInGMT() {
        return convertToGMT(new Date());
    }

    private static Date convertToGMT(Date date ){
        TimeZone tz = TimeZone.getDefault();
        Date ret = new Date( date.getTime() - tz.getRawOffset() );

        // if we are now in DST, back off by the delta.  Note that we are checking the GMT date, this is the KEY.
        if ( tz.inDaylightTime( ret )){
            Date dstDate = new Date( ret.getTime() - tz.getDSTSavings() );

            // check to make sure we have not crossed back into standard time
            // this happens when we are on the cusp of DST (7pm the day before the change for PDT)
            if ( tz.inDaylightTime( dstDate )){
                ret = dstDate;
            }
        }
        return ret;
    }

    /**
     * Get difference between 2 dates in days (hours, minutes will be set to zero).
     *
     * @param dateFrom  start date
     * @param dateUntil end date
     * @return difference between the to dates in days.
     */
    public static long getDayDifference(Date dateFrom, Date dateUntil) {
        dateFrom.setHours(0);
        dateFrom.setMinutes(0);

        dateUntil.setHours(0);
        dateUntil.setMinutes(0);

        return TimeUnit.DAYS.convert(dateUntil.getTime() - dateFrom.getTime(), TimeUnit.MILLISECONDS);
    }

    public static CharSequence getRelativeDateTimeString(Context context, long time) {
        if ((System.currentTimeMillis() - time) < 60 * 1000 && System.currentTimeMillis() > time) {
            // < 60 seconds -> seconds ago
            return context.getString(R.string.seconds_ago);
        } else {
            // in the future or past (larger than 60 seconds)
            CharSequence dateString = DateUtils.getRelativeDateTimeString(
                    context,
                    time,
                    DateUtils.DAY_IN_MILLIS,
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
