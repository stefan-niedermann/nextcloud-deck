package it.niedermann.nextcloud.deck.util;

import java.util.Locale;

public class MimeTypeUtil {

    public static final String TEXT_PLAIN = "text/plain";
    public static final String APPLICATION_OCTET_STREAM = "application/octet-stream";

    /**
     * @return 'True' if the mime type defines image
     */
    public static boolean isImage(String mimeType) {
        return mimeType != null && mimeType.toLowerCase(Locale.ROOT).startsWith("image/") &&
                !mimeType.toLowerCase(Locale.ROOT).contains("djvu");
    }

    /**
     * @return 'True' the mime type defines video
     */
    public static boolean isVideo(String mimeType) {
        return mimeType != null && mimeType.toLowerCase(Locale.ROOT).startsWith("video/");
    }

    /**
     * @return 'True' the mime type defines audio
     */
    public static boolean isAudio(String mimeType) {
        return mimeType != null && mimeType.toLowerCase(Locale.ROOT).startsWith("audio/");
    }

    /**
     * @return 'True' if mime type defines text
     */
    public static boolean isText(String mimeType) {
        return mimeType != null && mimeType.toLowerCase(Locale.ROOT).startsWith("text/");
    }
}
