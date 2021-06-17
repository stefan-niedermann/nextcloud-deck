package it.niedermann.nextcloud.deck.ui.board;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.Objects;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.DialogTextColorInputBinding;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.ui.MainViewModel;

import static it.niedermann.nextcloud.deck.ui.branding.BrandingUtil.applyBrandToEditTextInputLayout;

public class EditBoardDialogFragment extends DialogFragment {

    private DialogTextColorInputBinding binding;

    private static final String KEY_BOARD_ID = "board_id";

    private EditBoardListener editBoardListener;

    private FullBoard fullBoard = null;

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
        binding = DialogTextColorInputBinding.inflate(requireActivity().getLayoutInflater());

        final AlertDialog.Builder builder = new AlertDialog.Builder(requireContext())
                .setView(binding.getRoot())
                .setNeutralButton(android.R.string.cancel, null);

        final Bundle args = getArguments();
        if (args != null && args.containsKey(KEY_BOARD_ID)) {
            builder.setTitle(R.string.edit_board);
            builder.setPositiveButton(R.string.simple_save, (dialog, which) -> {
                this.fullBoard.board.setColor(binding.colorChooser.getSelectedColor());
                this.fullBoard.board.setTitle(binding.input.getText().toString());
                this.editBoardListener.onUpdateBoard(fullBoard);
            });
            final MainViewModel viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
            viewModel.getFullBoardById(viewModel.getCurrentAccount().getId(), args.getLong(KEY_BOARD_ID)).observe(EditBoardDialogFragment.this, (FullBoard fb) -> {
                if (fb.board != null) {
                    this.fullBoard = fb;
                    String title = this.fullBoard.getBoard().getTitle();
                    binding.input.setText(title);
                    binding.input.setSelection(title.length());
                    applyBrandToEditTextInputLayout(fb.getBoard().getColor(), binding.inputWrapper);
                    binding.colorChooser.selectColor(fullBoard.getBoard().getColor());
                }
            });
        } else {
            builder.setTitle(R.string.add_board);
            builder.setPositiveButton(R.string.simple_add, (dialog, which) -> editBoardListener.onCreateBoard(binding.input.getText().toString(), binding.colorChooser.getSelectedColor()));
            binding.colorChooser.selectColor(ContextCompat.getColor(requireContext(), R.color.board_default_color));
        }

        return builder.create();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding.input.requestFocus();
        Objects.requireNonNull(requireDialog().getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public static DialogFragment newInstance(long boardId) {
        final DialogFragment dialog = new EditBoardDialogFragment();

        final Bundle args = new Bundle();
        args.putLong(KEY_BOARD_ID, boardId);
        dialog.setArguments(args);

        return dialog;
    }

    public static DialogFragment newInstance() {
        return new EditBoardDialogFragment();
    }
}