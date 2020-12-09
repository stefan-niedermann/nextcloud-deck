package it.niedermann.nextcloud.deck.ui.preparecreate;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemPrepareCreateStackBinding;
import it.niedermann.nextcloud.deck.model.Stack;

public class StackAdapter extends AbstractAdapter<Stack> {

    @SuppressWarnings("WeakerAccess")
    public StackAdapter(@NonNull Context context) {
        super(context, R.layout.item_prepare_create_stack);
    }

    @Override
    protected long getItemId(@NonNull Stack item) {
        return item.getLocalId();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        final ItemPrepareCreateStackBinding binding;
        if (convertView == null) {
            binding = ItemPrepareCreateStackBinding.inflate(inflater, parent, false);
        } else {
            binding = ItemPrepareCreateStackBinding.bind(convertView);
        }

        final Stack item = getItem(position);
        if (item != null) {
            binding.stackTitle.setText(item.getTitle());
        } else {
            DeckLog.logError(new IllegalArgumentException("No item for position " + position));
        }
        return binding.getRoot();
    }
}
