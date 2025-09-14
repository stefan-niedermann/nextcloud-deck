package it.niedermann.nextcloud.deck.deprecated.ui.board.edit;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.DialogTextColorInputBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.deprecated.ui.theme.ThemeUtils;
import it.niedermann.nextcloud.deck.deprecated.ui.theme.Themed;
import it.niedermann.nextcloud.deck.deprecated.util.KeyboardUtils;

public class EditBoardDialogFragment extends DialogFragment implements Themed {

    private DialogTextColorInputBinding binding;

    private static final String KEY_ACCOUNT = "account";
    private static final String KEY_BOARD_ID = "board_id";

    private EditBoardListener editBoardListener;

    private Account account;

    private FullBoard fullBoard = null;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof EditBoardListener) {
            this.editBoardListener = (EditBoardListener) context;
        } else {
            throw new ClassCastException("Caller must implement " + EditBoardListener.class.getCanonicalName());
        }

        final var args = getArguments();

        if (args == null || !args.containsKey(KEY_ACCOUNT)) {
            throw new IllegalArgumentException(KEY_ACCOUNT + " must be provided");
        }

        this.account = (Account) args.getSerializable(KEY_ACCOUNT);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DialogTextColorInputBinding.inflate(requireActivity().getLayoutInflater());

        final var builder = new MaterialAlertDialogBuilder(requireContext())
                .setView(binding.getRoot())
                .setNeutralButton(android.R.string.cancel, null);
        final var viewModel = new ViewModelProvider(requireActivity()).get(EditBoardViewModel.class);

        final var args = getArguments();

        if (args == null || (!args.containsKey(KEY_BOARD_ID) && !args.containsKey(KEY_ACCOUNT))) {
            throw new IllegalArgumentException("Bundle must at least contain " + KEY_ACCOUNT + " or " + KEY_BOARD_ID);
        }

        if (args.containsKey(KEY_BOARD_ID)) {
            final long boardId = args.getLong(KEY_BOARD_ID);
            builder.setTitle(R.string.edit_board);
            builder.setPositiveButton(R.string.simple_save, (dialog, which) -> {
                this.fullBoard.board.setColor(binding.colorChooser.getSelectedColor());
                this.fullBoard.board.setTitle(binding.input.getText().toString());
                this.editBoardListener.onUpdateBoard(fullBoard);
            });

            viewModel.getFullBoardById(account.getId(), boardId).observe(this, fullBoard -> {
                if (fullBoard.board != null) {
                    this.fullBoard = fullBoard;
                    applyTheme(fullBoard.getBoard().getColor());

                    final String title = this.fullBoard.getBoard().getTitle();
                    binding.input.setText(title);
                    binding.input.setSelection(title.length());
                    binding.colorChooser.selectColor(this.fullBoard.getBoard().getColor());
                }
            });
        } else {
            builder.setTitle(R.string.add_board);
            builder.setPositiveButton(R.string.simple_add, (dialog, which) -> editBoardListener.onCreateBoard(account, binding.input.getText().toString(), binding.colorChooser.getSelectedColor()));
            binding.colorChooser.selectColor(ContextCompat.getColor(requireContext(), R.color.board_default_color));

            viewModel.getAccountColor(account.getId()).observe(this, this::applyTheme);
        }

        return builder.create();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        KeyboardUtils.showKeyboardForEditText(binding.input);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        editBoardListener.onDismiss(dialog);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.binding = null;
    }

    @Override
    public void applyTheme(int color) {
        final var utils = ThemeUtils.of(color, requireContext());

        utils.material.colorTextInputLayout(binding.inputWrapper);
    }

    public static DialogFragment newInstance(@NonNull Account account, long boardId) {
        final DialogFragment dialog = new EditBoardDialogFragment();

        final var args = new Bundle();
        args.putSerializable(KEY_ACCOUNT, account);
        args.putLong(KEY_BOARD_ID, boardId);
        dialog.setArguments(args);

        return dialog;
    }

    public static DialogFragment newInstance(@NonNull Account account) {
        final DialogFragment dialog = new EditBoardDialogFragment();

        final var args = new Bundle();
        args.putSerializable(KEY_ACCOUNT, account);
        dialog.setArguments(args);

        return dialog;
    }
}