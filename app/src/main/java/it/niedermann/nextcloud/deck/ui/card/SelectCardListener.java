package it.niedermann.nextcloud.deck.ui.card;

import it.niedermann.nextcloud.deck.model.full.FullCard;

public interface SelectCardListener {
    void onCardSelected(FullCard fullCard);
}