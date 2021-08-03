package it.niedermann.nextcloud.deck.util;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import it.niedermann.nextcloud.deck.DeckLog;

/**
 * Created by stefan on 07.03.20.
 */

public class FilesUtil {

    private FilesUtil() {
        throw new UnsupportedOperationException("This class must not get instantiated");
    }

    /**
     * https://help.nextcloud.com/t/android-app-select-file-with-nextcloud-app-file-cant-be-read/103706
     * Must not be called from the UI thread because the {@param currentUri} might refer to a not yet locally available file.
     */
    @WorkerThread
    public static File copyContentUriToTempFile(@NonNull Context context, @NonNull Uri currentUri, long accountId, Long localCardId) throws IOException, IllegalArgumentException {
        final var inputStream = context.getContentResolver().openInputStream(currentUri);
        if (inputStream == null) {
            throw new IOException("Could not open input stream for " + currentUri.getPath());
        }
        final var cacheFile = getTempCacheFile(context, "attachments/account-" + accountId + "/card-" + (localCardId == null ? "pending-creation" : localCardId) + '/' + UriUtils.getDisplayNameForUri(currentUri, context));
        final var outputStream = new FileOutputStream(cacheFile);
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
        final var cacheFile = new File(context.getApplicationContext().getFilesDir().getAbsolutePath() + "/" + fileName);

        DeckLog.verbose("- Full path for new cache file:", cacheFile.getAbsolutePath());

        final var tempDir = cacheFile.getParentFile();
        if (tempDir == null) {
            throw new FileNotFoundException("could not cacheFile.getParentFile()");
        }
        if (!tempDir.exists()) {
            DeckLog.verbose("-- The folder in which the new file should be created does not exist yet. Trying to create it…");
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
}
