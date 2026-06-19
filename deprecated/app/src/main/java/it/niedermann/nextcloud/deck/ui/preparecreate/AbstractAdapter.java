package it.niedermann.nextcloud.deck.ui.preparecreate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

public abstract class AbstractAdapter<T> extends ArrayAdapter<T> {

    @NonNull
    protected final LayoutInflater inflater;

    @SuppressWarnings("WeakerAccess")
    public AbstractAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        inflater = LayoutInflater.from(context);
    }

    protected abstract long getItemId(@NonNull T item);

    @Override
    public final long getItemId(int position) {
        return getItemId(Objects.requireNonNull(getItem(position)));
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }
}
