package it.niedermann.nextcloud.deck.ui.card.attachments;

import it.niedermann.nextcloud.deck.model.Attachment;

public interface AttachmentDeletedListener {
    void onAttachmentDeleted(Attachment attachment);
}