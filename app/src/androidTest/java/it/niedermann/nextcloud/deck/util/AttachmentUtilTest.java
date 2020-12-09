package it.niedermann.nextcloud.deck.util;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.model.ocs.Version;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class AttachmentUtilTest {

    @Test
    public void testGetThumbnailUrl() {
        final Version versionThatDoesSupportFileAttachments = new Version("1.3.0", 1, 3, 0);
        final Version versionThatDoesNotSupportFileAttachments = new Version("1.2.0", 1, 2, 0);
        final String accountUrl = "https://example.com";

        // TODO depends on https://github.com/nextcloud/deck/pull/2638
//        final Attachment attachment1 = new Attachment();
//        attachment1.setFileId("1337");
//        final String thumbnailUrl1 = AttachmentUtil.getThumbnailUrl(versionThatDoesSupportFileAttachments, accountUrl, -1L, attachment1, 500);
//        assertEquals("https://example.com/index.php/core/preview?fileId=1337&x=500&y=500", thumbnailUrl1);
//
//        final Attachment attachment2 = new Attachment();
//        attachment2.setFileId("0815");
//        final String thumbnailUrl2 = AttachmentUtil.getThumbnailUrl(versionThatDoesSupportFileAttachments, accountUrl, 0L, attachment2, 4711);
//        assertEquals("https://example.com/index.php/core/preview?fileId=0815&x=4711&y=4711", thumbnailUrl2);

        // Given there is an invalid fileId...
        final Attachment attachment3 = new Attachment();
        attachment3.setId(999L);
        attachment3.setFileId("");
        final String thumbnailUrl3 = AttachmentUtil.getThumbnailUrl(versionThatDoesSupportFileAttachments, accountUrl, 15L, attachment3, 205);
        // ... a fallback to the attachment itself should be returned
        assertEquals("https://example.com/index.php/apps/deck/cards/15/attachment/999", thumbnailUrl3);

        // Given the server version does not support file attachments yet...
        final Attachment attachment4 = new Attachment();
        attachment4.setId(111L);
        attachment4.setFileId("222");
        final String thumbnailUrl4 = AttachmentUtil.getThumbnailUrl(versionThatDoesNotSupportFileAttachments, accountUrl, 333L, attachment4, 444);
        // ... a fallback to the attachment itself should be returned
        assertEquals("https://example.com/index.php/apps/deck/cards/333/attachment/111", thumbnailUrl4);
    }

}
