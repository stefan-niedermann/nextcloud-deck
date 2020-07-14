package it.niedermann.nextcloud.deck.ui.card.attachments;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import it.niedermann.nextcloud.deck.ui.branding.BrandingUtil;

public abstract class AttachmentViewHolder extends RecyclerView.ViewHolder {
    AttachmentViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    abstract protected ImageView getPreview();

    protected void setNotSyncedYetStatus(boolean synced, @ColorInt int mainColor) {
        final ImageView notSyncedYet = getNotSyncedYetStatusIcon();
        DrawableCompat.setTint(notSyncedYet.getDrawable(), BrandingUtil.getSecondaryForegroundColorDependingOnTheme(notSyncedYet.getContext(), mainColor));
        notSyncedYet.setVisibility(synced ? View.GONE : View.VISIBLE);
    }

    abstract protected ImageView getNotSyncedYetStatusIcon();
}