package it.niedermann.nextcloud.deck.ui.board;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.MainActivity;
import it.niedermann.nextcloud.deck.ui.helper.colorchooser.ColorChooser;

public class EditBoardDialogFragment extends DialogFragment {

    private static final String KEY_ACCOUNT_ID = "account_id";
    private static final String KEY_BOARD_ID = "board_id";
    private static final Long NO_BOARD_ID = -1L;

    private FullBoard fullBoard = null;

    @BindView(R.id.input)
    EditText boardTitle;

    @BindView(R.id.colorChooser)
    ColorChooser colorChooser;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Activity activity = Objects.requireNonNull(getActivity());
        View view = activity.getLayoutInflater().inflate(R.layout.dialog_board_create, null);
        ButterKnife.bind(this, view);
        Long boardId = Objects.requireNonNull(getArguments()).getLong(KEY_BOARD_ID);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);

        if (NO_BOARD_ID.equals(boardId)) {
            dialogBuilder.setTitle(R.string.create_board);
            dialogBuilder.setPositiveButton(R.string.simple_create, (dialog, which) -> ((MainActivity) getActivity()).onCreateBoard(boardTitle.getText().toString(), colorChooser.getSelectedColor()));
            this.colorChooser.selectColor(String.format("#%06X", 0xFFFFFF & getResources().getColor(R.color.board_default_color)));
        } else {
            dialogBuilder.setTitle(R.string.edit_board);
            dialogBuilder.setPositiveButton(R.string.simple_save, (dialog, which) -> {
                this.fullBoard.board.setColor(colorChooser.getSelectedColor().substring(1));
                this.fullBoard.board.setTitle(this.boardTitle.getText().toString());
                ((MainActivity) getActivity()).onUpdateBoard(fullBoard);
            });
            new SyncManager(activity).getFullBoardById(Objects.requireNonNull(getArguments()).getLong(KEY_ACCOUNT_ID), boardId).observe(EditBoardDialogFragment.this, (FullBoard fb) -> {
                if (fb.board != null) {
                    this.fullBoard = fb;
                    String title = this.fullBoard.getBoard().getTitle();
                    this.boardTitle.setText(title);
                    this.boardTitle.setSelection(title.length());
                    this.colorChooser.selectColor("#" + fullBoard.getBoard().getColor());
                }
            });
        }

        return dialogBuilder
                .setView(view)
                .setNegativeButton(R.string.simple_cancel, (dialog, which) -> {
                })
                .create();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        boardTitle.requestFocus();
        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public static EditBoardDialogFragment newInstance(@NonNull Long accountId, @NonNull Long boardId) {
        EditBoardDialogFragment dialog = new EditBoardDialogFragment();

        Bundle args = new Bundle();
        args.putLong(KEY_ACCOUNT_ID, accountId);
        args.putLong(KEY_BOARD_ID, boardId);
        dialog.setArguments(args);

        return dialog;
    }

    public static EditBoardDialogFragment newInstance() {
        EditBoardDialogFragment dialog = new EditBoardDialogFragment();

        Bundle args = new Bundle();
        args.putLong(KEY_BOARD_ID, NO_BOARD_ID);
        dialog.setArguments(args);

        return dialog;
    }

}