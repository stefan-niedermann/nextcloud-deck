package it.niedermann.nextcloud.deck.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.model.enums.EAttachmentType;
import it.niedermann.nextcloud.deck.model.ocs.Version;

/**
 * Created by stefan on 07.03.20.
 */

public class AttachmentUtil {

    private AttachmentUtil() {
        throw new UnsupportedOperationException("This class must not get instantiated");
    }

    /**
     * @return a link to the thumbnail of the given {@link Attachment}.
     * If a thumbnail is not available (see {@link Version#supportsFileAttachments()}), a link to
     * the {@link Attachment} itself will be returned instead.
     */
    public static String getThumbnailUrl(@NonNull Account account, @NonNull Long cardRemoteId, @NonNull Attachment attachment, @Px int previewSize) {
        return getThumbnailUrl(account, cardRemoteId, attachment, previewSize, previewSize);
    }

    public static String getThumbnailUrl(@NonNull Account account, @NonNull Long cardRemoteId, @NonNull Attachment attachment, @Px int previewWidth, @Px int previewHeight) {
        return account.getServerDeckVersionAsObject().supportsFileAttachments() &&
                EAttachmentType.FILE.equals(attachment.getType()) &&
                attachment.getFileId() != null
                ? account.getUrl() + "/index.php/core/preview?fileId=" + attachment.getFileId() + "&x=" + previewWidth + "&y=" + previewHeight + "&a=true"
                : getRemoteOrLocalUrl(account.getUrl(), cardRemoteId, attachment);
    }

    /**
     * @return {@link AttachmentUtil#getDeck_1_0_RemoteUrl} or {@link Attachment#getLocalPath()} as fallback
     * in case this {@param attachment} has not yet been synced.
     */
    @Nullable
    private static String getRemoteOrLocalUrl(@NonNull String accountUrl, @Nullable Long cardRemoteId, @NonNull Attachment attachment) {
        return (attachment.getId() == null || cardRemoteId == null)
                ? attachment.getLocalPath()
                : getDeck_1_0_RemoteUrl(accountUrl, cardRemoteId, attachment.getId());
    }

    /**
     * Tries to open the given {@link Attachment} in web browser. Displays a toast on failure.
     */
    public static void openAttachmentInBrowser(@NonNull Account account, @NonNull Context context, Long cardRemoteId, Attachment attachment) {
        if (cardRemoteId == null) {
            Toast.makeText(context, R.string.card_does_not_yet_exist, Toast.LENGTH_LONG).show();
            DeckLog.logError(new IllegalArgumentException("cardRemoteId must not be null."));
            return;
        }

        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(getCopyDownloadUrl(account, cardRemoteId, attachment))));
        } catch (IllegalArgumentException e) {
            Toast.makeText(context, R.string.attachment_does_not_yet_exist, Toast.LENGTH_LONG).show();
            DeckLog.logError(new IllegalArgumentException("attachmentRemoteId must not be null."));
        }
    }

    public static String getCopyDownloadUrl(@NonNull Account account, @NonNull Long cardRemoteId, @NonNull Attachment attachment) {
        if (attachment.getId() == null) {
            throw new IllegalArgumentException("attachment id must not be null");
        }

        return (attachment.getFileId() != null)
                ? account.getUrl() + "/f/" + attachment.getFileId()
                : getDeck_1_0_RemoteUrl(account.getUrl(), cardRemoteId, attachment.getId());
    }

    /**
     * Attention! This does only work for attachments of type {@link EAttachmentType#DECK_FILE} which are a legacy of Deck API 1.0
     */
    @Deprecated
    private static String getDeck_1_0_RemoteUrl(@NonNull String accountUrl, @NonNull Long cardRemoteId, @NonNull Long attachmentRemoteId) {
        return accountUrl + "/index.php/apps/deck/cards/" + cardRemoteId + "/attachment/" + attachmentRemoteId;
    }

    @DrawableRes
    public static int getIconForMimeType(@NonNull String mimeType) {
        if (TextUtils.isEmpty(mimeType)) {
            return R.drawable.ic_attach_file_grey600_24dp;
        } else if (MimeTypeUtil.isAudio(mimeType)) {
            return R.drawable.ic_music_note_grey600_24dp;
        } else if (MimeTypeUtil.isVideo(mimeType)) {
            return R.drawable.ic_local_movies_grey600_24dp;
        } else if (MimeTypeUtil.isPdf(mimeType)) {
            return R.drawable.ic_baseline_picture_as_pdf_24;
        } else if (MimeTypeUtil.isContact(mimeType)) {
            return R.drawable.ic_baseline_contact_mail_24;
        } else {
            return R.drawable.ic_attach_file_grey600_24dp;
        }
    }

}
