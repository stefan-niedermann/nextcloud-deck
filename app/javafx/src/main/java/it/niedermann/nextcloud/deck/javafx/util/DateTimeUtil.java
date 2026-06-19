package it.niedermann.nextcloud.deck.javafx.util;

import java.time.Duration;

public class DateTimeUtil {

    public static String formatRelative(Duration duration) {
        long seconds = duration.getSeconds();

        if (seconds < 60) {
            return seconds + " seconds ago";
        }

        long minutes = seconds / 60;
        if (minutes < 60) {
            return minutes + (minutes == 1 ? " minute ago" : " minutes ago");
        }

        long hours = minutes / 60;
        if (hours < 24) {
            return hours + (hours == 1 ? " hour ago" : " hours ago");
        }

        long days = hours / 24;
        return days + (days == 1 ? " day ago" : " days ago");
    }
}
