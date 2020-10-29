package it.niedermann.nextcloud.deck.ui.card.attachments;

import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.CallSuper;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import it.niedermann.android.util.ClipboardUtil;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.model.enums.DBStatus;
import it.niedermann.nextcloud.deck.ui.branding.BrandingUtil;
import it.niedermann.nextcloud.deck.util.AttachmentUtil;

public abstract class AttachmentViewHolder extends RecyclerView.ViewHolder {
    AttachmentViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void bind(@NonNull Account account, @NonNull MenuInflater menuInflater, @NonNull FragmentManager fragmentManager, Long cardRemoteId, Attachment attachment, @Nullable View.OnClickListener onClickListener, @ColorInt int mainColor) {
        bind(menuInflater, fragmentManager, cardRemoteId, attachment, onClickListener, mainColor, AttachmentUtil.getRemoteOrLocalUrl(account.getUrl(), cardRemoteId, attachment));
    }

    @CallSuper
    public void bind(@NonNull MenuInflater menuInflater, @NonNull FragmentManager fragmentManager, Long cardRemoteId, Attachment attachment, @Nullable View.OnClickListener onClickListener, @ColorInt int mainColor, @Nullable String attachmentUri) {
        setNotSyncedYetStatus(!DBStatus.LOCAL_EDITED.equals(attachment.getStatusEnum()), mainColor);
        itemView.setOnCreateContextMenuListener((menu, v, menuInfo) -> {
            menuInflater.inflate(R.menu.attachment_menu, menu);
            menu.findItem(R.id.delete).setOnMenuItemClickListener(item -> {
                DeleteAttachmentDialogFragment.newInstance(attachment).show(fragmentManager, DeleteAttachmentDialogFragment.class.getCanonicalName());
                return false;
            });
            if (attachmentUri == null || attachment.getId() == null || cardRemoteId == null) {
                menu.findItem(android.R.id.copyUrl).setVisible(false);
            } else {
                menu.findItem(android.R.id.copyUrl).setVisible(true);
                menu.findItem(android.R.id.copyUrl).setOnMenuItemClickListener(item -> ClipboardUtil.INSTANCE.copyToClipboard(itemView.getContext(), attachment.getFilename(), attachmentUri));
            }
        });
    }

    abstract protected ImageView getPreview();

    protected void setNotSyncedYetStatus(boolean synced, @ColorInt int mainColor) {
        final ImageView notSyncedYet = getNotSyncedYetStatusIcon();
        DrawableCompat.setTint(notSyncedYet.getDrawable(), BrandingUtil.getSecondaryForegroundColorDependingOnTheme(notSyncedYet.getContext(), mainColor));
        notSyncedYet.setVisibility(synced ? View.GONE : View.VISIBLE);
    }

    abstract protected ImageView getNotSyncedYetStatusIcon();
}