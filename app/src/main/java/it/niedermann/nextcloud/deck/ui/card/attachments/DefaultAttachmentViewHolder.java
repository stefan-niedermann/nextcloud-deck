package it.niedermann.nextcloud.deck.ui.card.attachments;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.ColorInt;
import androidx.core.graphics.drawable.DrawableCompat;

import it.niedermann.nextcloud.deck.databinding.ItemAttachmentDefaultBinding;

public class DefaultAttachmentViewHolder extends AttachmentViewHolder {
    ItemAttachmentDefaultBinding binding;

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
    protected void setNotSyncedYetStatus(boolean synced, @ColorInt int mainColor) {
        DrawableCompat.setTint(binding.notSyncedYet.getDrawable(), mainColor);
        binding.notSyncedYet.setVisibility(synced ? View.GONE : View.VISIBLE);
    }
}