package it.niedermann.nextcloud.deck.util;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;

/**
 * Created by stefan on 07.03.20.
 */

public class AttachmentUtil {

    private AttachmentUtil() {
    }

    public static String getUrl(String accountUrl, long cardRemoteId, long attachmentRemoteId) {
        return accountUrl + "/index.php/apps/deck/cards/" + cardRemoteId + "/attachment/" + attachmentRemoteId;
    }

    public static void appendAttachment(@NonNull Context context, @NonNull SyncManager syncManager, @NonNull List<Parcelable> streamsToUpload, @NonNull FullCard fullCard) {

        List<Uri> contentUris = new ArrayList<>();

        for (Parcelable sourceStream : streamsToUpload) {
            Uri sourceUri = (Uri) sourceStream;
            if (sourceUri != null) {
                if (ContentResolver.SCHEME_CONTENT.equals(sourceUri.getScheme())) {
                    contentUris.add(sourceUri);
                    DeckLog.verbose("--- found content URL, remember for later: " + sourceUri.getPath());
                } else if (ContentResolver.SCHEME_FILE.equals(sourceUri.getScheme())) {
                    /// file: uris should point to a local file, should be safe let FileUploader handle them
                    DeckLog.verbose("--- found file URL, directly upload: " + sourceUri.getPath());
                    syncManager.addAttachmentToCard(fullCard.getAccountId(), fullCard.getCard().getLocalId(), Attachment.getMimetypeForUri(context, sourceUri), new File(sourceUri.getPath()));
                }
            }
        }

        if (!contentUris.isEmpty()) {
            /// content: uris will be copied to temporary files before calling {@link FileUploader}
            for (Uri contentUri : contentUris) {
                try {
                    DeckLog.verbose("---- so, now copy&upload: " + contentUri.getPath());
                    File copiedFile = copyContentUriToTempFile(context, contentUri, fullCard.getAccountId(), fullCard.getCard().getLocalId());
                    syncManager.addAttachmentToCard(fullCard.getAccountId(), fullCard.getCard().getLocalId(), context.getContentResolver().getType(contentUri), copiedFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static File copyContentUriToTempFile(@NonNull Context context, @NonNull Uri currentUri, long accountId, Long localId) throws IOException {
        String fullTempPath = context.getApplicationContext().getFilesDir().getAbsolutePath() + "/attachments/account-" + accountId + "/card-" + localId + '/' + UUID.randomUUID() + '/' + UriUtils.getDisplayNameForUri(currentUri, context);
        DeckLog.verbose("----- fullTempPath: " + fullTempPath);
        InputStream inputStream = context.getContentResolver().openInputStream(currentUri);
        if (inputStream == null) {
            throw new IOException("Could not open input stream for " + currentUri.getPath());
        }
        File cacheFile = new File(fullTempPath);
        File tempDir = cacheFile.getParentFile();
        if (tempDir == null) {
            throw new FileNotFoundException("could not cacheFile.getPranetFile()");
        }
        if (!tempDir.exists()) {
            if (!tempDir.mkdirs()) {
                throw new IOException("Directory for temporary file does not exist and could not be created.");
            }
        }
        if (!cacheFile.createNewFile()) {
            throw new IOException("Failed to create cacheFile");
        }
        FileOutputStream outputStream = new FileOutputStream(fullTempPath);
        byte[] buffer = new byte[4096];

        int count;
        while ((count = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, count);
        }
        DeckLog.verbose("----- wrote");
        return cacheFile;
    }
}
