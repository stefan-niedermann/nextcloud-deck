package it.niedermann.nextcloud.deck.ui.card;

import it.niedermann.nextcloud.deck.model.full.FullCard;

public interface CreateCardListener {
    /**
     * This method is called when a new Card is created
     * @param createdCard The new Card's data
     */
    void onCardCreated(FullCard createdCard);
}