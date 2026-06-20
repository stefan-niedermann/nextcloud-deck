package it.niedermann.nextcloud.deck.ui.card.details;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.RecyclerView;

import it.niedermann.nextcloud.deck.databinding.ItemDependentBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Card;

public class DependentViewHolder extends RecyclerView.ViewHolder {
    private final ItemDependentBinding binding;

    @SuppressWarnings("WeakerAccess")
    public DependentViewHolder(ItemDependentBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(@NonNull Account account, @NonNull Card card, @Nullable Consumer<Card> onClickListener) {
        binding.cardTitle.setText(card.getTitle());
        if (onClickListener != null) {
            itemView.setOnClickListener((v) -> onClickListener.accept(card));
        }
    }
}