package it.niedermann.nextcloud.deck.ui.card;

import android.content.DialogInterface;

import androidx.annotation.NonNull;

import it.niedermann.nextcloud.deck.model.full.FullCard;

public interface CreateCardListener extends DialogInterface.OnDismissListener {
    /**
     * This method is called when a new Card is created
     *
     * @param createdCard The new Card's data
     */
    void onCardCreated(@NonNull FullCard createdCard);

}