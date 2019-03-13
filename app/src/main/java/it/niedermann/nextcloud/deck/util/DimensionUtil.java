package it.niedermann.nextcloud.deck.util;

import android.content.Context;

import it.niedermann.nextcloud.deck.R;

public final class DimensionUtil {
    private DimensionUtil() {}

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
     * Converts dp into px.
     *
     * @param dp dp value
     * @return corresponding px value
     */
    public static int dpToPx(Context context, int dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density + 0.5f);
    }
}
