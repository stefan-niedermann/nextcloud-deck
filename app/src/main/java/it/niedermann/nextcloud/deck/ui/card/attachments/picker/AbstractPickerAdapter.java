package it.niedermann.nextcloud.deck.ui.card.attachments.picker;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import static android.database.Cursor.FIELD_TYPE_BLOB;
import static android.database.Cursor.FIELD_TYPE_FLOAT;
import static android.database.Cursor.FIELD_TYPE_INTEGER;
import static android.database.Cursor.FIELD_TYPE_NULL;
import static android.database.Cursor.FIELD_TYPE_STRING;
import static androidx.recyclerview.widget.RecyclerView.NO_ID;

/**
 * An {@link RecyclerView.Adapter} which provides previews of one type of files and also an option to open a native dialog.
 * <p>
 * Example: Previews for images of the gallery as well a one option to take a photo
 */
public abstract class AbstractPickerAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {

    protected static final int VIEW_TYPE_ITEM = 0;
    protected static final int VIEW_TYPE_ITEM_NATIVE = 1;

    private final int count;
    protected final int columnIndex;
    private final int columnIndexType;
    @NonNull
    protected final Consumer<Uri> onSelect;
    @NonNull
    protected final Runnable openNativePicker;
    @NonNull
    protected final Cursor cursor;
    @NonNull
    protected final ContentResolver contentResolver;

    /**
     * Should be used to bind heavy operations like when dealing with {@link Bitmap}
     */
    @NonNull
    protected final ExecutorService bindExecutor = Executors.newFixedThreadPool(1);

    public AbstractPickerAdapter(@NonNull Context context, @NonNull Consumer<Uri> onSelect, @NonNull Runnable openNativePicker, Uri subject, String idColumn, String sortOrder) {
        this(context, onSelect, openNativePicker, subject, idColumn, new String[]{idColumn}, sortOrder);
    }

    public AbstractPickerAdapter(@NonNull Context context, @NonNull Consumer<Uri> onSelect, @NonNull Runnable openNativePicker, Uri subject, String idColumn, String[] requestedColumns, String sortOrder) {
        this.onSelect = onSelect;
        this.openNativePicker = openNativePicker;
        this.contentResolver = context.getContentResolver();
        this.cursor = Objects.requireNonNull(contentResolver.query(subject, requestedColumns, null, null, sortOrder));
        this.cursor.moveToFirst();
        this.columnIndex = this.cursor.getColumnIndex(idColumn);
        this.columnIndexType = this.cursor.getType(columnIndex);
        this.count = cursor.getCount();
        setHasStableIds(true);
    }

    /**
     * Moves the {@link #cursor} to the given position
     */
    @Override
    public long getItemId(int position) {
        if (cursor.moveToPosition(position - 1)) {
            switch (columnIndexType) {
                case FIELD_TYPE_INTEGER:
                    return cursor.getLong(columnIndex);
                case FIELD_TYPE_NULL:
                    return NO_ID;
                case FIELD_TYPE_FLOAT:
                    return String.valueOf(cursor.getFloat(columnIndex)).hashCode();
                case FIELD_TYPE_STRING:
                    return cursor.getString(columnIndex).hashCode();
                case FIELD_TYPE_BLOB:
                    return Arrays.hashCode(cursor.getBlob(columnIndex));
                default:
                    throw new IllegalStateException("Unknown type for columnIndex \"" + columnIndex + "\": " + columnIndexType);
            }
        } else {
            return NO_ID;
        }
    }

    @Override
    public int getItemCount() {
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0
                ? VIEW_TYPE_ITEM_NATIVE
                : VIEW_TYPE_ITEM;
    }

    /**
     * Call this method when the {@link AbstractPickerAdapter} is no longer need to free resources.
     */
    public void onDestroy() {
        cursor.close();
    }
}
