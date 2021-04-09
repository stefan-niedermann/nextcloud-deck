package it.niedermann.nextcloud.deck.ui.upcomingcards;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import it.niedermann.nextcloud.deck.databinding.ItemSectionBinding;

public class UpcomingCardsSectionViewHolder extends RecyclerView.ViewHolder {

    @NonNull
    private final ItemSectionBinding binding;

    public UpcomingCardsSectionViewHolder(@NonNull ItemSectionBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(@NonNull String sectionTitle) {
        this.binding.sectionTitle.setText(sectionTitle);
    }
}
