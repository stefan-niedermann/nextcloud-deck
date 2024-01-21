package it.niedermann.nextcloud.deck.ui.card.attachments;

import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.CallSuper;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nextcloud.android.common.ui.theme.utils.ColorRole;

import it.niedermann.android.util.ClipboardUtil;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.model.enums.DBStatus;
import it.niedermann.nextcloud.deck.model.enums.EAttachmentType;
import it.niedermann.nextcloud.deck.ui.theme.ThemeUtils;
import it.niedermann.nextcloud.deck.util.AttachmentUtil;

public abstract class AttachmentViewHolder extends RecyclerView.ViewHolder {
    AttachmentViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void bind(@NonNull Account account, @NonNull MenuInflater menuInflater, @NonNull FragmentManager fragmentManager, Long cardRemoteId, Attachment attachment, @Nullable View.OnClickListener onClickListener, @ColorInt int color) {
        final String attachmentUri = (attachment.getId() == null || cardRemoteId == null)
                ? attachment.getLocalPath()
                : AttachmentUtil.getCopyDownloadUrl(account, cardRemoteId, attachment);

        final var synced = !DBStatus.LOCAL_EDITED.equals(attachment.getStatusEnum());
        getNotSyncedYetStatusIcon().setVisibility(synced ? View.GONE : View.VISIBLE);
        itemView.setOnCreateContextMenuListener((menu, v, menuInfo) -> {
            menuInflater.inflate(R.menu.attachment_menu, menu);
            if (EAttachmentType.DECK_FILE.equals(attachment.getType())) {
                menu.findItem(R.id.delete).setOnMenuItemClickListener(item -> {
                    DeleteAttachmentDialogFragment.newInstance(attachment).show(fragmentManager, DeleteAttachmentDialogFragment.class.getCanonicalName());
                    return false;
                });
                menu.findItem(R.id.delete).setVisible(true);
            } else {
                menu.findItem(R.id.delete).setVisible(false);
            }
            if (attachmentUri == null || attachment.getId() == null || cardRemoteId == null) {
                menu.findItem(android.R.id.copyUrl).setVisible(false);
            } else {
                menu.findItem(android.R.id.copyUrl).setVisible(true);
                menu.findItem(android.R.id.copyUrl).setOnMenuItemClickListener(item -> ClipboardUtil.copyToClipboard(itemView.getContext(), attachment.getFilename(), attachmentUri));
            }
        });

        applyTheme(color);
    }

    @CallSuper
    protected void applyTheme(@ColorInt int color) {
        final var utils = ThemeUtils.of(color, getPreview().getContext());

        utils.platform.colorImageView(getNotSyncedYetStatusIcon(), ColorRole.PRIMARY);
    }

    abstract protected ImageView getPreview();

    abstract protected ImageView getNotSyncedYetStatusIcon();
}