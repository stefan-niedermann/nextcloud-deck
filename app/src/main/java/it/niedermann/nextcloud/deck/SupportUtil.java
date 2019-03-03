package it.niedermann.nextcloud.deck;

import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class SupportUtil {
    private static final int DATE_TIME_PARTS_SIZE = 2;

    /**
     * Creates a {@link Spanned} from a HTML string on all SDK versions.
     *
     * @param source Source string with HTML markup
     * @return Spannable for using in a {@link TextView}
     * @see Html#fromHtml(String)
     * @see Html#fromHtml(String, int)
     */
    public static Spanned fromHtml(String source) {
        if (Build.VERSION.SDK_INT >= 24) {
            return Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(source);
        }
    }

    /**
     * Fills a {@link TextView} with HTML content and activates links in that {@link TextView}.
     *
     * @param view       The {@link TextView} which should be filled.
     * @param stringId   The string resource containing HTML tags (escaped by <code>&lt;</code>)
     * @param formatArgs Arguments for the string resource.
     */
    public static void setHtml(TextView view, int stringId, Object... formatArgs) {
        view.setText(SupportUtil.fromHtml(view.getResources().getString(stringId, formatArgs)));
        view.setMovementMethod(LinkMovementMethod.getInstance());
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

    // TODO create proper UI-Utils class
    /**
     * Converts size of file icon from dp to pixel.
     *
     * @return int
     */
    public static int getAvatarDimension(Context context) {
        // Converts dp to pixel
        return Math.round(context.getResources().getDimension(R.dimen.avatar_size));
    }

    /**
     * convert dp into px.
     *
     * @param dp dp value
     * @return corresponding px value
     */
    public static int dpToPx(Context context, int dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density + 0.5f);
    }
}
