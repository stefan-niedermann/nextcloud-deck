package it.niedermann.nextcloud.deck.ui.card.attachments.picker;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Pair;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.databinding.ItemAttachmentImageBinding;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.Q;
import static androidx.recyclerview.widget.RecyclerView.NO_ID;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryItemViewHolder> {

    @NonNull
    private final Context context;
    private final int columnIndex;
    private final int count;
    @Nullable
    private final Cursor cursor;
    @NonNull
    private final ContentResolver contentResolver;
    @NonNull
    private final Map<Integer, Pair<Long, FutureTask<Bitmap>>> itemCache = new HashMap<>();
    private final ExecutorService bitmapFetcherExecutor = Executors.newCachedThreadPool();
    private final ExecutorService bitmapWaiterExecutor = Executors.newCachedThreadPool();

    public GalleryAdapter(@NonNull Context context) {
        this.context = context;
        this.contentResolver = context.getContentResolver();
        log("Start Query");
        @SuppressLint("InlinedApi") final String sortOrder = (SDK_INT >= Q)
                ? MediaStore.Images.Media.DATE_TAKEN
                : MediaStore.Images.Media.DATE_ADDED;
        cursor = Objects.requireNonNull(contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.Media._ID}, null, null, sortOrder + " DESC"));
        this.columnIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
        this.count = cursor.getCount();

        log("Cursor count = " + this.count);
        notifyItemRangeInserted(0, this.count);
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        Pair<Long, ?> itemAtPosition = getImageId(position);
        return itemAtPosition == null ? NO_ID : itemAtPosition.first;
    }

    @NonNull
    @Override
    public GalleryItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GalleryItemViewHolder(ItemAttachmentImageBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryItemViewHolder holder, int position) {
        FutureTask<Bitmap> imageFuture = getImageId(position).second;
        bitmapFetcherExecutor.execute(imageFuture);
        bitmapWaiterExecutor.execute(() -> {
            try {
                final Bitmap image = imageFuture.get();
                new Handler(Looper.getMainLooper()).post(() -> holder.bind(image));
            } catch (ExecutionException | InterruptedException ignored) {
                new Handler(Looper.getMainLooper()).post(() -> holder.bind(null));
            }
        });
    }

    @Override
    public int getItemCount() {
        return count;
    }

    private Pair<Long, FutureTask<Bitmap>> getImageId(int position) {
        if (itemCache.containsKey(position)) {
            return itemCache.get(position);
        } else {
            if (cursor == null) {
                return new Pair<>(NO_ID, null);
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

    private void log(String msg) {
        DeckLog.log(msg);
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
