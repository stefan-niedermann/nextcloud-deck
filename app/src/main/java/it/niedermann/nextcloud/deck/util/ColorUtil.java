package it.niedermann.nextcloud.deck.util;

import android.graphics.Color;

import androidx.annotation.ColorInt;

public final class ColorUtil {
    private ColorUtil() {
    }

    @ColorInt
    public static int getForegroundColorForBackgroundColor(@ColorInt int color) {
        if (Color.TRANSPARENT == color)
            return Color.BLACK;
        else if (isColorDark(color))
            return Color.WHITE;
        else
            return Color.BLACK;
    }

    public static boolean isColorDark(@ColorInt int color) {
        return getBrightness(color) < 200;
    }

    private static int getBrightness(@ColorInt int color) {
        final int[] rgb = {Color.red(color), Color.green(color), Color.blue(color)};

        return (int) Math.sqrt(rgb[0] * rgb[0] * .241 + rgb[1]
                * rgb[1] * .691 + rgb[2] * rgb[2] * .068);
    }

    // ---------------------------------------------------
    // Based on https://github.com/LeaVerou/contrast-ratio
    // ---------------------------------------------------

    public static boolean contrastRatioIsSufficient(@ColorInt int colorOne, @ColorInt int colorTwo) {
        return getContrastRatio(colorOne, colorTwo) > 3d;
    }

    private static double getContrastRatio(@ColorInt int colorOne, @ColorInt int colorTwo) {
        final double lum1 = getLuminanace(colorOne);
        final double lum2 = getLuminanace(colorTwo);
        final double brightest = Math.max(lum1, lum2);
        final double darkest = Math.min(lum1, lum2);
        return (brightest + 0.05) / (darkest + 0.05);
    }

    private static double getLuminanace(@ColorInt int color) {
        final int[] rgb = {Color.red(color), Color.green(color), Color.blue(color)};
        return getSubcolorLuminance(rgb[0]) * 0.2126 + getSubcolorLuminance(rgb[1]) * 0.7152 + getSubcolorLuminance(rgb[2]) * 0.0722;
    }

    private static double getSubcolorLuminance(@ColorInt int color) {
        final double value = color / 255d;
        return value <= 0.03928
                ? value / 12.92
                : Math.pow((value + 0.055) / 1.055, 2.4);
    }
}
