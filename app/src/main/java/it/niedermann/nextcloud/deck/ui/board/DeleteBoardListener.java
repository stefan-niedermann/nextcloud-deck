package it.niedermann.nextcloud.deck.ui.board;

import it.niedermann.nextcloud.deck.model.Board;

public interface DeleteBoardListener {
    void onBoardDeleted(Board board);
}