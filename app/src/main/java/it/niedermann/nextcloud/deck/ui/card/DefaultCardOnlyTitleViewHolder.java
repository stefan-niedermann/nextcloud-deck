package it.niedermann.nextcloud.deck.ui.card;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.card.MaterialCardView;

import it.niedermann.nextcloud.deck.databinding.ItemCardDefaultOnlyTitleBinding;

public class DefaultCardOnlyTitleViewHolder extends AbstractCardViewHolder {
    private ItemCardDefaultOnlyTitleBinding binding;

    @SuppressWarnings("WeakerAccess")
    public DefaultCardOnlyTitleViewHolder(@NonNull ItemCardDefaultOnlyTitleBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
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
    protected TextView getCardDueDate() {
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
    protected View getCardMenu() {
        return binding.cardMenu;
    }

    @Override
    protected MaterialCardView getCard() {
        return binding.card;
    }
}