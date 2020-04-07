package it.niedermann.nextcloud.deck.ui.card;

import androidx.recyclerview.widget.RecyclerView;

import it.niedermann.nextcloud.deck.databinding.ItemCardBinding;

public class ItemCardViewHolder extends RecyclerView.ViewHolder {
    public ItemCardBinding binding;

    @SuppressWarnings("WeakerAccess")
    public ItemCardViewHolder(ItemCardBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}