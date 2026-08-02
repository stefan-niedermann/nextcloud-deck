package it.niedermann.nextcloud.deck.util;

public interface ColorHarmonizer {
    /**
     * Harmonizes the given color with the key color.
     *
     * @param color    The color to harmonize.
     * @param keyColor The key color to harmonize with (usually the theme's primary color).
     * @return The harmonized color as an ARGB integer.
     */
    int harmonize(int color, int keyColor);
}
