package it.niedermann.nextcloud.deck.ui.card.attachments;

import android.net.Uri;
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

import java.util.Optional;
import java.util.function.Consumer;

import it.niedermann.android.markdown.MarkdownUtil;
import it.niedermann.android.util.ClipboardUtil;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.model.enums.DBStatus;
import it.niedermann.nextcloud.deck.ui.theme.ThemeUtils;
import it.niedermann.nextcloud.deck.util.AttachmentUtil;

public abstract class AttachmentViewHolder extends RecyclerView.ViewHolder {

    AttachmentViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void bind(@NonNull Account account,
                     @NonNull MenuInflater menuInflater,
                     @NonNull FragmentManager fragmentManager,
                     @Nullable Long cardRemoteId,
                     Attachment attachment,
                     @Nullable View.OnClickListener onClickListener,
                     @NonNull Consumer<String> onAppendToDescription,
                     @ColorInt int color) {
        final var synced = !DBStatus.LOCAL_EDITED.equals(attachment.getStatusEnum());
        getNotSyncedYetStatusIcon().setVisibility(synced ? View.GONE : View.VISIBLE);

        final var uri = AttachmentUtil.getRemoteUrl(account, cardRemoteId, attachment)
                .map(Uri::toString);

        itemView.setOnCreateContextMenuListener((menu, v, menuInfo) -> {
            menuInflater.inflate(R.menu.attachment_menu, menu);
            if (uri.isPresent()) {
                menu.findItem(android.R.id.copyUrl).setVisible(true);
                menu.findItem(android.R.id.copyUrl).setOnMenuItemClickListener(item -> ClipboardUtil.copyToClipboard(itemView.getContext(), attachment.getFilename(), uri.get()));
            } else {
                menu.findItem(android.R.id.copyUrl).setVisible(false);
            }

            menu.findItem(R.id.append_to_description).setOnMenuItemClickListener(item -> {
                getAppendToDescriptionContent(account, cardRemoteId, attachment)
                        .ifPresentOrElse(onAppendToDescription, () -> {
                            // TODO Toast
                        });
                return true;
            });

            menu.findItem(R.id.delete).setOnMenuItemClickListener(item -> {
                DeleteAttachmentDialogFragment.newInstance(attachment).show(fragmentManager, DeleteAttachmentDialogFragment.class.getCanonicalName());
                return true;
            });
        });

        applyTheme(color);
    }

    @CallSuper
    protected void applyTheme(@ColorInt int color) {
        final var utils = ThemeUtils.of(color, getPreview().getContext());

        utils.platform.colorImageView(getNotSyncedYetStatusIcon(), ColorRole.PRIMARY);
    }

    protected Optional<String> getAppendToDescriptionContent(@NonNull Account account,
                                                             @Nullable Long cardRemoteId,
                                                             @NonNull Attachment attachment) {
        return AttachmentUtil.getRemoteUrl(account, cardRemoteId, attachment)
                .map(this::splicePathWithQuery)
                .map(url -> MarkdownUtil.getMarkdownLink(attachment.getBasename(), url));
    }

    protected String splicePathWithQuery(@NonNull Uri uri) {
        final var path = uri.getPath();
        final var query = uri.getQuery();
        return query == null
                ? path
                : path + "/" + query;
    }

    abstract protected ImageView getPreview();

    abstract protected ImageView getNotSyncedYetStatusIcon();
}