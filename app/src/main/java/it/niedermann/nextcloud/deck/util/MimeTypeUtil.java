package it.niedermann.nextcloud.deck.util;

import androidx.annotation.Nullable;

import java.util.Locale;

public class MimeTypeUtil {

    public static final String IMAGE_JPEG = "image/jpeg";
    public static final String TEXT_PLAIN = "text/plain";
    public static final String TEXT_VCARD = "text/vcard";
    public static final String APPLICATION_PDF = "application/pdf";

    public static boolean isImage(@Nullable String mimeType) {
        return mimeType != null && mimeType.toLowerCase(Locale.ROOT).startsWith("image/") &&
                !mimeType.toLowerCase(Locale.ROOT).contains("djvu");
    }

    public static boolean isVideo(@Nullable String mimeType) {
        return mimeType != null && mimeType.toLowerCase(Locale.ROOT).startsWith("video/");
    }

    public static boolean isAudio(@Nullable String mimeType) {
        return mimeType != null && mimeType.toLowerCase(Locale.ROOT).startsWith("audio/");
    }

    public static boolean isText(@Nullable String mimeType) {
        return mimeType != null && mimeType.toLowerCase(Locale.ROOT).startsWith("text/");
    }

    public static boolean isTextPlain(@Nullable String mimeType) {
        return mimeType != null && mimeType.toLowerCase(Locale.ROOT).startsWith(TEXT_PLAIN);
    }

    public static boolean isContact(@Nullable String mimeType) {
        return mimeType != null && mimeType.toLowerCase(Locale.ROOT).startsWith(TEXT_VCARD);
    }

    public static boolean isPdf(@Nullable String mimeType) {
        return mimeType != null && mimeType.toLowerCase(Locale.ROOT).startsWith(APPLICATION_PDF);
    }
}
