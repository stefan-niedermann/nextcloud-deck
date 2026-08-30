package it.niedermann.nextcloud.deck.ui.exception.tips;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import it.niedermann.nextcloud.deck.exceptions.UploadAttachmentFailedException;

public class TipsAdapterFileTooLargeTest {

    @Test
    public void detectsServerFileSizeLimitMessage() {
        final Throwable throwable = new UploadAttachmentFailedException(
                "Unknown URI scheme",
                new IllegalStateException("{\"status\":500,\"message\":\"No file uploaded or file size exceeds maximum of 2 MB\"}")
        );
        assertTrue(TipsAdapter.isAttachmentFileTooLarge(throwable));
    }

    @Test
    public void ignoresUnknownUriSchemeWithoutSizeLimit() {
        assertFalse(TipsAdapter.isAttachmentFileTooLarge(
                new UploadAttachmentFailedException("Unknown URI scheme: content")));
    }
}
