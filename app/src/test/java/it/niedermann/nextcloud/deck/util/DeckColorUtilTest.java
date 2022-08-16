package it.niedermann.nextcloud.deck.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.graphics.Color;

import androidx.core.util.Pair;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;

@RunWith(RobolectricTestRunner.class)
public class DeckColorUtilTest {

    @Test
    public void testContrastRatioIsSufficient() {
        final var sufficientContrastColorPairs = new ArrayList<Pair<Integer, Integer>>();
        sufficientContrastColorPairs.add(new Pair<>(Color.BLACK, Color.WHITE));
        sufficientContrastColorPairs.add(new Pair<>(Color.WHITE, Color.parseColor("#0082C9")));

        for (final var colorPair : sufficientContrastColorPairs) {
            assert colorPair.first != null;
            assert colorPair.second != null;
            assertTrue(
                    "Expect contrast between " + String.format("#%06X", (0xFFFFFF & colorPair.first)) + " and " + String.format("#%06X", (0xFFFFFF & colorPair.second)) + " to be sufficient",
                    DeckColorUtil.contrastRatioIsSufficient(colorPair.first, colorPair.second)
            );
        }

        final var insufficientContrastColorPairs = new ArrayList<Pair<Integer, Integer>>();
        insufficientContrastColorPairs.add(new Pair<>(Color.WHITE, Color.WHITE));
        insufficientContrastColorPairs.add(new Pair<>(Color.BLACK, Color.BLACK));

        for (final var colorPair : insufficientContrastColorPairs) {
            assert colorPair.first != null;
            assert colorPair.second != null;
            assertFalse(
                    "Expect contrast between " + String.format("#%06X", (0xFFFFFF & colorPair.first)) + " and " + String.format("#%06X", (0xFFFFFF & colorPair.second)) + " to be insufficient",
                    DeckColorUtil.contrastRatioIsSufficient(colorPair.first, colorPair.second)
            );
        }
    }

}
