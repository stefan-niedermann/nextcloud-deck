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
import android.util.Pair;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
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
    private final Map<Integer, Pair<Long, FutureTask<Bitmap>>> itemCache = new HashMap<>();
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
        final Pair<Long, ?> itemAtPosition = getImageInformation(position);
        return itemAtPosition == null ? NO_ID : itemAtPosition.first;
    }

    @NonNull
    @Override
    public GalleryItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GalleryItemViewHolder(ItemAttachmentImageBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryItemViewHolder holder, int position) {
        final Pair<Long, FutureTask<Bitmap>> imageInformation = getImageInformation(position);
        bitmapFetcherExecutor.execute(imageInformation.second);
        bitmapWaiterExecutor.execute(() -> {
            try {
                final Bitmap image = imageInformation.second.get();
                new Handler(Looper.getMainLooper()).post(() -> holder.bind(ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageInformation.first), image, onSelect));
            } catch (ExecutionException | InterruptedException ignored) {
                new Handler(Looper.getMainLooper()).post(holder::bindError);
            }
        });
    }

    @Override
    public int getItemCount() {
        return count;
    }

    private Pair<Long, FutureTask<Bitmap>> getImageInformation(int position) {
        if (itemCache.containsKey(position)) {
            return itemCache.get(position);
        } else {
            if (cursor.isClosed()) {
                throw new IllegalStateException("This adapter has already been destoryed and can no longer be used.");
            }
            if (cursor.moveToPosition(position)) {
                long id = cursor.getLong(columnIndex);
                return itemCache.put(position, new Pair<>(id, new FutureTask<>(() -> {
                    if (SDK_INT >= Q) {
                        return contentResolver.loadThumbnail(ContentUris.withAppendedId(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id), new Size(512, 384), null);
                    } else {
                        return MediaStore.Images.Thumbnails.getThumbnail(
                                contentResolver, id,
                                MediaStore.Images.Thumbnails.MINI_KIND, null);
                    }
                })));
            } else {
                throw new NoSuchElementException("Could not find ID for position " + position);
            }
        }
    }

    /**
     * Call this method in case of low memory. It will clear the internally cached {@link Bitmap}s to free memory.
     */
    public void onLowMemory() {
        itemCache.clear();
    }

    /**
     * Call this method when the {@link GalleryAdapter} is no longe need to free resources.
     */
    public void onDestroy() {
        cursor.close();
    }
}
