package it.niedermann.nextcloud.deck.ui.board.accesscontrol;

import androidx.recyclerview.widget.RecyclerView;

import it.niedermann.nextcloud.deck.databinding.ItemAccessControlBinding;

public class AccessControlViewHolder extends RecyclerView.ViewHolder {
    public ItemAccessControlBinding binding;

    @SuppressWarnings("WeakerAccess")
    public AccessControlViewHolder(ItemAccessControlBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}