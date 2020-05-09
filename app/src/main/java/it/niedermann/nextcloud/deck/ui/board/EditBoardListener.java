package it.niedermann.nextcloud.deck.ui.board;

import it.niedermann.nextcloud.deck.model.full.FullBoard;

public interface EditBoardListener {
    void onUpdateBoard(FullBoard fullBoard);

    default void onCreateBoard(String title, String color) {
        // Creating board is not necessary
    }
}