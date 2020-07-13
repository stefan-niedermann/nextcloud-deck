package it.niedermann.nextcloud.deck.ui.board;

import it.niedermann.nextcloud.deck.model.Board;

public interface ArchiveBoardListener {
    void onArchive(Board board);
    void onClone(Board board);
}