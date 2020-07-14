package it.niedermann.nextcloud.deck.ui.card.attachments;

import android.widget.ImageView;

import it.niedermann.nextcloud.deck.databinding.ItemAttachmentImageBinding;

public class ImageAttachmentViewHolder extends AttachmentViewHolder {
    private ItemAttachmentImageBinding binding;

    @SuppressWarnings("WeakerAccess")
    public ImageAttachmentViewHolder(ItemAttachmentImageBinding binding) {
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