package it.niedermann.nextcloud.deck.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.model.ocs.Version;

/**
 * Created by stefan on 07.03.20.
 */

public class AttachmentUtil {

    private AttachmentUtil() {
    }

    /**
     * @return a link to the thumbnail of the given {@link Attachment}.
     * If a thumbnail is not available (see {@link Version#supportsFileAttachments()}), a link to
     * the {@link Attachment} itself will be returned instead.
     */
    public static String getThumbnailUrl(@NonNull Version version, @NonNull String accountUrl, @NonNull Long cardRemoteId, @NonNull Attachment attachment, @Px int previewSize) {
        return version.supportsFileAttachments() && !TextUtils.isEmpty(String.valueOf(attachment.getFileId()))
                ? accountUrl + "/index.php/core/preview?fileId=" + attachment.getFileId() + "&x=" + previewSize + "&y=" + previewSize
                : getRemoteOrLocalUrl(accountUrl, cardRemoteId, attachment);
    }

    /**
     * @return {@link AttachmentUtil#getRemoteUrl} or {@link Attachment#getLocalPath()} as fallback
     * in case this {@param attachment} has not yet been synced.
     */
    @Nullable
    public static String getRemoteOrLocalUrl(@NonNull String accountUrl, @Nullable Long cardRemoteId, @NonNull Attachment attachment) {
        return (attachment.getId() == null || cardRemoteId == null)
                ? attachment.getLocalPath()
                : getRemoteUrl(accountUrl, cardRemoteId, attachment.getId());
    }

    /**
     * Tries to open the given {@link Attachment} in web browser. Displays a toast on failure.
     */
    public static void openAttachmentInBrowser(@NonNull Context context, @NonNull String accountUrl, Long cardRemoteId, Long attachmentRemoteId) {
        if (cardRemoteId == null) {
            Toast.makeText(context, R.string.card_does_not_yet_exist, Toast.LENGTH_LONG).show();
            DeckLog.logError(new IllegalArgumentException("cardRemoteId must not be null."));
            return;
        }
        if (attachmentRemoteId == null) {
            Toast.makeText(context, R.string.attachment_does_not_yet_exist, Toast.LENGTH_LONG).show();
            DeckLog.logError(new IllegalArgumentException("attachmentRemoteId must not be null."));
            return;
        }
        context.startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(AttachmentUtil.getRemoteUrl(accountUrl, cardRemoteId, attachmentRemoteId))));
    }

    private static String getRemoteUrl(@NonNull String accountUrl, @NonNull Long cardRemoteId, @NonNull Long attachmentRemoteId) {
        return accountUrl + "/index.php/apps/deck/cards/" + cardRemoteId + "/attachment/" + attachmentRemoteId;
    }

    public static File copyContentUriToTempFile(@NonNull Context context, @NonNull Uri currentUri, long accountId, Long localCardId) throws IOException, IllegalArgumentException {
        final InputStream inputStream = context.getContentResolver().openInputStream(currentUri);
        if (inputStream == null) {
            throw new IOException("Could not open input stream for " + currentUri.getPath());
        }
        final File cacheFile = getTempCacheFile(context, "attachments/account-" + accountId + "/card-" + (localCardId == null ? "pending-creation" : localCardId) + '/' + UriUtils.getDisplayNameForUri(currentUri, context));
        final FileOutputStream outputStream = new FileOutputStream(cacheFile);
        byte[] buffer = new byte[4096];

        int count;
        while ((count = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, count);
        }
        DeckLog.verbose("----- wrote");
        return cacheFile;
    }

    /**
     * Creates a new {@link File}
     */
    public static File getTempCacheFile(@NonNull Context context, String fileName) throws IOException {
        File cacheFile = new File(context.getApplicationContext().getFilesDir().getAbsolutePath() + "/" + fileName);

        DeckLog.verbose("- Full path for new cache file: " + cacheFile.getAbsolutePath());

        final File tempDir = cacheFile.getParentFile();
        if (tempDir == null) {
            throw new FileNotFoundException("could not cacheFile.getParentFile()");
        }
        if (!tempDir.exists()) {
            DeckLog.verbose("-- The folder in which the new file should be created does not exist yet. Trying to create it...");
            if (tempDir.mkdirs()) {
                DeckLog.verbose("--- Creation successful");
            } else {
                throw new IOException("Directory for temporary file does not exist and could not be created.");
            }
        }

        DeckLog.verbose("- Try to create actual cache file");
        if (cacheFile.createNewFile()) {
            DeckLog.verbose("-- Successfully created cache file");
        } else {
            throw new IOException("Failed to create cacheFile");
        }

        return cacheFile;
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

    public static String getMimeType(@Nullable String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

}
