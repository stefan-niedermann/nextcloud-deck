package it.niedermann.nextcloud.deck.ui.attachments;

import androidx.recyclerview.widget.RecyclerView;

import it.niedermann.nextcloud.deck.databinding.ItemAttachmentBinding;

public class AttachmentViewHolder extends RecyclerView.ViewHolder {
    public ItemAttachmentBinding binding;

    @SuppressWarnings("WeakerAccess")
    public AttachmentViewHolder(ItemAttachmentBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}