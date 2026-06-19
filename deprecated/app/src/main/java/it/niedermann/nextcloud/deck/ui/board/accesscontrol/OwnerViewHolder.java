package it.niedermann.nextcloud.deck.ui.board.accesscontrol;

import androidx.recyclerview.widget.RecyclerView;

import it.niedermann.nextcloud.deck.databinding.ItemAccessControlOwnerBinding;

public class OwnerViewHolder extends RecyclerView.ViewHolder {
    public ItemAccessControlOwnerBinding binding;

    @SuppressWarnings("WeakerAccess")
    public OwnerViewHolder(ItemAccessControlOwnerBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}