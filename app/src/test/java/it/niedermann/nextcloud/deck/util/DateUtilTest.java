package it.niedermann.nextcloud.deck.util;

import android.content.Context;
import android.os.Build;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.util.ReflectionHelpers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(RobolectricTestRunner.class)
public class DateUtilTest {

    @Test
    public void testGetRelativeDateTimeString() {
        final Context appContext = ApplicationProvider.getApplicationContext();

        Stream.of(10, 20, 30, 40, 50)
                .map(secondsAgo -> ZonedDateTime.now().minus(Duration.ofSeconds(secondsAgo)).toInstant().toEpochMilli())
                .forEach(secondsAgoInMillis -> assertEquals("Below one minute diff, it should just print \"seconds\"", "seconds ago", DateUtil.getRelativeDateTimeString(appContext, secondsAgoInMillis)));

        // TODO Robolectric implementation seems to behave different from emulated device
//        Stream.of(10, 20, 30, 40, 50)
//                .forEach(minutesAgo -> assertEquals("Minutes ago should print the minutes", minutesAgo + " minutes ago", DateUtil.getRelativeDateTimeString(appContext, ZonedDateTime.now().minus(Duration.ofMinutes(minutesAgo)).toInstant().toEpochMilli())));

        assertEquals("Very long time ago should print the complete date", "4/13/2018", DateUtil.getRelativeDateTimeString(appContext, ZonedDateTime.of(2018,
                4, 13, 10, 45, 0, 0, ZoneId.systemDefault()).toInstant().toEpochMilli()));
    }

    @SuppressWarnings({"unchecked", "ConstantConditions", "OptionalGetWithoutIsPresent"})
    @Test
    public void testGetRelativeDateStringWithoutTime() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Method m = DateUtil.class.getDeclaredMethod("getRelativeDateStringWithoutTime", String.class);
        m.setAccessible(true);

        assertFalse("Should not modify unknown formats", ((Optional<String>) m.invoke(null, "blah")).isPresent());

        Locale.setDefault(Locale.FRANCE);
        assertEquals("Should handle french date times with comma", "12 juin",  ((Optional<String>) m.invoke(null, "12 juin, à 07:26")).get());

        final int defaultSdk = Build.VERSION.SDK_INT;
        ReflectionHelpers.setStaticField(Build.VERSION.class, "SDK_INT", 30);
        assertEquals("Should handle french date times without comma", "12 juin",  ((Optional<String>) m.invoke(null, "12 juin à 07:26")).get());
        assertEquals("Should handle french date times without comma", "10 avr.",  ((Optional<String>) m.invoke(null, "10 avr. à 00:00")).get());
        ReflectionHelpers.setStaticField(Build.VERSION.class, "SDK_INT", defaultSdk);

        Locale.setDefault(Locale.GERMANY);
        assertEquals("Should handle german formats", "12. Juli",  ((Optional<String>) m.invoke(null, "12. Juli, 13:46")).get());
        assertEquals("Should handle german formats", "28. Feb.",  ((Optional<String>) m.invoke(null, "28. Feb., 00:43")).get());
    }
}
