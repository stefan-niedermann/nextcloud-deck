package it.niedermann.nextcloud.deck.deprecated.ui.card.attachments;

import it.niedermann.nextcloud.deck.model.Attachment;

public interface AttachmentDeletedListener {
    void onAttachmentDeleted(Attachment attachment);
}