package it.niedermann.nextcloud.deck.util;

import android.graphics.Color;

import androidx.core.util.Pair;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class DeckColorUtilTest {

    @Test
    public void testContrastRatioIsSufficient() {
        final List<Pair<Integer, Integer>> sufficientContrastColorPairs = new ArrayList<>();
        sufficientContrastColorPairs.add(new Pair<>(Color.BLACK, Color.WHITE));
        sufficientContrastColorPairs.add(new Pair<>(Color.WHITE, Color.parseColor("#0082C9")));

        for (Pair<Integer, Integer> colorPair : sufficientContrastColorPairs) {
            assert colorPair.first != null;
            assert colorPair.second != null;
            assertTrue(
                    "Expect contrast between " + String.format("#%06X", (0xFFFFFF & colorPair.first)) + " and " + String.format("#%06X", (0xFFFFFF & colorPair.second)) + " to be sufficient",
                    DeckColorUtil.contrastRatioIsSufficient(colorPair.first, colorPair.second)
            );
        }

        final List<Pair<Integer, Integer>> insufficientContrastColorPairs = new ArrayList<>();
        insufficientContrastColorPairs.add(new Pair<>(Color.WHITE, Color.WHITE));
        insufficientContrastColorPairs.add(new Pair<>(Color.BLACK, Color.BLACK));

        for (Pair<Integer, Integer> colorPair : insufficientContrastColorPairs) {
            assert colorPair.first != null;
            assert colorPair.second != null;
            assertFalse(
                    "Expect contrast between " + String.format("#%06X", (0xFFFFFF & colorPair.first)) + " and " + String.format("#%06X", (0xFFFFFF & colorPair.second)) + " to be insufficient",
                    DeckColorUtil.contrastRatioIsSufficient(colorPair.first, colorPair.second)
            );
        }
    }

}
