package it.niedermann.nextcloud.deck.util;

import android.content.Context;

import androidx.annotation.DimenRes;

import it.niedermann.nextcloud.deck.R;

public final class DimensionUtil {
    private DimensionUtil() {
    }

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
     * Converts size of file icon from dp to pixel.
     *
     * @return int
     */
    public static int getAvatarDimension(Context context, @DimenRes int size) {
        // Converts dp to pixel
        return Math.round(context.getResources().getDimension(size));
    }
}
