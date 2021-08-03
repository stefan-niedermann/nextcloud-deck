package it.niedermann.nextcloud.deck.util;

import static org.junit.Assert.assertEquals;

import android.graphics.Typeface;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class SpannableUtilTest {

    @Test
    public void testStrong() {
        final var spannableString = SpannableUtil.strong("test");
        assertEquals(1, spannableString.getSpans(0, spannableString.length(), Object.class).length);
        assertEquals(1, spannableString.getSpans(0, spannableString.length(), StyleSpan.class).length);
        assertEquals(4, spannableString.length());
        assertEquals(Typeface.BOLD, spannableString.getSpans(0, spannableString.length(), StyleSpan.class)[0].getStyle());
    }

    @Test
    public void testDisabled() {
        final var context = ApplicationProvider.getApplicationContext();

        final var spannableString = SpannableUtil.disabled("test", context);
        assertEquals(2, spannableString.getSpans(0, spannableString.length(), Object.class).length);
        assertEquals(1, spannableString.getSpans(0, spannableString.length(), ForegroundColorSpan.class).length);
        assertEquals(1, spannableString.getSpans(0, spannableString.length(), StyleSpan.class).length);
        assertEquals(4, spannableString.length());
        assertEquals(Typeface.ITALIC, spannableString.getSpans(0, spannableString.length(), StyleSpan.class)[0].getStyle());
    }


    @Test
    public void testUrl() {
        final var spannableString = SpannableUtil.url("test", "https://example.com");
        assertEquals(1, spannableString.getSpans(0, spannableString.length(), Object.class).length);
        assertEquals(1, spannableString.getSpans(0, spannableString.length(), URLSpan.class).length);
        assertEquals(4, spannableString.length());
        assertEquals("https://example.com", spannableString.getSpans(0, spannableString.length(), URLSpan.class)[0].getURL());
    }

}
