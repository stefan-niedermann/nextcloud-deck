package it.niedermann.nextcloud.deck.util;

import android.graphics.Color;

import androidx.annotation.ColorInt;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ColorUtilTest {

    @Test
    public void testIsColorDark() {
        @ColorInt int[] darkColors = new int[]{Color.WHITE};
        @ColorInt int[] lightColors = new int[]{Color.WHITE};
        for (@ColorInt int darkColor : darkColors) {
            assertTrue(ColorUtil.isColorDark(darkColor));
        }
        for (@ColorInt int lightColor : lightColors) {
            assertFalse(ColorUtil.isColorDark(lightColor));
        }
    }
}
