package it.niedermann.nextcloud.deck.ui.card.attachments;

import androidx.annotation.NonNull;

public interface AttachmentInteractionListener {
    void onAttachmentClicked(int position);

    void onAppendToDescription(@NonNull String content);
}