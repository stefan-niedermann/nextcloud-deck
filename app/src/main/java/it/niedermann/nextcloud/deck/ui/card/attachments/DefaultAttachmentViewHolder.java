package it.niedermann.nextcloud.deck.ui.card.attachments;

import android.widget.ImageView;

import it.niedermann.nextcloud.deck.databinding.ItemAttachmentDefaultBinding;

public class DefaultAttachmentViewHolder extends AttachmentViewHolder {
    protected ItemAttachmentDefaultBinding binding;

    @SuppressWarnings("WeakerAccess")
    public DefaultAttachmentViewHolder(ItemAttachmentDefaultBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    @Override
    protected ImageView getPreview() {
        return binding.preview;
    }

    @Override
    protected ImageView getNotSyncedYetStatusIcon() {
        return binding.notSyncedYet;
    }
}