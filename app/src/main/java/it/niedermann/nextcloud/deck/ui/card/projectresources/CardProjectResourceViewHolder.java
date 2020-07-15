package it.niedermann.nextcloud.deck.ui.card.projectresources;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import it.niedermann.nextcloud.deck.databinding.ItemProjectResourceBinding;
import it.niedermann.nextcloud.deck.model.ocs.projects.OcsProjectResource;

public class CardProjectResourceViewHolder extends RecyclerView.ViewHolder {
    @NonNull
    private final ItemProjectResourceBinding binding;

    public CardProjectResourceViewHolder(@NonNull ItemProjectResourceBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(@NonNull OcsProjectResource ocsProjectResource) {

    }
}
