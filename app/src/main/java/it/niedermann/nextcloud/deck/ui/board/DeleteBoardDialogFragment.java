package it.niedermann.nextcloud.deck.ui.board;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.ui.branding.BrandedDeleteAlertDialogBuilder;

public class DeleteBoardDialogFragment extends DialogFragment {

    private static final String KEY_BOARD = "board";

    private DeleteBoardListener deleteBoardListener;
    private Board board;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof DeleteBoardListener) {
            this.deleteBoardListener = (DeleteBoardListener) context;
        } else {
            throw new ClassCastException("Caller must implement " + DeleteBoardListener.class.getCanonicalName());
        }

        if (getArguments() == null || !getArguments().containsKey(KEY_BOARD)) {
            throw new IllegalArgumentException("Please provide at least " + KEY_BOARD + " as an argument");
        } else {
            this.board = (Board) getArguments().getSerializable(KEY_BOARD);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new BrandedDeleteAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.delete_something, board.getTitle()))
                .setMessage(R.string.delete_board_message)
                .setPositiveButton(R.string.simple_delete, (dialog, which) -> deleteBoardListener.onBoardDeleted(board))
                .setNeutralButton(android.R.string.cancel, null);
        return builder.create();
    }

    public static DialogFragment newInstance(@NonNull Board board) {
        final DeleteBoardDialogFragment dialog = new DeleteBoardDialogFragment();

        final Bundle args = new Bundle();
        args.putSerializable(KEY_BOARD, board);
        dialog.setArguments(args);

        return dialog;
    }
}
