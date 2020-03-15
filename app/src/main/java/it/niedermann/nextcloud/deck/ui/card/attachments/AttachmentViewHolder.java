package it.niedermann.nextcloud.deck.ui.card.attachments;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import it.niedermann.nextcloud.deck.model.Attachment;

abstract class AttachmentViewHolder extends RecyclerView.ViewHolder {
    @NonNull
    private List<Attachment> attachments = new ArrayList<>();

    AttachmentViewHolder(@NonNull View itemView, @NonNull List<Attachment> attachments) {
        super(itemView);
        this.attachments.addAll(attachments);
    }

    abstract protected ImageView getPreview();

    abstract protected void setNotSyncedYetStatus(boolean synced);

    ItemDetailsLookup.ItemDetails<Long> getItemDetails() {
        return new CardAttachmentDetail(getAdapterPosition(), attachments.get(getAdapterPosition()).getLocalId());
    }

    public void bind(Attachment attachment, boolean selected) {
        itemView.setActivated(selected);
    }
}