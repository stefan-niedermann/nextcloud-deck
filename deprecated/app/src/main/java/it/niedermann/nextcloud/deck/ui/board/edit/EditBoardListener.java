package it.niedermann.nextcloud.deck.ui.board.edit;

import android.content.DialogInterface;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.full.FullBoard;

public interface EditBoardListener extends DialogInterface.OnDismissListener {
    void onUpdateBoard(FullBoard fullBoard);

    default void onCreateBoard(@NonNull Account account, String title, @ColorInt int color) {
        // Creating board is not necessary
    }
}