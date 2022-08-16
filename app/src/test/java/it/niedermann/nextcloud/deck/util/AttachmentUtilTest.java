package it.niedermann.nextcloud.deck.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.model.enums.EAttachmentType;

@RunWith(RobolectricTestRunner.class)
public class AttachmentUtilTest {

    @Test
    public void testGetThumbnailUrl() {
        final var accountUrl = "https://example.com";
        final var accountThatDoesSupportFileAttachments = new Account();
        final var accountThatDoesNotSupportFileAttachments = new Account();
        accountThatDoesSupportFileAttachments.setUrl(accountUrl);
        accountThatDoesSupportFileAttachments.setServerDeckVersion("1.3.0");
        accountThatDoesNotSupportFileAttachments.setUrl(accountUrl);
        accountThatDoesNotSupportFileAttachments.setServerDeckVersion("1.2.0");

        final var attachment1 = new Attachment();
        attachment1.setFileId(1337L);
        attachment1.setType(EAttachmentType.FILE);
        final String thumbnailUrl1 = AttachmentUtil.getThumbnailUrl(accountThatDoesSupportFileAttachments, -1L, attachment1, 500);
        assertEquals("https://example.com/index.php/core/preview?fileId=1337&x=500&y=500&a=true", thumbnailUrl1);

        final var attachment2 = new Attachment();
        attachment2.setFileId(815L);
        attachment2.setType(EAttachmentType.FILE);
        final String thumbnailUrl2 = AttachmentUtil.getThumbnailUrl(accountThatDoesSupportFileAttachments, 0L, attachment2, 4711);
        assertEquals("https://example.com/index.php/core/preview?fileId=815&x=4711&y=4711&a=true", thumbnailUrl2);

        // Given there is an invalid fileId...
        final var attachment3 = new Attachment();
        attachment3.setId(999L);
        attachment3.setFileId(null);
        final var thumbnailUrl3 = AttachmentUtil.getThumbnailUrl(accountThatDoesSupportFileAttachments, 15L, attachment3, 205);
        // ... a fallback to the attachment itself should be returned
        assertEquals("https://example.com/index.php/apps/deck/cards/15/attachment/999", thumbnailUrl3);

        // Given the server version does not support file attachments yet...
        final var attachment4 = new Attachment();
        attachment4.setId(111L);
        attachment4.setFileId(222L);
        final var thumbnailUrl4 = AttachmentUtil.getThumbnailUrl(accountThatDoesNotSupportFileAttachments, 333L, attachment4, 444);
        // ... a fallback to the attachment itself should be returned
        assertEquals("https://example.com/index.php/apps/deck/cards/333/attachment/111", thumbnailUrl4);
    }

}
