package it.niedermann.nextcloud.deck.deprecated.ui.card;

import androidx.annotation.NonNull;

import it.niedermann.nextcloud.deck.model.full.FullCard;

public interface SelectCardListener {
    void onCardSelected(@NonNull FullCard fullCard, long boardId);
}