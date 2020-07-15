package it.niedermann.nextcloud.deck.ui.card.details;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemProjectBinding;
import it.niedermann.nextcloud.deck.model.ocs.projects.OcsProject;

public class CardProjectsViewHolder extends RecyclerView.ViewHolder {

    private ItemProjectBinding binding;

    public CardProjectsViewHolder(@NonNull ItemProjectBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(@NonNull OcsProject project) {
        binding.projectName.setText(project.getName());
        final int resourcesCount = project.getResources().size();
        binding.resourcesCount.setText(itemView.getContext().getResources().getQuantityString(R.plurals.resources_count, resourcesCount, resourcesCount));
    }
}
