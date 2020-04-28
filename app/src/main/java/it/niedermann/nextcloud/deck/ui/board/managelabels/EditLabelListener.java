package it.niedermann.nextcloud.deck.ui.board.managelabels;

import androidx.annotation.NonNull;

import it.niedermann.nextcloud.deck.model.Label;

public interface EditLabelListener {
    void onLabelUpdated(@NonNull Label label);
}