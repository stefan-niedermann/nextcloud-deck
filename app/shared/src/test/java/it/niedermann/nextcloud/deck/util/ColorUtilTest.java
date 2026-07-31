package it.niedermann.nextcloud.deck.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class ColorUtilTest {

    private ColorUtil colorUtil;

    private static final int COLOR_WHITE = 0xFFFFFFFF;
    private static final int COLOR_BLACK = 0xFF000000;
    private static final int COLOR_TRANSPARENT = 0x00000000;
    private static final int COLOR_YELLOW = 0xFFFFFF00;

    private static final int[] DARK_COLORS = {
            COLOR_BLACK,
            0xFF0082C9, // #0082C9
            0xFF007676  // #007676
    };

    private static final int[] LIGHT_COLORS = {
            COLOR_WHITE,
            COLOR_YELLOW
    };

    @BeforeEach
    void setUp() {
        colorUtil = new ColorUtil();
    }

    @Test
    void testGetForegroundColorForBackgroundColor() {
        for (int color : DARK_COLORS) {
            assertEquals(
                    COLOR_WHITE,
                    colorUtil.getForegroundColorForBackgroundColor(color),
                    "Expect foreground color for " + String.format("#%06X", 0xFFFFFF & color) + " to be WHITE"
            );
        }
        for (int color : LIGHT_COLORS) {
            assertEquals(
                    COLOR_BLACK,
                    colorUtil.getForegroundColorForBackgroundColor(color),
                    "Expect foreground color for " + String.format("#%06X", 0xFFFFFF & color) + " to be BLACK"
            );
        }
        assertEquals(
                COLOR_BLACK,
                colorUtil.getForegroundColorForBackgroundColor(COLOR_TRANSPARENT),
                "Expect foreground color for TRANSPARENT to be BLACK"
        );
    }

    @Test
    void testIsColorDark() {
        for (int color : DARK_COLORS) {
            assertTrue(
                    colorUtil.isColorDark(color),
                    "Expect " + String.format("#%06X", 0xFFFFFF & color) + " to be a dark color"
            );
        }
        for (int color : LIGHT_COLORS) {
            assertFalse(
                    colorUtil.isColorDark(color),
                    "Expect " + String.format("#%06X", 0xFFFFFF & color) + " to be a light color"
            );
        }
    }

    @Test
    void testIntColorToHexString() {
        assertEquals("ffffff", colorUtil.intColorToHexString(COLOR_WHITE));
        assertEquals("000000", colorUtil.intColorToHexString(COLOR_BLACK));
        assertEquals("0082c9", colorUtil.intColorToHexString(colorUtil.parseColor("#0082C9")));
    }

    @Test
    void testGetCleanHexColorString() {
        record Pair(String first, String second) {}
        List<Pair> validColors = new ArrayList<>();
        validColors.add(new Pair("#0082C9", "#0082C9"));
        validColors.add(new Pair("0082C9", "#0082C9"));
        validColors.add(new Pair("#CCC", "#CCCCCC"));
        validColors.add(new Pair("ccc", "#cccccc"));
        validColors.add(new Pair("af0", "#aaff00"));
        validColors.add(new Pair("#af0", "#aaff00"));
        // Strip alpha channel
        validColors.add(new Pair("af05", "#aaff00"));
        validColors.add(new Pair("#af05", "#aaff00"));
        validColors.add(new Pair("aaff0055", "#aaff00"));
        validColors.add(new Pair("#aaff0055", "#aaff00"));

        for (Pair color : validColors) {
            assertEquals(
                    color.second,
                    colorUtil.formatColorToParsableHexString(color.first),
                    "Expect " + color.first + " to be cleaned up to " + color.second
            );
        }

        assertThrows(NullPointerException.class, () -> {
            colorUtil.formatColorToParsableHexString(null);
        });

        String[] invalidColors = {"", "cc", "c", "#a", "#55L", "55L"};
        for (String color : invalidColors) {
            assertThrows(IllegalArgumentException.class, () -> {
                colorUtil.formatColorToParsableHexString(color);
            });
        }
    }
}
