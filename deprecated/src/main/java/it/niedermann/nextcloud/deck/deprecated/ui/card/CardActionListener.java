package it.niedermann.nextcloud.deck.deprecated.ui.card;

import androidx.annotation.NonNull;

import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.model.full.FullCard;

public interface CardActionListener {

    void onArchive(@NonNull FullCard fullCard);

    void onDelete(@NonNull FullCard fullCard);

    void onAssignCurrentUser(@NonNull FullCard fullCard);

    void onUnassignCurrentUser(@NonNull FullCard fullCard);

    void onMove(@NonNull FullBoard fullBoard, @NonNull FullCard fullCard);

    void onShareLink(@NonNull FullBoard fullBoard, @NonNull FullCard fullCard);

    void onShareContent(@NonNull FullCard fullCard);
}
