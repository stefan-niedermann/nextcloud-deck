package it.niedermann.nextcloud.deck.ui.card.attachments.picker;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestBuilder;

import java.util.function.BiConsumer;

import it.niedermann.nextcloud.deck.databinding.ItemAttachmentDefaultBinding;
import it.niedermann.nextcloud.deck.databinding.ItemPickerNativeBinding;

import static android.provider.MediaStore.Downloads.DATE_ADDED;
import static android.provider.MediaStore.Downloads.DATE_MODIFIED;
import static android.provider.MediaStore.Downloads.EXTERNAL_CONTENT_URI;
import static android.provider.MediaStore.Downloads.MIME_TYPE;
import static android.provider.MediaStore.Downloads.SIZE;
import static android.provider.MediaStore.Downloads.TITLE;
import static android.provider.MediaStore.Downloads._ID;
import static java.util.Objects.requireNonNull;

@RequiresApi(api = 29)
public class FileAdapter extends AbstractCursorPickerAdapter<RecyclerView.ViewHolder> {

    private final int displayNameColumnIndex;
    private final int sizeColumnIndex;
    private final int modifiedColumnIndex;
    private final int mimeTypeColumnIndex;

    private FileAdapter(@NonNull Context context, @NonNull BiConsumer<Uri, Pair<String, RequestBuilder<?>>> onSelect, @NonNull Runnable onSelectPicker) {
        super(context, onSelect, onSelectPicker, _ID, requireNonNull(context.getContentResolver().query(EXTERNAL_CONTENT_URI, new String[]{_ID, TITLE, SIZE, DATE_MODIFIED, MIME_TYPE}, null, null, DATE_ADDED + " DESC")));
        displayNameColumnIndex = cursor.getColumnIndex(TITLE);
        sizeColumnIndex = cursor.getColumnIndex(SIZE);
        modifiedColumnIndex = cursor.getColumnIndex(DATE_MODIFIED);
        mimeTypeColumnIndex = cursor.getColumnIndex(MIME_TYPE);
        notifyItemRangeInserted(0, getItemCount() + 1);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_ITEM_NATIVE:
                return new FileNativeItemViewHolder(ItemPickerNativeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
            case VIEW_TYPE_ITEM:
                return new FileItemViewHolder(ItemAttachmentDefaultBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
            default:
                throw new IllegalStateException("Unknown viewType " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case VIEW_TYPE_ITEM_NATIVE: {
                ((FileNativeItemViewHolder) holder).bind(openNativePicker);
                break;
            }
            case VIEW_TYPE_ITEM: {
                if (!cursor.isClosed()) {
                    bindExecutor.execute(() -> {
                        final long id = getItemId(position);
                        final String name = cursor.getString(displayNameColumnIndex);
                        final String mimeType = cursor.getString(mimeTypeColumnIndex);
                        final long size = cursor.getLong(sizeColumnIndex);
                        final long modified = cursor.getLong(modifiedColumnIndex);
                        new Handler(Looper.getMainLooper()).post(() -> ((FileItemViewHolder) holder).bind(ContentUris.withAppendedId(MediaStore.Files.getContentUri("external"), id), name, mimeType, size, modified, onSelect));
                    });
                }
                break;
            }
        }
    }
}
