package it.niedermann.nextcloud.deck.ui.card.comments;

import androidx.recyclerview.widget.RecyclerView;

import it.niedermann.nextcloud.deck.databinding.ItemCommentBinding;

public class ItemCommentViewHolder extends RecyclerView.ViewHolder {
    public ItemCommentBinding binding;

    @SuppressWarnings("WeakerAccess")
    public ItemCommentViewHolder(ItemCommentBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}