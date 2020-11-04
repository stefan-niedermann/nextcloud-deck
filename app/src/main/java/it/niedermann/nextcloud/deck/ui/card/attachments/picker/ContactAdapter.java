package it.niedermann.nextcloud.deck.ui.card.attachments.picker;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

import it.niedermann.nextcloud.deck.databinding.ItemPickerNativeBinding;
import it.niedermann.nextcloud.deck.databinding.ItemPickerUserBinding;

import static android.provider.ContactsContract.Contacts.CONTENT_LOOKUP_URI;
import static android.provider.ContactsContract.Contacts.CONTENT_URI;
import static android.provider.ContactsContract.Contacts.DISPLAY_NAME;
import static android.provider.ContactsContract.Contacts.LOOKUP_KEY;
import static android.provider.ContactsContract.Contacts.SORT_KEY_PRIMARY;

public class ContactAdapter extends AbstractPickerAdapter<RecyclerView.ViewHolder> {


    private final int displayNameColumnIndex;

    public ContactAdapter(@NonNull Context context, @NonNull Consumer<Uri> onSelect, @NonNull Runnable onSelectPicker) {
        super(context, onSelect, onSelectPicker, CONTENT_URI, LOOKUP_KEY, new String[]{LOOKUP_KEY, DISPLAY_NAME}, SORT_KEY_PRIMARY);
        displayNameColumnIndex = cursor.getColumnIndex(DISPLAY_NAME);
        notifyItemRangeInserted(0, getItemCount());
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
                bindExecutor.execute(() -> {
                    final ContactItemViewHolder viewHolder = (ContactItemViewHolder) holder;
                    final long id = getItemId(position);
                    final String name = cursor.getString(displayNameColumnIndex);
                    try (InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(contentResolver, Uri.withAppendedPath(CONTENT_LOOKUP_URI, cursor.getString(columnIndex)))) {
                        final Bitmap thumbnail = BitmapFactory.decodeStream(inputStream);
                        new Handler(Looper.getMainLooper()).post(() -> viewHolder.bind(ContentUris.withAppendedId(CONTENT_LOOKUP_URI, id), thumbnail, name, onSelect));
                    } catch (IOException ignored) {
                        new Handler(Looper.getMainLooper()).post(viewHolder::bindError);
                    }
                });
                break;
            }
        }
    }
}
