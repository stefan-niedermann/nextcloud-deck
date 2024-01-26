package it.niedermann.nextcloud.deck.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;

import com.nextcloud.android.sso.helper.VersionCheckHelper;
import com.nextcloud.android.sso.model.FilesAppType;

import java.util.Optional;

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
     * @see #getThumbnailUrl(Account, Long, Attachment, int, int)
     */
    public static Optional<Uri> getThumbnailUrl(@NonNull Account account,
                                                @NonNull Long cardRemoteId,
                                                @NonNull Attachment attachment,
                                                @Px int previewSize) {
        return getThumbnailUrl(account, cardRemoteId, attachment, previewSize, previewSize);
    }

    /**
     * @return an {@link Uri} to the thumbnail of the given {@link Attachment}.
     * If a thumbnail is not available (see {@link Version#supportsFileAttachments()}), an {@link Uri} to
     * the {@link Attachment} itself will be returned instead. Beware that this might be some huge data to load and process.
     */
    public static Optional<Uri> getThumbnailUrl(@NonNull Account account,
                                                @NonNull Long cardRemoteId,
                                                @NonNull Attachment attachment,
                                                @Px int previewWidth,
                                                @Px int previewHeight) {
        return getRemoteUrl(account, attachment)
                .map(uri -> uri + "?x=" + previewWidth + "&y=" + previewHeight + "&a=true")
                .map(Uri::parse)
                .or(() -> getRemoteUrl_1_0(account, cardRemoteId, attachment))
                .or(() -> Optional.ofNullable(attachment.getLocalPath()).map(Uri::parse));
    }

    /**
     * @return an optional {@link Intent} to open the {@param attachment} in the Nextcloud Files app with a fallback to the web browser.
     */
    @NonNull
    public static Optional<Intent> generateOpenAttachmentIntent(@NonNull Account account,
                                                                @NonNull Context context,
                                                                @Nullable Long cardRemoteId,
                                                                @NonNull Attachment attachment) {
        return generateOpenAttachmentInNextcloudFilesIntent(context, account, attachment)
                .or(() -> generateOpenAttachmentInBrowserIntent(context.getPackageManager(), account, cardRemoteId, attachment));
    }

    @NonNull
    private static Optional<Intent> generateOpenAttachmentInNextcloudFilesIntent(@NonNull Context context,
                                                                                 @NonNull Account account,
                                                                                 @NonNull Attachment attachment) {
        final Long fileId = attachment.getFileId();

        if (fileId == null) {
            return Optional.empty();
        }

        final var packageManager = context.getPackageManager();

        for (final var type : FilesAppType.values()) {
            try {
                if (VersionCheckHelper.getNextcloudFilesVersionCode(context, type) > 30110000) {
                    final var intent = new Intent(Intent.ACTION_VIEW)
                            .setClassName(type.packageId, "com.owncloud.android.ui.activity.FileDisplayActivity")
                            .putExtra("KEY_FILE_ID", String.valueOf(fileId))
                            .putExtra("KEY_ACCOUNT", account.getName());

                    if (packageManager.resolveActivity(intent, 0) != null) {
                        return Optional.of(intent);
                    }
                }
            } catch (PackageManager.NameNotFoundException ignored) {
            }
        }

        return Optional.empty();
    }

    @NonNull
    private static Optional<Intent> generateOpenAttachmentInBrowserIntent(@NonNull PackageManager packageManager,
                                                                          @NonNull Account account,
                                                                          @Nullable Long cardRemoteId,
                                                                          @NonNull Attachment attachment) {
        return getRemoteUrl(account, cardRemoteId, attachment)
                .map(uri -> new Intent(Intent.ACTION_VIEW).setData(uri))
                .filter(intent -> packageManager.resolveActivity(intent, 0) != null);
    }

    public static Optional<Uri> getRemoteUrl(@NonNull Account account,
                                             @Nullable Long cardRemoteId,
                                             @NonNull Attachment attachment) {
        return getRemoteUrl(account, attachment)
                .or(() -> getRemoteUrl_1_0(account, cardRemoteId, attachment));
    }

    private static Optional<Uri> getRemoteUrl(@NonNull Account account,
                                              @NonNull Attachment attachment) {
        if (!account.getServerDeckVersionAsObject().supportsFileAttachments()) {
            return Optional.empty();
        }

        if (attachment.getType() != EAttachmentType.FILE) {
            return Optional.empty();
        }

        if (attachment.getFileId() == null) {
            return Optional.empty();
        }

        return Optional.of(account.getUrl() + "/f/" + attachment.getFileId())
                .map(Uri::parse);
    }

    /**
     * Attention! This does only work for attachments of type {@link EAttachmentType#DECK_FILE} which are a legacy of Deck <code>API 1.0</code>
     */
    @Deprecated
    private static Optional<Uri> getRemoteUrl_1_0(@NonNull Account account,
                                                  @Nullable Long cardRemoteId,
                                                  @NonNull Attachment attachment) {
        if (cardRemoteId == null) {
            return Optional.empty();
        }

        final Long attachmentRemoteId = attachment.getId();
        if (attachmentRemoteId == null) {
            return Optional.empty();
        }

        return Optional.of(account.getUrl() + "/index.php/apps/deck/cards/" + cardRemoteId + "/attachment/" + attachmentRemoteId)
                .map(Uri::parse);
    }

    @DrawableRes
    public static int getIconForMimeType(@NonNull String mimeType) {
        if (TextUtils.isEmpty(mimeType)) {
            return R.drawable.ic_attach_file_24dp;
        } else if (MimeTypeUtil.isAudio(mimeType)) {
            return R.drawable.ic_music_note_24dp;
        } else if (MimeTypeUtil.isVideo(mimeType)) {
            return R.drawable.ic_local_movies_24dp;
        } else if (MimeTypeUtil.isPdf(mimeType)) {
            return R.drawable.ic_picture_as_pdf_24;
        } else if (MimeTypeUtil.isContact(mimeType)) {
            return R.drawable.ic_contact_mail_24;
        } else {
            return R.drawable.ic_attach_file_24dp;
        }
    }

}
