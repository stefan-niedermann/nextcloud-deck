package it.niedermann.nextcloud.deck.ui.board;

import androidx.annotation.ColorInt;

import it.niedermann.nextcloud.deck.model.full.FullBoard;

public interface EditBoardListener {
    void onUpdateBoard(FullBoard fullBoard);

    default void onCreateBoard(String title, @ColorInt int color) {
        // Creating board is not necessary
    }
}