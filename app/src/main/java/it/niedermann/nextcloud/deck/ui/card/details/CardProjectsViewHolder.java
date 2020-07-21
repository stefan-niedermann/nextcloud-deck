package it.niedermann.nextcloud.deck.ui.card.details;

import android.view.View.OnClickListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemProjectBinding;
import it.niedermann.nextcloud.deck.model.ocs.projects.full.OcsProjectWithResources;

public class CardProjectsViewHolder extends RecyclerView.ViewHolder {

    private ItemProjectBinding binding;

    public CardProjectsViewHolder(@NonNull ItemProjectBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(@NonNull OcsProjectWithResources project, @Nullable OnClickListener onClickListener) {
        binding.projectName.setText(project.getName());
        final int resourcesCount = project.getResources().size();
        binding.resourcesCount.setText(itemView.getContext().getResources().getQuantityString(R.plurals.resources_count, resourcesCount, resourcesCount));
        if (resourcesCount > 0) {
            binding.getRoot().setOnClickListener(onClickListener);
        } else {
            binding.getRoot().setOnClickListener(null);
        }
    }
}
