package it.niedermann.nextcloud.deck.ui.card.activities;

import androidx.recyclerview.widget.RecyclerView;

import it.niedermann.nextcloud.deck.databinding.ItemActivityBinding;

public class CardActivityViewHolder extends RecyclerView.ViewHolder {
    public ItemActivityBinding binding;

    @SuppressWarnings("WeakerAccess")
    public CardActivityViewHolder(ItemActivityBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}