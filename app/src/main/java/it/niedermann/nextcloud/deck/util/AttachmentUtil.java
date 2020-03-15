package it.niedermann.nextcloud.deck.util;

import androidx.annotation.Nullable;

/**
 * Created by stefan on 07.03.20.
 */

public class AttachmentUtil {

    private AttachmentUtil() {
    }

    @Nullable
    public static String getUrl(String accountUrl, Long cardRemoteId, Long attachmentRemoteId) {
        return (accountUrl == null || cardRemoteId == null || attachmentRemoteId == null)
                ? null
                : accountUrl + "/index.php/apps/deck/cards/" + cardRemoteId + "/attachment/" + attachmentRemoteId;
    }
}
