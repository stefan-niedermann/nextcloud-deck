package it.niedermann.nextcloud.deck.ui.preparecreate;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemPrepareCreateStackBinding;
import it.niedermann.nextcloud.deck.model.full.FullStack;

public class StackAdapter extends AbstractAdapter<FullStack> {

    @SuppressWarnings("WeakerAccess")
    public StackAdapter(@NonNull Context context) {
        super(context, R.layout.item_prepare_create_stack);
    }

    @Override
    protected long getItemId(@NonNull FullStack item) {
        return item.getLocalId();
    }

    @NotNull
    @Override
    public View getView(int position, View convertView, @NotNull ViewGroup parent) {
        final ItemPrepareCreateStackBinding binding;
        if (convertView == null) {
            binding = ItemPrepareCreateStackBinding.inflate(inflater, parent, false);
        } else {
            binding = ItemPrepareCreateStackBinding.bind(convertView);
        }

        final FullStack item = getItem(position);
        if (item != null) {
            binding.stackTitle.setText(item.getStack().getTitle());
        } else {
            DeckLog.logError(new IllegalArgumentException("No item for position " + position));
        }
        return binding.getRoot();
    }
}
