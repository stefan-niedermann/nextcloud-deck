package it.niedermann.nextcloud.deck.ui.filter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.util.NoSuchElementException;

import it.niedermann.nextcloud.deck.model.Label;

public class LabelFilterAdapter extends ArrayAdapter<Label> {

    @NonNull
    private final LayoutInflater inflater;

    @SuppressWarnings("WeakerAccess")
    public LabelFilterAdapter(@NonNull Context context) {
        super(context, android.R.layout.simple_list_item_multiple_choice, android.R.id.text1);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public long getItemId(int position) {
        final Label label = getItem(position);
        if (label == null) {
            throw new NoSuchElementException();
        }
        return label.getLocalId();
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    @SuppressWarnings("WeakerAccess")
    public int getPosition(long labelId) {
        for (int i = 0; i < getCount(); i++) {
            if (getItemId(i) == labelId) {
                return i;
            }
        }
        throw new NoSuchElementException();
    }

    @NotNull
    @Override
    public View getView(int position, View convertView, @NotNull ViewGroup parent) {
        final View view;
        if (convertView == null) {
            view = inflater.inflate(android.R.layout.simple_list_item_multiple_choice, parent, false);
        } else {
            view = convertView;
        }
        ((TextView) view.findViewById(android.R.id.text1)).setText(getItem(position).getTitle());
        return view;
    }
}
