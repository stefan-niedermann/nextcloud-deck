package it.niedermann.nextcloud.deck.ui.card;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.MenuRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.card.MaterialCardView;

import java.util.List;

import it.niedermann.nextcloud.deck.databinding.ItemCardCompactBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.ui.theme.ThemeUtils;
import it.niedermann.nextcloud.deck.ui.view.DueDateChip;

public class CompactCardViewHolder extends AbstractCardViewHolder {
    private final ItemCardCompactBinding binding;
    private final int maxCoverImagesCount;

    @SuppressWarnings("WeakerAccess")
    public CompactCardViewHolder(@NonNull ItemCardCompactBinding binding, int maxCoverImagesCount) {
        super(binding.getRoot());
        this.binding = binding;
        this.maxCoverImagesCount = maxCoverImagesCount;
    }

    /**
     * Removes all {@link OnClickListener} and {@link OnLongClickListener}
     */
    @Override
    public void bind(@NonNull FullCard fullCard, @NonNull Account account, @Nullable Long boardRemoteId, boolean hasEditPermission, @MenuRes int optionsMenu, @NonNull CardOptionsItemSelectedListener optionsItemsSelectedListener, @NonNull String counterMaxValue, @Nullable ThemeUtils utils) {
        super.bind(fullCard, account, boardRemoteId, hasEditPermission, optionsMenu, optionsItemsSelectedListener, counterMaxValue, utils);

        setupCoverImages(account, binding.coverImages, fullCard, Math.min(maxCoverImagesCount, 1));

        final List<Label> labels = fullCard.getLabels();
        if (labels != null && labels.size() > 0) {
            binding.labels.updateLabels(labels);
            binding.labels.setVisibility(View.VISIBLE);
        } else {
            binding.labels.removeAllViews();
            binding.labels.setVisibility(View.GONE);
        }
    }

    public void bindCardClickListener(@Nullable OnClickListener l) {
        binding.card.setOnClickListener(l);
    }

    public void bindCardLongClickListener(@Nullable OnLongClickListener l) {
        binding.card.setOnLongClickListener(l);
    }

    public MaterialCardView getDraggable() {
        return binding.card;
    }

    @Override
    protected DueDateChip getCardDueDate() {
        return binding.cardDueDate;
    }

    @Override
    protected ImageView getNotSyncedYet() {
        return binding.notSyncedYet;
    }

    @Override
    protected TextView getCardTitle() {
        return binding.cardTitle;
    }

    @Override
    protected ImageView getCardMenu() {
        return binding.cardMenu;
    }

    @Override
    protected MaterialCardView getCard() {
        return binding.card;
    }
}