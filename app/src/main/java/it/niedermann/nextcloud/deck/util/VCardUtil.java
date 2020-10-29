package it.niedermann.nextcloud.deck.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import androidx.annotation.NonNull;

import java.util.NoSuchElementException;

public class VCardUtil {

    private VCardUtil() {
        // You shall not pass
    }

    public static Uri getVCardContentUri(@NonNull Context context, @NonNull Uri contactUri) throws NoSuchElementException {
        final ContentResolver cr = context.getContentResolver();
        try (final Cursor cursor = cr.query(contactUri, null, null, null, null)) {
            if (cursor.moveToFirst()) {
                final String lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                return Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_VCARD_URI, lookupKey);
            } else {
                throw new NoSuchElementException("Cursor has zero entries");
            }
        }
    }
}
