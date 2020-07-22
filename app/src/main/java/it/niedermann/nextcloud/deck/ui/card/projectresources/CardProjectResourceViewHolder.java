package it.niedermann.nextcloud.deck.ui.card.projectresources;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.net.URL;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemProjectResourceBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.ocs.projects.OcsProjectResource;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

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
        try {
            binding.getRoot().setOnClickListener((v) -> itemView.getContext().startActivity(new Intent(Intent.ACTION_VIEW).setData(getResourceUri(account, resource.getLink()))));
        } catch (IllegalArgumentException e) {
            DeckLog.logError(e);
        }
        binding.type.setVisibility(VISIBLE);
        switch (resource.getType()) {
            case "deck-board": {
                binding.type.setText(resources.getString(R.string.project_type_deck_board));
                break;
            }
            case "deck-card": {
                binding.type.setText(resources.getString(R.string.project_type_deck_card));
                break;
            }
            case "file": {
                binding.type.setText(resources.getString(R.string.project_type_file));
                break;
            }
            default: {
                DeckLog.warn("Unknown project type: " + resource.getType());
                binding.type.setVisibility(GONE);
                break;
            }
        }
    }

    @NonNull
    private static Uri getResourceUri(@NonNull Account account, @NonNull String link) throws IllegalArgumentException {
        try {
            // Assume link contains a fully qualified Uri including host
            final URL u = new URL(link);
            return Uri.parse(u.toString());
        } catch (Throwable linkIsNotQualified) {
            try {
                // Assume link is a absolute path that needs to be concatenated with account url for a complete Uri
                final URL u = new URL(account.getUrl() + link);
                return Uri.parse(u.toString());
            } catch (Throwable throwable) {
                throw new IllegalArgumentException("Could not parse " + Uri.class.getSimpleName() + ": " + link, throwable);
            }
        }
    }
}
