package it.niedermann.nextcloud.deck.ui.board.managelabels;

import androidx.annotation.NonNull;

import it.niedermann.nextcloud.deck.model.Label;

public interface ManageLabelListener {
    void requestDelete(@NonNull Label label);

    void requestEdit(@NonNull Label label);
}