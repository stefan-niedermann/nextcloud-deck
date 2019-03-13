package it.niedermann.nextcloud.deck.util;

import android.graphics.Color;
import androidx.annotation.ColorInt;

/**
 * Helper implementation to deal with color related functionality.
 */
public final class ColorUtil {
    private ColorUtil() {}

    public static @ColorInt int getForegroundColorForBackgroundColor(int color) {
        if (android.R.color.transparent == color)
            return Color.BLACK;

        int[] rgb = {Color.red(color), Color.green(color), Color.blue(color)};

        int brightness = (int) Math.sqrt(rgb[0] * rgb[0] * .241 + rgb[1]
                * rgb[1] * .691 + rgb[2] * rgb[2] * .068);

        // light color -> use dark color
        if (brightness >= 200) {
            return Color.BLACK;
        }

        return Color.WHITE;
    }
}
