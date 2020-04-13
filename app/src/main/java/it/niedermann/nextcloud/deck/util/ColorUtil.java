package it.niedermann.nextcloud.deck.util;

import android.graphics.Color;

import androidx.annotation.ColorInt;

@SuppressWarnings("WeakerAccess")
public final class ColorUtil {
    private ColorUtil() {
    }

    @ColorInt
    public static int getForegroundColorForBackgroundColor(@ColorInt int color) {
        if (android.R.color.transparent == color)
            return Color.BLACK;
        else if (isColorDark(color))
            return Color.WHITE;
        else
            return Color.BLACK;
    }

    public static boolean isColorDark(@ColorInt int color) {
        return getBrightness(color) < 200;
    }

    public static int getBrightness(@ColorInt int color) {
        final int[] rgb = {Color.red(color), Color.green(color), Color.blue(color)};

        return (int) Math.sqrt(rgb[0] * rgb[0] * .241 + rgb[1]
                * rgb[1] * .691 + rgb[2] * rgb[2] * .068);
    }

    public static double getContrastRatio(@ColorInt int colorOne, @ColorInt int colorTwo) {
        final int brightnessOne = getBrightness(colorOne);
        final int brightnessTwo = getBrightness(colorTwo);

        return (brightnessOne > brightnessTwo)
                ? (double) brightnessOne / (double) brightnessTwo
                : (double) brightnessTwo / (double) brightnessOne;
    }

    public static boolean contrastRatioIsSufficient(@ColorInt int colorOne, @ColorInt int colorTwo) {
        return getContrastRatio(colorOne, colorTwo) > 1.5f;
    }
}
