package it.niedermann.nextcloud.deck.deprecated.ui.card.attachments;

import androidx.annotation.NonNull;

public interface AttachmentInteractionListener {
    void onAttachmentClicked(int position);

    void onAppendToDescription(@NonNull String content);
}