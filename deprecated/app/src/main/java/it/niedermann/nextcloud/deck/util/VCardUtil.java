package it.niedermann.nextcloud.deck.util;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import java.util.NoSuchElementException;
import java.util.Objects;

import it.niedermann.nextcloud.deck.R;

public class VCardUtil {

    private VCardUtil() {
        throw new UnsupportedOperationException("This class must not get instantiated");
    }

    public static Uri getVCardContentUri(@NonNull Context context, @NonNull Uri contactUri) throws NoSuchElementException {
        final var cr = context.getContentResolver();
        try (final var cursor = cr.query(contactUri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                final var columnIndex = cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY);
                if (columnIndex >= 0) {
                    final String lookupKey = cursor.getString(columnIndex);
                    return Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_VCARD_URI, lookupKey);
                } else {
                    throw new NoSuchElementException("Could not find column index for " + ContactsContract.Contacts.LOOKUP_KEY);
                }
            } else {
                throw new NoSuchElementException("Cursor has zero entries");
            }
        }
    }

    public static Uri getAudioContentUri(@NonNull Context context, @NonNull Uri uri) throws NoSuchElementException {
        try(final var cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Audio.AudioColumns.DISPLAY_NAME, MediaStore.Audio.AudioColumns.DATA}, null, null, null)) {
            cursor.moveToFirst();

            final var displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DISPLAY_NAME));
            final var data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA));



//        ArrayList audio = new ArrayList();
//        Cursor c = context.getContentResolver().query(uri, new String[]{MediaStore.Audio.Media.DISPLAY_NAME}, null, null, null);
//
//
//        while (c.moveToNext()) {
//            String name = c.getString(c.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
//            audio.add(name);
        }

        return uri;
    }

    @ColorInt
    public static int getColorBasedOnDisplayName(@NonNull Context context, @NonNull String displayName) {
        final String[] colors = context.getResources().getStringArray(R.array.board_default_colors);
        final int hashCode = Objects.hashCode(displayName);
        return Color.parseColor(colors[(hashCode < 0 ? hashCode * -1 : hashCode) % colors.length]);
    }
}
