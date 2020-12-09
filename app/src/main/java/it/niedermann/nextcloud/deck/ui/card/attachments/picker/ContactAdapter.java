package it.niedermann.nextcloud.deck.ui.card.attachments.picker;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.BiConsumer;

import it.niedermann.nextcloud.deck.databinding.ItemPickerNativeBinding;
import it.niedermann.nextcloud.deck.databinding.ItemPickerUserBinding;

import static android.provider.ContactsContract.CommonDataKinds.Email.DATA;
import static android.provider.ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY;
import static android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER;
import static android.provider.ContactsContract.Contacts.CONTENT_LOOKUP_URI;
import static android.provider.ContactsContract.Contacts.CONTENT_URI;
import static android.provider.ContactsContract.Contacts.DISPLAY_NAME;
import static android.provider.ContactsContract.Contacts.SORT_KEY_PRIMARY;
import static android.provider.ContactsContract.Contacts._ID;

public class ContactAdapter extends AbstractCursorPickerAdapter<RecyclerView.ViewHolder> {

    private final int lookupKeyColumnIndex;
    private final int displayNameColumnIndex;

    public ContactAdapter(@NonNull Context context, @NonNull BiConsumer<Uri, Pair<String, RequestBuilder<?>>> onSelect, @NonNull Runnable onSelectPicker) {
        super(context, onSelect, onSelectPicker, CONTENT_URI, _ID, new String[]{_ID, LOOKUP_KEY, DISPLAY_NAME}, SORT_KEY_PRIMARY);
        lookupKeyColumnIndex = cursor.getColumnIndex(LOOKUP_KEY);
        displayNameColumnIndex = cursor.getColumnIndex(DISPLAY_NAME);
        notifyItemRangeInserted(0, getItemCount() + 1);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_ITEM_NATIVE:
                return new ContactNativeItemViewHolder(ItemPickerNativeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
            case VIEW_TYPE_ITEM:
                return new ContactItemViewHolder(ItemPickerUserBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
            default:
                throw new IllegalStateException("Unknown viewType " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case VIEW_TYPE_ITEM_NATIVE: {
                ((ContactNativeItemViewHolder) holder).bind(openNativePicker);
                break;
            }
            case VIEW_TYPE_ITEM: {
                final ContactItemViewHolder viewHolder = (ContactItemViewHolder) holder;
                if (!cursor.isClosed()) {
                    cursor.moveToPosition(position - 1);
                    final String displayName = cursor.getString(displayNameColumnIndex);
                    final String lookupKey = cursor.getString(lookupKeyColumnIndex);
                    bindExecutor.execute(() -> {
                        try (InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(contentResolver, Uri.withAppendedPath(CONTENT_LOOKUP_URI, lookupKey))) {
                            final Bitmap thumbnail = BitmapFactory.decodeStream(inputStream);
                            String contactInformation = "";
                            try (final Cursor phoneCursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, new String[]{NUMBER}, LOOKUP_KEY + " = ?", new String[]{lookupKey}, null)) {
                                if (phoneCursor != null && phoneCursor.moveToFirst()) {
                                    contactInformation = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                                }
                            }
                            if (TextUtils.isEmpty(contactInformation)) {
                                try (final Cursor emailCursor = contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, new String[]{DATA}, LOOKUP_KEY + " = ?", new String[]{lookupKey}, null)) {
                                    if (emailCursor != null && emailCursor.moveToFirst()) {
                                        contactInformation = emailCursor.getString(emailCursor.getColumnIndex(DATA));
                                    }
                                }
                            }
                            final String finalContactInformation = contactInformation;
                            new Handler(Looper.getMainLooper()).post(() -> viewHolder.bind(Uri.withAppendedPath(CONTENT_LOOKUP_URI, lookupKey), thumbnail, displayName, finalContactInformation, onSelect));
                        } catch (IOException ignored) {
                            new Handler(Looper.getMainLooper()).post(viewHolder::bindError);
                        }
                    });
                } else {
                    new Handler(Looper.getMainLooper()).post(viewHolder::bindError);
                }
                break;
            }
        }
    }
}
