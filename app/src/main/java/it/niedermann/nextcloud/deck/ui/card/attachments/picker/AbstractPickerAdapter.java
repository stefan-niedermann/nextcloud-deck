package it.niedermann.nextcloud.deck.ui.card.attachments.picker;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import static android.database.Cursor.FIELD_TYPE_INTEGER;
import static android.database.Cursor.FIELD_TYPE_NULL;
import static androidx.recyclerview.widget.RecyclerView.NO_ID;
import static java.util.Objects.requireNonNull;

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
     * Should be used to bind heavy operations like when dealing with {@link Bitmap}.
     * This must only be one {@link Thread} because otherwise the cursor might change while fetching data from it.
     */
    @NonNull
    protected final ExecutorService bindExecutor = Executors.newFixedThreadPool(1);

    public AbstractPickerAdapter(@NonNull Context context, @NonNull Consumer<Uri> onSelect, @NonNull Runnable openNativePicker, Uri subject, String idColumn, String sortOrder) {
        this(context, onSelect, openNativePicker, subject, idColumn, new String[]{idColumn}, sortOrder);
    }

    public AbstractPickerAdapter(@NonNull Context context, @NonNull Consumer<Uri> onSelect, @NonNull Runnable openNativePicker, Uri subject, String idColumn, String[] requestedColumns, String sortOrder) {
        this(context, onSelect, openNativePicker, idColumn, requireNonNull(context.getContentResolver().query(subject, requestedColumns, null, null, sortOrder)));
    }

    public AbstractPickerAdapter(@NonNull Context context, @NonNull Consumer<Uri> onSelect, @NonNull Runnable openNativePicker, String idColumn, @NonNull Cursor cursor) {
        this.contentResolver = context.getContentResolver();
        this.onSelect = onSelect;
        this.openNativePicker = openNativePicker;
        this.cursor = cursor;
        this.cursor.moveToFirst();
        this.columnIndex = this.cursor.getColumnIndex(idColumn);
        this.count = cursor.getCount();
        this.columnIndexType = (this.count > 0) ? this.cursor.getType(columnIndex) : FIELD_TYPE_NULL;
        setHasStableIds(true);
    }

    /**
     * Moves the {@link #cursor} to the given position
     */
    @Override
    public long getItemId(int position) {
        if (cursor.moveToPosition(position - 1)) {
            //noinspection SwitchStatementWithTooFewBranches
            switch (columnIndexType) {
                case FIELD_TYPE_INTEGER:
                    return cursor.getLong(columnIndex);
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
