package it.niedermann.nextcloud.deck.util;

/**
 * Created by stefan on 07.03.20.
 */

public class AttachmentUtil {

    private AttachmentUtil() {
    }

    public static String getUrl(String accountUrl, long cardRemoteId, long attachmentRemoteId) {
        return accountUrl + "/index.php/apps/deck/cards/" + cardRemoteId + "/attachment/" + attachmentRemoteId;
    }
}
