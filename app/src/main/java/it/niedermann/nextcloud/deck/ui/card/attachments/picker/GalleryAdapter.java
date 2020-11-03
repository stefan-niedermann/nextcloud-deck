package it.niedermann.nextcloud.deck.ui.card.attachments.picker;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import it.niedermann.nextcloud.deck.databinding.ItemAttachmentImageBinding;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.Q;
import static androidx.recyclerview.widget.RecyclerView.NO_ID;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryItemViewHolder> {

    private final int columnIndex;
    private final int count;
    @NonNull
    private final Consumer<Uri> onSelect;
    @NonNull
    private final Cursor cursor;
    @NonNull
    private final ContentResolver contentResolver;
    @NonNull
    private final ExecutorService bitmapFetcherExecutor = Executors.newCachedThreadPool();
    @NonNull
    private final ExecutorService bitmapWaiterExecutor = Executors.newCachedThreadPool();

    public GalleryAdapter(@NonNull Context context, @NonNull Consumer<Uri> onSelect) {
        this.onSelect = onSelect;
        this.contentResolver = context.getContentResolver();
        @SuppressLint("InlinedApi") final String sortOrder = (SDK_INT >= Q)
                ? MediaStore.Images.Media.DATE_TAKEN
                : MediaStore.Images.Media.DATE_ADDED;
        cursor = Objects.requireNonNull(contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.Media._ID}, null, null, sortOrder + " DESC"));
        this.columnIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
        this.count = cursor.getCount();
        notifyItemRangeInserted(0, this.count);
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        if (cursor.moveToPosition(position)) {
            return cursor.getLong(columnIndex);
        } else {
            return NO_ID;
        }
    }

    @NonNull
    @Override
    public GalleryItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GalleryItemViewHolder(ItemAttachmentImageBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryItemViewHolder holder, int position) {
        long id = getItemId(position);
        try {
            Bitmap thumbnail;
            if (SDK_INT >= Q) {
                thumbnail = contentResolver.loadThumbnail(ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id), new Size(512, 384), null);
            } else {
                thumbnail = MediaStore.Images.Thumbnails.getThumbnail(
                        contentResolver, id,
                        MediaStore.Images.Thumbnails.MINI_KIND, null);
            }
            new Handler(Looper.getMainLooper()).post(() -> holder.bind(ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id), thumbnail, onSelect));
        } catch (IOException ignored) {
            new Handler(Looper.getMainLooper()).post(holder::bindError);
        }
    }

    @Override
    public int getItemCount() {
        return count;
    }

    /**
     * Call this method when the {@link GalleryAdapter} is no longe need to free resources.
     */
    public void onDestroy() {
        cursor.close();
    }
}
