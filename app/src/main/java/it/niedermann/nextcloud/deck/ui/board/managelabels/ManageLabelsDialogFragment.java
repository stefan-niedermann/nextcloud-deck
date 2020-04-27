package it.niedermann.nextcloud.deck.ui.board.managelabels;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.DialogBoardManageLabelsBinding;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.MainViewModel;
import it.niedermann.nextcloud.deck.ui.branding.BrandedAlertDialogBuilder;
import it.niedermann.nextcloud.deck.ui.branding.BrandedDialogFragment;

public class ManageLabelsDialogFragment extends BrandedDialogFragment {

    private MainViewModel viewModel;
    private DialogBoardManageLabelsBinding binding;

    private static final String KEY_BOARD_ID = "board_id";

    private long boardId;
    private SyncManager syncManager;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        final Bundle args = getArguments();

        if (args == null || !args.containsKey(KEY_BOARD_ID)) {
            throw new IllegalArgumentException(KEY_BOARD_ID + " must be provided as arguments");
        }

        this.boardId = args.getLong(KEY_BOARD_ID);

        if (this.boardId <= 0L) {
            throw new IllegalArgumentException(KEY_BOARD_ID + " must be a valid local id and not be less or equal 0");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        final AlertDialog.Builder dialogBuilder = new BrandedAlertDialogBuilder(requireContext());

        binding = DialogBoardManageLabelsBinding.inflate(requireActivity().getLayoutInflater());
        syncManager = new SyncManager(requireActivity());
        syncManager.getFullBoardById(viewModel.getCurrentAccount().getId(), boardId).observe(this, (FullBoard fullBoard) -> {
            if (fullBoard != null) {
            } else {
                throw new IllegalStateException("FullBoard should not be null");
            }
        });
        return dialogBuilder
                .setTitle(R.string.manage_tags)
                .setView(binding.getRoot())
                .setPositiveButton(R.string.simple_close, null)
                .create();
    }

    @Override
    public void applyBrand(int mainColor, int textColor) {
    }

    public static DialogFragment newInstance(long boardLocalId) {
        final DialogFragment dialog = new ManageLabelsDialogFragment();

        final Bundle args = new Bundle();
        args.putLong(KEY_BOARD_ID, boardLocalId);
        dialog.setArguments(args);

        return dialog;
    }
}