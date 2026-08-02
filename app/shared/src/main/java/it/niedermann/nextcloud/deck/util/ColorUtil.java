package it.niedermann.nextcloud.deck.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import it.niedermann.nextcloud.deck.domain.model.Color;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/**
 * Based on https://github.com/LeaVerou/contrast-ratio
 */
@Singleton
public class ColorUtil {

    private final Map<Integer, Integer> foregroundCache = new HashMap<>();
    private final Map<Integer, Boolean> isDarkColorCache = new HashMap<>();

    private ColorHarmonizer harmonizer;

    private static final int BLACK = 0xFF000000;
    private static final int WHITE = 0xFFFFFFFF;
    private static final int TRANSPARENT = 0x00000000;

    @Inject
    public ColorUtil() {
        // Static utility class
    }

    public void setHarmonizer(ColorHarmonizer harmonizer) {
        this.harmonizer = harmonizer;
    }

    public int harmonize(int color, int keyColor) {
        if (harmonizer != null) {
            return harmonizer.harmonize(color, keyColor);
        }
        return color;
    }

    public int getForegroundColorForBackgroundColor(int color) {
        Integer ret = foregroundCache.get(color);
        if (ret == null) {
            ret = (TRANSPARENT == color) ? BLACK : (isColorDark(color) ? WHITE : BLACK);
            foregroundCache.put(color, ret);
        }
        return ret;
    }

    public boolean isColorDark(int color) {
        return isDarkColorCache.computeIfAbsent(color, c -> getBrightness(c) < 200);
    }

    private int getBrightness(int color) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        return (int) Math.sqrt(
                r * r * .241 + (g * g * .691) + b * b * .068
        );
    }

    private double getContrastRatio(int colorOne, int colorTwo) {
        double lum1 = getLuminance(colorOne);
        double lum2 = getLuminance(colorTwo);
        double brightest = Math.max(lum1, lum2);
        double darkest = Math.min(lum1, lum2);
        return (brightest + 0.05) / (darkest + 0.05);
    }

    private double getLuminance(int color) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        return getSubcolorLuminance(r) * 0.2126 + getSubcolorLuminance(g) * 0.7152 + getSubcolorLuminance(b) * 0.0722;
    }

    private double getSubcolorLuminance(int color) {
        double value = color / 255.0;
        return (value <= 0.03928) ? (value / 12.92) : Math.pow((value + 0.055) / 1.055, 2.4);
    }

    /**
     * @return well formatted string starting with a hash followed by 6 hex numbers that is parsable by parseColor.
     */
    protected String formatColorToParsableHexString(String input) {
        Objects.requireNonNull(input, "input color string is null");
        if (isParsableValidHexColorString(input)) {
            return input;
        }
        char[] chars = input.replace("#", "").toCharArray();
        StringBuilder sb = new StringBuilder(7).append("#");
        switch (chars.length) {
            case 8:
                // Strip alpha channel
                sb.append(new String(chars, 0, 6));
                break;
            case 6:
                // Default long
                sb.append(chars);
                break;
            case 4:
                // Strip alpha channel
                for (int i = 0; i < 3; i++) {
                    sb.append(chars[i]).append(chars[i]);
                }
                break;
            case 3:
                // Default short
                for (char c : chars) {
                    sb.append(c).append(c);
                }
                break;
            default:
                throw new IllegalArgumentException("unparsable color string: \"" + input + "\"");
        }
        String formattedHexColor = sb.toString();
        if (isParsableValidHexColorString(formattedHexColor)) {
            return formattedHexColor;
        } else {
            throw new IllegalArgumentException("\"" + input + "\" is not a valid color string. Result of tried normalizing: " + formattedHexColor);
        }
    }

    private boolean isParsableValidHexColorString(String input) {
        try {
            parseColor(input);
            return input.matches("#[a-fA-F0-9]{6}");
        } catch (Exception e) {
            return false;
        }
    }

    public int parseColor(String colorString) {
        if (colorString.charAt(0) == '#') {
            // Use a long to avoid overflow on the high bit, then cast to int
            long color = Long.parseLong(colorString.substring(1), 16);
            if (colorString.length() == 7) {
                // Set the alpha value
                color |= 0xFF000000L;
            } else if (colorString.length() != 9) {
                throw new IllegalArgumentException("Unknown color");
            }
            return (int) color;
        }
        throw new IllegalArgumentException("Unknown color");
    }

    /**
     * Formats the given color to a 6 digit lowercase string *without* leading # character
     */
    public String intColorToHexString(int color) {
        return String.format(Locale.getDefault(), "%06x", 0xFFFFFF & color);
    }

    /**
     * Formats the given color to a web-compatible hex string (e.g. #RRGGBB)
     */
    public String toWebColor(int color) {
        return "#" + intColorToHexString(color);
    }

    /**
     * Formats the given domain Color to a web-compatible hex string (e.g. #RRGGBB)
     */
    public String toWebColor(Color color) {
        return toWebColor(color.argb());
    }
}
