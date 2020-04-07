package it.niedermann.nextcloud.deck.ui.card.attachments;

import it.niedermann.nextcloud.deck.model.Attachment;

public interface NewCardAttachmentHandler {
    void attachmentAdded(Attachment attachment);

    void attachmentRemoved(Attachment attachment);
}