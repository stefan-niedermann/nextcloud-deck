package it.niedermann.nextcloud.deck.ui.board;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.DialogBoardCreateBinding;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.branding.BrandedActivity;
import it.niedermann.nextcloud.deck.ui.branding.BrandedDialogFragment;

import static it.niedermann.nextcloud.deck.Application.NO_BOARD_ID;

public class EditBoardDialogFragment extends BrandedDialogFragment {

    private DialogBoardCreateBinding binding;

    private static final String KEY_ACCOUNT_ID = "account_id";
    private static final String KEY_BOARD_ID = "board_id";

    private EditBoardListener editBoardListener;

    private FullBoard fullBoard = null;

    /**
     * Use newInstance()-Method
     */
    public EditBoardDialogFragment() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof EditBoardListener) {
            this.editBoardListener = (EditBoardListener) context;
        } else {
            throw new ClassCastException("Caller must implement " + EditBoardListener.class.getCanonicalName());
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DialogBoardCreateBinding.inflate(requireActivity().getLayoutInflater());

        long boardId = requireArguments().getLong(KEY_BOARD_ID);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext());

        if (boardId == NO_BOARD_ID) {
            dialogBuilder.setTitle(R.string.add_board);
            dialogBuilder.setPositiveButton(R.string.simple_add, (dialog, which) -> editBoardListener.onCreateBoard(binding.input.getText().toString(), binding.colorChooser.getSelectedColor()));
            binding.colorChooser.selectColor(String.format("#%06X", 0xFFFFFF & getResources().getColor(R.color.board_default_color)));
        } else {
            dialogBuilder.setTitle(R.string.edit_board);
            dialogBuilder.setPositiveButton(R.string.simple_save, (dialog, which) -> {
                this.fullBoard.board.setColor(binding.colorChooser.getSelectedColor().substring(1));
                this.fullBoard.board.setTitle(binding.input.getText().toString());
                editBoardListener.onUpdateBoard(fullBoard);
            });
            new SyncManager(requireActivity()).getFullBoardById(requireArguments().getLong(KEY_ACCOUNT_ID), boardId).observe(EditBoardDialogFragment.this, (FullBoard fb) -> {
                if (fb.board != null) {
                    this.fullBoard = fb;
                    String title = this.fullBoard.getBoard().getTitle();
                    binding.input.setText(title);
                    binding.input.setSelection(title.length());
                    binding.colorChooser.selectColor("#" + fullBoard.getBoard().getColor());
                }
            });
        }

        return dialogBuilder
                .setView(binding.getRoot())
                .setNegativeButton(android.R.string.cancel, null)
                .create();
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

    @Override
    public void applyBrand(int mainColor, int textColor) {
        super.applyBrand(mainColor, textColor);
        BrandedActivity.applyBrandToEditText(mainColor, textColor, binding.input);
    }
}