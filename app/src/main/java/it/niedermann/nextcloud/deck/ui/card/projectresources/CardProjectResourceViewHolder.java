package it.niedermann.nextcloud.deck.ui.card.projectresources;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

    /**
     * extracts the values of board- and card-ID from url.
     * Depending on what kind of url it gets, it will return a long[] of lenght 1 or 2:
     * If the url contains both values, you'll get 2, if it contains only the board, you'll get 1.
     *
     * The order is fixed here: [boardId, cardId]
     * @param url to extract from
     * @return extracted and parsed values as long[] with length 1-2
     */
    protected static long[] extractBoardIdAndCardIdFromUrl(@NonNull String url) {
        // extract important part
        String[] splitByPrefix = url.split(".*\\/index\\.php\\/apps\\/deck\\/#\\/board\\/");
        // split into board- and card part
        String[] splitBySeparator = splitByPrefix[1].split("\\/card\\/");

        // remove any unexpected stuff
        if (splitBySeparator.length > 1 && splitBySeparator[1].contains("/")) {
            splitBySeparator[1] = splitBySeparator[1].split("\\/")[0];
        }
        if (splitBySeparator.length > 0 && splitBySeparator[0].contains("/")) {
            splitBySeparator[0] = splitBySeparator[0].split("\\/")[0];
        }

        // return result
        if (splitBySeparator.length == 1) {
            return new long[] {Long.parseLong(splitBySeparator[0])};
        } else if (splitBySeparator.length == 2) {
            return new long[] {Long.parseLong(splitBySeparator[0]), Long.parseLong(splitBySeparator[1])};
        } else {
            throw new IllegalArgumentException("could not parse URL for board- and/or card-ID");
        }
    }
}
