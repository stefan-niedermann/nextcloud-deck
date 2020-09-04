package it.niedermann.nextcloud.deck.ui.card.projectresources;

import android.content.Intent;
import android.content.res.Resources;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemProjectResourceBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.ocs.projects.OcsProjectResource;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.card.EditActivity;
import it.niedermann.nextcloud.deck.util.ProjectUtil;

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

    public void bind(@NonNull Account account, @NonNull OcsProjectResource resource, @NonNull LifecycleOwner owner) {
        final Resources resources = itemView.getResources();
        binding.name.setText(resource.getName());
        final @Nullable String link = resource.getLink();
        binding.type.setVisibility(VISIBLE);
        final SyncManager syncManager = new SyncManager(itemView.getContext());
        if (resource.getType() != null) {
            switch (resource.getType()) {
                case "deck": {
                    binding.type.setText(resources.getString(R.string.project_type_deck_board));
                    // TODO https://github.com/stefan-niedermann/nextcloud-deck/issues/671
                    linkifyViewHolder(account, link);
                    break;
                }
                case "deck-card": {
                    try {
                        long[] ids = ProjectUtil.extractBoardIdAndCardIdFromUrl(link);
                        if (ids.length == 2) {
                            syncManager.synchronizeCardByRemoteId(ids[1], account).observe(owner, (fullCard) -> {
                                if (fullCard != null) {
                                    syncManager.getBoard(account.getId(), ids[0]).observe(owner, (board) -> {
                                        if (board != null) {
                                            binding.getRoot().setOnClickListener((v) -> itemView.getContext().startActivity(EditActivity.createEditCardIntent(itemView.getContext(), account, board.getLocalId(), fullCard.getLocalId())));
                                        } else {
                                            linkifyViewHolder(account, link);
                                        }
                                    });
                                } else {
                                    linkifyViewHolder(account, link);
                                }
                            });
                        } else {
                            linkifyViewHolder(account, link);
                        }
                    } catch (IllegalArgumentException e) {
                        DeckLog.logError(e);
                        linkifyViewHolder(account, link);
                    }
                    binding.type.setText(resources.getString(R.string.project_type_deck_card));
                    break;
                }
                case "file": {
                    binding.type.setText(resources.getString(R.string.project_type_file));
                    linkifyViewHolder(account, link);
                    break;
                }
                case "room": {
                    binding.type.setText(resources.getString(R.string.project_type_room));
                    linkifyViewHolder(account, link);
                    break;
                }
                default: {
                    DeckLog.info("Unknown resource type for " + resource.getName() + ": " + resource.getType());
                    binding.type.setVisibility(GONE);
                    linkifyViewHolder(account, link);
                    break;
                }
            }
        } else {
            DeckLog.warn("Resource type for " + resource.getName() + " is null");
            binding.type.setVisibility(GONE);
        }
    }

    private void linkifyViewHolder(@NonNull Account account, @Nullable String link) {
        if (link != null) {
            try {
                binding.getRoot().setOnClickListener((v) -> itemView.getContext().startActivity(new Intent(Intent.ACTION_VIEW).setData(getResourceUri(account, link))));
            } catch (IllegalArgumentException e) {
                DeckLog.logError(e);
            }
        }
    }
}
