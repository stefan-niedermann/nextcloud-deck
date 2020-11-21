package it.niedermann.nextcloud.deck.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.ContactsContract;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import java.util.NoSuchElementException;
import java.util.Objects;

import it.niedermann.nextcloud.deck.R;

public class VCardUtil {

    private VCardUtil() {
        // You shall not pass
    }

    public static Uri getVCardContentUri(@NonNull Context context, @NonNull Uri contactUri) throws NoSuchElementException {
        final ContentResolver cr = context.getContentResolver();
        try (final Cursor cursor = cr.query(contactUri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                final String lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                return Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_VCARD_URI, lookupKey);
            } else {
                throw new NoSuchElementException("Cursor has zero entries");
            }
        }
    }

    @ColorInt
    public static int getColorBasedOnDisplayName(@NonNull Context context, @NonNull String displayName) {
        final String[] colors = context.getResources().getStringArray(R.array.board_default_colors);
        final int hashCode = Objects.hashCode(displayName);
        return Color.parseColor(colors[(hashCode < 0 ? hashCode * -1 : hashCode) % colors.length]);
    }
}
