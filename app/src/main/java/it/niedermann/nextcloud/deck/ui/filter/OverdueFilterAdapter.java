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

import java.util.Objects;

import it.niedermann.nextcloud.deck.model.enums.EDueType;

public class OverdueFilterAdapter extends ArrayAdapter<EDueType> {

    @NonNull
    private final LayoutInflater inflater;

    @SuppressWarnings("WeakerAccess")
    public OverdueFilterAdapter(@NonNull Context context) {
        super(context, android.R.layout.simple_list_item_1, android.R.id.text1, EDueType.values());
        inflater = LayoutInflater.from(context);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    @NonNull
    @Override
    public EDueType getItem(int position) {
        EDueType item = super.getItem(position);
        if (item != null) {
            return item;
        } else {
            return EDueType.NO_FILTER;
        }
    }

    @NotNull
    @Override
    public View getView(int position, View convertView, @NotNull ViewGroup parent) {
        final View view;
        if (convertView == null) {
            view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        } else {
            view = convertView;
        }
        ((TextView) view.findViewById(android.R.id.text1)).setText(Objects.requireNonNull(getItem(position)).toString(view.getContext()));
        return view;
    }
}
