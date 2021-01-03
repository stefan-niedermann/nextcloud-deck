package it.niedermann.nextcloud.deck.util;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {Build.VERSION_CODES.P})
public class SpannableUtilTest {

    @Test
    public void testStrong() {
        final SpannableString spannableString = SpannableUtil.strong("test");
        assertEquals(1, spannableString.getSpans(0, spannableString.length(), Object.class).length);
        assertEquals(1, spannableString.getSpans(0, spannableString.length(), StyleSpan.class).length);
        assertEquals(4, spannableString.length());
        assertEquals(Typeface.BOLD, spannableString.getSpans(0, spannableString.length(), StyleSpan.class)[0].getStyle());
    }

    @Test
    public void testDisabled() {
        final Context appContext = ApplicationProvider.getApplicationContext();

        final SpannableString spannableString = SpannableUtil.disabled("test", appContext);
        assertEquals(2, spannableString.getSpans(0, spannableString.length(), Object.class).length);
        assertEquals(1, spannableString.getSpans(0, spannableString.length(), ForegroundColorSpan.class).length);
        assertEquals(1, spannableString.getSpans(0, spannableString.length(), StyleSpan.class).length);
        assertEquals(4, spannableString.length());
        assertEquals(Typeface.ITALIC, spannableString.getSpans(0, spannableString.length(), StyleSpan.class)[0].getStyle());
    }


    @Test
    public void testUrl() {
        final SpannableString spannableString = SpannableUtil.url("test", "https://example.com");
        assertEquals(1, spannableString.getSpans(0, spannableString.length(), Object.class).length);
        assertEquals(1, spannableString.getSpans(0, spannableString.length(), URLSpan.class).length);
        assertEquals(4, spannableString.length());
        assertEquals("https://example.com", spannableString.getSpans(0, spannableString.length(), URLSpan.class)[0].getURL());
    }

}
