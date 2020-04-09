package it.niedermann.nextcloud.deck.ui.preparecreate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemPickStackStackBinding;
import it.niedermann.nextcloud.deck.model.full.FullStack;

public class StackAdapter extends ArrayAdapter<FullStack> {

    @NonNull
    private final LayoutInflater inflater;

    @SuppressWarnings("WeakerAccess")
    public StackAdapter(@NonNull Context context) {
        super(context, R.layout.item_pick_stack_account);
        setDropDownViewResource(R.layout.item_pick_stack_account);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public long getItemId(int position) {
        return Objects.requireNonNull(getItem(position)).getLocalId();
    }

    @NotNull
    @Override
    public View getView(int position, View convertView, @NotNull ViewGroup parent) {
        final ItemPickStackStackBinding binding;
        if (convertView == null) {
            binding = ItemPickStackStackBinding.inflate(inflater, parent, false);
        } else {
            binding = ItemPickStackStackBinding.bind(convertView);
        }

        final FullStack item = getItem(position);
        if (item != null) {
            binding.stackTitle.setText(item.getStack().getTitle());
        } else {
            DeckLog.logError(new IllegalArgumentException("No item for position " + position));
        }
        return binding.getRoot();
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }
}
