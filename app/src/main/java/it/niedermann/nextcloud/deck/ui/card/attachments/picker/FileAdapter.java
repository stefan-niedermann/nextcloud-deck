package it.niedermann.nextcloud.deck.ui.card.attachments.picker;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import it.niedermann.nextcloud.deck.databinding.ItemFilterUserBinding;
import it.niedermann.nextcloud.deck.databinding.ItemPickerUserBinding;

import static android.provider.BaseColumns._ID;
import static android.provider.MediaStore.Files.FileColumns.TITLE;
import static android.provider.MediaStore.MediaColumns.DATE_ADDED;
import static android.provider.MediaStore.MediaColumns.SIZE;

public class FileAdapter extends AbstractPickerAdapter<RecyclerView.ViewHolder> {

    private final int displayNameColumnIndex;
    private final int sizeColumnIndex;
    @NonNull
    private final ExecutorService bitmapExecutor = Executors.newCachedThreadPool();

    public FileAdapter(@NonNull Context context, @NonNull Consumer<Uri> onSelect, @NonNull Runnable onSelectPicker) {
        super(context, onSelect, onSelectPicker, MediaStore.Files.getContentUri("external"), _ID, new String[]{_ID, TITLE, SIZE}, DATE_ADDED + " DESC");
        displayNameColumnIndex = cursor.getColumnIndex(TITLE);
        sizeColumnIndex = cursor.getColumnIndex(SIZE);
        notifyItemRangeInserted(0, getItemCount());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_ITEM_PICKER:
                return new FilePickerItemViewHolder(ItemFilterUserBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
            case VIEW_TYPE_ITEM:
                return new FileItemViewHolder(ItemPickerUserBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
            default:
                throw new IllegalStateException("Unknown viewType " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case VIEW_TYPE_ITEM_PICKER: {
                ((FilePickerItemViewHolder) holder).bind(openNativePicker);
                break;
            }
            case VIEW_TYPE_ITEM: {
                bitmapExecutor.execute(() -> {
                    final long id = getItemId(position);
                    final String name = cursor.getString(displayNameColumnIndex);
                    final long size = cursor.getLong(sizeColumnIndex);
                    new Handler(Looper.getMainLooper()).post(() -> ((FileItemViewHolder) holder).bind(ContentUris.withAppendedId(MediaStore.Files.getContentUri("external"), id), name, size, onSelect));
                });
                break;
            }
        }
    }
}
