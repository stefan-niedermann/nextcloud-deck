package it.niedermann.nextcloud.deck.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static java.lang.Long.parseLong;

/**
 * A helper class for some {@link Uri} operations.
 */
public final class UriUtils {

    private static final String boardsSegment = "board";

    private UriUtils() {
        // utility class -> private constructor
    }

    @NonNull
    public static String getDisplayNameForUri(@NonNull Uri uri, @NonNull Context context) throws IllegalArgumentException {
        String displayName;

        if (!ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            displayName = uri.getLastPathSegment(); // ready to return
            if (displayName == null) {
                throw new IllegalArgumentException("Given uri is no content uri, but path is null. [" + uri + "]");
            }
        } else { // content:// URI
            try {
                displayName = getDisplayNameFromContentResolver(uri, context);
            } catch (IllegalArgumentException e) {
                // last chance to have a name
                String lastPathSegment = uri.getLastPathSegment();
                if (lastPathSegment == null) {
                    throw new IllegalArgumentException("Given uri is content uri, but path is null. [" + uri + "]", e);
                }
                displayName = uri.getLastPathSegment().replaceAll("\\s", "");
            }
        }

        // Add best possible extension
        int index = displayName.lastIndexOf('.');
        if (index == -1 || MimeTypeMap.getSingleton().getMimeTypeFromExtension(displayName.substring(index + 1).toLowerCase(Locale.ROOT)) == null) {
            String mimeType = context.getContentResolver().getType(uri);
            String extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
            if (extension != null) {
                displayName += "." + extension;
            }
        }

        // Replace path separator characters to avoid inconsistent paths
        return displayName.replaceAll("/", "-");
    }

    @NonNull
    private static String getDisplayNameFromContentResolver(Uri uri, Context context) throws IllegalArgumentException {
        final String displayName;

        final String mimeType = context.getContentResolver().getType(uri);
        if (mimeType == null) {
            throw new IllegalArgumentException("mimetype of given uri is null. [" + uri + "]");
        }

        final String displayNameColumn;

        if (MimeTypeUtil.isImage(mimeType)) {
            displayNameColumn = MediaStore.Images.ImageColumns.DISPLAY_NAME;
        } else if (MimeTypeUtil.isVideo(mimeType)) {
            displayNameColumn = MediaStore.Video.VideoColumns.DISPLAY_NAME;
        } else if (MimeTypeUtil.isAudio(mimeType)) {
            displayNameColumn = MediaStore.Audio.AudioColumns.DISPLAY_NAME;
        } else {
            displayNameColumn = MediaStore.Files.FileColumns.DISPLAY_NAME;
        }

        try (Cursor cursor = context.getContentResolver().query(
                uri, new String[]{displayNameColumn},
                null, null, null
        )) {
            if (cursor == null) {
                throw new IllegalArgumentException("Cursor for " + ContentResolver.class.getSimpleName() + " query is null. [" + uri + "]");
            }
            if (!cursor.moveToFirst()) {
                throw new IllegalArgumentException("Cursor for " + ContentResolver.class.getSimpleName() + " query is empty. [" + uri + "]");
            }
            displayName = cursor.getString(cursor.getColumnIndex(displayNameColumn));
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not retrieve display name for " + uri.toString(), e);
        }
        return displayName;
    }

    public static long parseBoardRemoteId(@NonNull String uri) throws IllegalArgumentException {
        List<String> segments = Arrays.asList(uri.split("/"));
        int boardsSegmentIndex = segments.lastIndexOf(boardsSegment);
        if (segments.size() <= boardsSegmentIndex) {
            throw new IllegalArgumentException("There was no ID following the last " + boardsSegment + " segment.");
        }
        final long parsedLong;
        try {
            parsedLong = parseLong(segments.get(boardsSegmentIndex + 1));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("The ID (\"" + segments.get(boardsSegmentIndex + 1) + "\") the last " + boardsSegment + " is not of type " + Long.class.getSimpleName(), e);
        }
        if (parsedLong < 1) {
            throw new IllegalArgumentException("Parsed board remote id must not be less then 1 but was " + parsedLong);
        }
        return parsedLong;
    }
}
