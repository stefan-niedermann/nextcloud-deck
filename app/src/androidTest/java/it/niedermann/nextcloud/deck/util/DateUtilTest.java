package it.niedermann.nextcloud.deck.util;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class DateUtilTest {

    @Test
    public void testGetRelativeDateTimeString() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Stream.of(10, 20, 30, 40, 50)
                .map(secondsAgo -> ZonedDateTime.now().minus(Duration.ofSeconds(secondsAgo)).toInstant().toEpochMilli())
                .forEach(secondsAgoInMillis -> assertEquals("Below one minute diff, it should just print \"seconds\"", "seconds ago", DateUtil.getRelativeDateTimeString(appContext, secondsAgoInMillis)));

        Stream.of(10, 20, 30, 40, 50)
                .forEach(minutesAgo -> assertEquals("Minutes ago should print the minutes", minutesAgo + " minutes ago", DateUtil.getRelativeDateTimeString(appContext, ZonedDateTime.now().minus(Duration.ofMinutes(minutesAgo)).toInstant().toEpochMilli())));

        assertEquals("Very long time ago should print the complete date", "4/13/2018", DateUtil.getRelativeDateTimeString(appContext, ZonedDateTime.of(2018,
                4, 13, 10, 45, 0, 0, ZoneId.systemDefault()).toInstant().toEpochMilli()));
    }

}
