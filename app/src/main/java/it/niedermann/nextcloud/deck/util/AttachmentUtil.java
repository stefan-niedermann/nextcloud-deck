package it.niedermann.nextcloud.deck.util;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import it.niedermann.nextcloud.deck.DeckLog;

/**
 * Created by stefan on 07.03.20.
 */

public class AttachmentUtil {

    private AttachmentUtil() {
    }

    public static String getRemoteUrl(String accountUrl, long cardRemoteId, long attachmentRemoteId) {
        return accountUrl + "/index.php/apps/deck/cards/" + cardRemoteId + "/attachment/" + attachmentRemoteId;
    }

    public static File copyContentUriToTempFile(@NonNull Context context, @NonNull Uri currentUri, long accountId, Long localId) throws IOException, IllegalArgumentException {
        String fullTempPath = context.getApplicationContext().getFilesDir().getAbsolutePath() + "/attachments/account-" + accountId + "/card-" + (localId == null ? "pending-creation" : localId) + '/' + UriUtils.getDisplayNameForUri(currentUri, context);
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
