package it.niedermann.nextcloud.deck;

import org.junit.Test;

import it.niedermann.nextcloud.deck.util.MimeTypeUtil;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Local unit tests for MimeType detection
 */
public class MimeTypeUtilTest {
    @Test
    public void isImage() {
        final String[] validMimeTypes = new String[]{
                "image/jpg", "image/Jpg", "image/JPG", "Image/jpg", "IMAGE/jpg", "IMAGE/JPG",
                "image/jpeg", "image/png", "image/tiff", "image/svg",
        };
        final String[] invalidMimeTypes = new String[]{
                "audio/jpg", "img/jpg", "application/octet-stream", "image/djvu", "", null
        };

        for (String validMimeType : validMimeTypes) {
            assertTrue("Expecting " + validMimeType + " to be a valid image mimetype", MimeTypeUtil.isImage(validMimeType));
        }

        for (String invalidMimeType : invalidMimeTypes) {
            assertFalse("Expecting " + invalidMimeType + " to be an invalid image mimetype", MimeTypeUtil.isImage(invalidMimeType));
        }
    }

    @Test
    public void isVideo() {
        final String[] validMimeTypes = new String[]{
                "video/mkv", "video/mp4", "video/mp2", "ViDeO/avi", "VIDEO/mp4", "Video/mp2"
        };
        final String[] invalidMimeTypes = new String[]{
                "audio/jpg", "img/jpg", "application/octet-stream", "", null
        };

        for (String validMimeType : validMimeTypes) {
            assertTrue("Expecting " + validMimeType + " to be a valid video mimetype", MimeTypeUtil.isVideo(validMimeType));
        }

        for (String invalidMimeType : invalidMimeTypes) {
            assertFalse("Expecting " + invalidMimeType + " to be an invalid video mimetype", MimeTypeUtil.isVideo(invalidMimeType));
        }
    }

    @Test
    public void isAudio() {
        final String[] validMimeTypes = new String[]{
                "audio/mp3", "audio/ogg", "audio/vorbis", "audio/flac", "Audio/mp3", "AUDIO/MP3"
        };
        final String[] invalidMimeTypes = new String[]{
                "text/plain", "img/jpg", "application/octet-stream", "", null
        };

        for (String validMimeType : validMimeTypes) {
            assertTrue("Expecting " + validMimeType + " to be a valid audio mimetype", MimeTypeUtil.isAudio(validMimeType));
        }

        for (String invalidMimeType : invalidMimeTypes) {
            assertFalse("Expecting " + invalidMimeType + " to be an invalid audio mimetype", MimeTypeUtil.isAudio(invalidMimeType));
        }
    }

    @Test
    public void isText() {
        final String[] validMimeTypes = new String[]{
                "text/plain", "text/rtf", "text/PLAIN", "Text/Plain", "TEXT/rtf", "TEXT/RTF"
        };
        final String[] invalidMimeTypes = new String[]{
                "audio/jpg", "img/jpg", "application/octet-stream", "", null
        };

        for (String validMimeType : validMimeTypes) {
            assertTrue("Expecting " + validMimeType + " to be a valid text mimetype", MimeTypeUtil.isText(validMimeType));
        }

        for (String invalidMimeType : invalidMimeTypes) {
            assertFalse("Expecting " + invalidMimeType + " to be an invalid text mimetype", MimeTypeUtil.isText(invalidMimeType));
        }
    }

    @Test
    public void isTextPlain() {
        final String[] validMimeTypes = new String[]{
                "text/plain", "TEXT/PLAIN", "Text/Plain"
        };
        final String[] invalidMimeTypes = new String[]{
                "text/rtf", "text/jpg", "", null
        };

        for (String validMimeType : validMimeTypes) {
            assertTrue("Expecting " + validMimeType + " to be a valid text mimetype", MimeTypeUtil.isTextPlain(validMimeType));
        }

        for (String invalidMimeType : invalidMimeTypes) {
            assertFalse("Expecting " + invalidMimeType + " to be an invalid text mimetype", MimeTypeUtil.isTextPlain(invalidMimeType));
        }
    }

    @Test
    public void isContact() {
        final String[] validMimeTypes = new String[]{
                "text/vcard", "TEXT/VCARD", "Text/vCard", "text/VCard"
        };
        final String[] invalidMimeTypes = new String[]{
                "text/plain", "text/rtf", "text/jpg", "", null
        };

        for (String validMimeType : validMimeTypes) {
            assertTrue("Expecting " + validMimeType + " to be a valid text mimetype", MimeTypeUtil.isContact(validMimeType));
        }

        for (String invalidMimeType : invalidMimeTypes) {
            assertFalse("Expecting " + invalidMimeType + " to be an invalid text mimetype", MimeTypeUtil.isContact(invalidMimeType));
        }
    }

    @Test
    public void isPdf() {
        final String[] validMimeTypes = new String[]{
                "application/pdf", "APPLICATION/PDF", "Application/Pdf"
        };
        final String[] invalidMimeTypes = new String[]{
                "audio/jpg", "img/jpg", "application/octet-stream", "app/pdf", "", null
        };

        for (String validMimeType : validMimeTypes) {
            assertTrue("Expecting " + validMimeType + " to be a valid text mimetype", MimeTypeUtil.isPdf(validMimeType));
        }

        for (String invalidMimeType : invalidMimeTypes) {
            assertFalse("Expecting " + invalidMimeType + " to be an invalid text mimetype", MimeTypeUtil.isPdf(invalidMimeType));
        }
    }
}