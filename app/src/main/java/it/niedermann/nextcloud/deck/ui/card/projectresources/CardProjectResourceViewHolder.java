package it.niedermann.nextcloud.deck.ui.card.projectresources;

import android.content.Intent;
import android.content.res.Resources;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemProjectResourceBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.ocs.projects.OcsProjectResource;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static it.niedermann.nextcloud.deck.util.ProjectUtil.getResourceUri;

public class CardProjectResourceViewHolder extends RecyclerView.ViewHolder {
    @NonNull
    private final ItemProjectResourceBinding binding;

    public CardProjectResourceViewHolder(@NonNull ItemProjectResourceBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(@NonNull Account account, @NonNull OcsProjectResource resource) {
        final Resources resources = itemView.getResources();
        binding.name.setText(resource.getName());
        final @Nullable String link = resource.getLink();
        if (link != null) {
            try {
                binding.getRoot().setOnClickListener((v) -> itemView.getContext().startActivity(new Intent(Intent.ACTION_VIEW).setData(getResourceUri(account, resource.getLink()))));
            } catch (IllegalArgumentException e) {
                DeckLog.logError(e);
            }
        }
        binding.type.setVisibility(VISIBLE);
        if (resource.getType() != null) {
            switch (resource.getType()) {
                case "deck": {
                    // TODO https://github.com/stefan-niedermann/nextcloud-deck/issues/671
                    binding.type.setText(resources.getString(R.string.project_type_deck_board));
                    break;
                }
                case "deck-card": {
                    // TODO https://github.com/stefan-niedermann/nextcloud-deck/issues/671
                    binding.type.setText(resources.getString(R.string.project_type_deck_card));
                    break;
                }
                case "file": {
                    binding.type.setText(resources.getString(R.string.project_type_file));
                    break;
                }
                case "room": {
                    binding.type.setText(resources.getString(R.string.project_type_room));
                    break;
                }
                default: {
                    DeckLog.info("Unknown resource type for " + resource.getName() + ": " + resource.getType());
                    binding.type.setVisibility(GONE);
                    break;
                }
            }
        } else {
            DeckLog.warn("Resource type for " + resource.getName() + " is null");
            binding.type.setVisibility(GONE);
        }
    }
}
