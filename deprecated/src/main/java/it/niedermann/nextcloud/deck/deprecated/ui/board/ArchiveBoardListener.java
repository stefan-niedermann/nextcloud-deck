package it.niedermann.nextcloud.deck.deprecated.ui.board;

import androidx.annotation.NonNull;

import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Board;

public interface ArchiveBoardListener {
    void onArchive(Board board);
    void onClone(@NonNull Account account, @NonNull Board board);
}