package it.niedermann.nextcloud.deck.ui.card.attachments;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class AttachmentViewHolder extends RecyclerView.ViewHolder {
    AttachmentViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    abstract protected ImageView getPreview();

    abstract protected void setNotSyncedYetStatus(boolean synced, @ColorInt int color);
}