package it.niedermann.nextcloud.deck.ui.card.attachments;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import java.util.List;

import it.niedermann.nextcloud.deck.databinding.ItemAttachmentDefaultBinding;
import it.niedermann.nextcloud.deck.model.Attachment;

class DefaultAttachmentViewHolder extends AttachmentViewHolder {
    @NonNull
    public final ItemAttachmentDefaultBinding binding;

    DefaultAttachmentViewHolder(@NonNull ItemAttachmentDefaultBinding binding, @NonNull List<Attachment> attachments) {
        super(binding.getRoot(), attachments);
        this.binding = binding;
    }

    @Override
    protected ImageView getPreview() {
        return binding.preview;
    }

    @Override
    protected void setNotSyncedYetStatus(boolean synced) {
        binding.notSyncedYet.setVisibility(synced ? View.GONE : View.VISIBLE);
    }
}