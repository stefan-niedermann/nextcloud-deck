package it.niedermann.nextcloud.deck.util;

import android.content.Context;
import android.os.Build;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {Build.VERSION_CODES.P})
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

}
