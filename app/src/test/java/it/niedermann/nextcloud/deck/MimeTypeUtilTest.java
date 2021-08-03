package it.niedermann.nextcloud.deck;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import it.niedermann.nextcloud.deck.util.MimeTypeUtil;

/**
 * Local unit tests for MimeType detection
 */
public class MimeTypeUtilTest {
    @Test
    public void isImage() {
        final var validMimeTypes = new String[]{
                "image/jpg", "image/Jpg", "image/JPG", "Image/jpg", "IMAGE/jpg", "IMAGE/JPG",
                "image/jpeg", "image/png", "image/tiff", "image/svg",
        };
        final var invalidMimeTypes = new String[]{
                "audio/jpg", "img/jpg", "application/octet-stream", "image/djvu", "", null
        };

        for (final var validMimeType : validMimeTypes) {
            assertTrue("Expecting " + validMimeType + " to be a valid image mimetype", MimeTypeUtil.isImage(validMimeType));
        }

        for (final var invalidMimeType : invalidMimeTypes) {
            assertFalse("Expecting " + invalidMimeType + " to be an invalid image mimetype", MimeTypeUtil.isImage(invalidMimeType));
        }
    }

    @Test
    public void isVideo() {
        final var validMimeTypes = new String[]{
                "video/mkv", "video/mp4", "video/mp2", "ViDeO/avi", "VIDEO/mp4", "Video/mp2"
        };
        final var invalidMimeTypes = new String[]{
                "audio/jpg", "img/jpg", "application/octet-stream", "", null
        };

        for (final var validMimeType : validMimeTypes) {
            assertTrue("Expecting " + validMimeType + " to be a valid video mimetype", MimeTypeUtil.isVideo(validMimeType));
        }

        for (final var invalidMimeType : invalidMimeTypes) {
            assertFalse("Expecting " + invalidMimeType + " to be an invalid video mimetype", MimeTypeUtil.isVideo(invalidMimeType));
        }
    }

    @Test
    public void isAudio() {
        final var validMimeTypes = new String[]{
                "audio/mp3", "audio/ogg", "audio/vorbis", "audio/flac", "Audio/mp3", "AUDIO/MP3"
        };
        final var invalidMimeTypes = new String[]{
                "text/plain", "img/jpg", "application/octet-stream", "", null
        };

        for (final var validMimeType : validMimeTypes) {
            assertTrue("Expecting " + validMimeType + " to be a valid audio mimetype", MimeTypeUtil.isAudio(validMimeType));
        }

        for (final var invalidMimeType : invalidMimeTypes) {
            assertFalse("Expecting " + invalidMimeType + " to be an invalid audio mimetype", MimeTypeUtil.isAudio(invalidMimeType));
        }
    }

    @Test
    public void isText() {
        final var validMimeTypes = new String[]{
                "text/plain", "text/rtf", "text/PLAIN", "Text/Plain", "TEXT/rtf", "TEXT/RTF"
        };
        final var invalidMimeTypes = new String[]{
                "audio/jpg", "img/jpg", "application/octet-stream", "", null
        };

        for (final var validMimeType : validMimeTypes) {
            assertTrue("Expecting " + validMimeType + " to be a valid text mimetype", MimeTypeUtil.isText(validMimeType));
        }

        for (final var invalidMimeType : invalidMimeTypes) {
            assertFalse("Expecting " + invalidMimeType + " to be an invalid text mimetype", MimeTypeUtil.isText(invalidMimeType));
        }
    }

    @Test
    public void isTextPlain() {
        final var validMimeTypes = new String[]{
                "text/plain", "TEXT/PLAIN", "Text/Plain"
        };
        final var invalidMimeTypes = new String[]{
                "text/rtf", "text/jpg", "", null
        };

        for (final var validMimeType : validMimeTypes) {
            assertTrue("Expecting " + validMimeType + " to be a valid text mimetype", MimeTypeUtil.isTextPlain(validMimeType));
        }

        for (final var invalidMimeType : invalidMimeTypes) {
            assertFalse("Expecting " + invalidMimeType + " to be an invalid text mimetype", MimeTypeUtil.isTextPlain(invalidMimeType));
        }
    }

    @Test
    public void isContact() {
        final var validMimeTypes = new String[]{
                "text/vcard", "TEXT/VCARD", "Text/vCard", "text/VCard"
        };
        final var invalidMimeTypes = new String[]{
                "text/plain", "text/rtf", "text/jpg", "", null
        };

        for (final var validMimeType : validMimeTypes) {
            assertTrue("Expecting " + validMimeType + " to be a valid text mimetype", MimeTypeUtil.isContact(validMimeType));
        }

        for (final var invalidMimeType : invalidMimeTypes) {
            assertFalse("Expecting " + invalidMimeType + " to be an invalid text mimetype", MimeTypeUtil.isContact(invalidMimeType));
        }
    }

    @Test
    public void isPdf() {
        final var validMimeTypes = new String[]{
                "application/pdf", "APPLICATION/PDF", "Application/Pdf"
        };
        final var invalidMimeTypes = new String[]{
                "audio/jpg", "img/jpg", "application/octet-stream", "app/pdf", "", null
        };

        for (final var validMimeType : validMimeTypes) {
            assertTrue("Expecting " + validMimeType + " to be a valid text mimetype", MimeTypeUtil.isPdf(validMimeType));
        }

        for (final var invalidMimeType : invalidMimeTypes) {
            assertFalse("Expecting " + invalidMimeType + " to be an invalid text mimetype", MimeTypeUtil.isPdf(invalidMimeType));
        }
    }
}