package it.niedermann.nextcloud.deck.javafx.util;

import java.util.Locale;

public class FileUtil {

    public static String humanReadableSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        }

        String[] units = {"KiB", "MiB", "GiB", "TiB", "PiB", "EiB"};
        double size = bytes;
        int unitIndex = -1;

        do {
            size /= 1024.0;
            unitIndex++;
        } while (size >= 1024 && unitIndex < units.length - 1);

        return String.format(Locale.getDefault(), "%.1f %s", size, units[unitIndex]);
    }
}
