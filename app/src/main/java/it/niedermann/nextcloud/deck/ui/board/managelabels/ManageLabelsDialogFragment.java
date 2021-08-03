package it.niedermann.nextcloud.deck.ui.board.managelabels;

import static it.niedermann.nextcloud.deck.ui.branding.BrandingUtil.applyBrandToEditTextInputLayout;
import static it.niedermann.nextcloud.deck.ui.branding.BrandingUtil.applyBrandToFAB;

import android.app.Dialog;
import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.Random;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.databinding.DialogBoardManageLabelsBinding;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.MainViewModel;
import it.niedermann.nextcloud.deck.ui.branding.BrandedDialogFragment;
import it.niedermann.nextcloud.deck.ui.branding.DeleteAlertDialogBuilder;

public class ManageLabelsDialogFragment extends BrandedDialogFragment implements ManageLabelListener, EditLabelListener {

    private MainViewModel viewModel;
    private DialogBoardManageLabelsBinding binding;
    private ManageLabelsAdapter adapter;
    private String[] colors;

    private static final String KEY_BOARD_ID = "board_id";

    private long boardId;

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
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext());
        binding = DialogBoardManageLabelsBinding.inflate(requireActivity().getLayoutInflater());
        colors = getResources().getStringArray(R.array.board_default_colors);
        adapter = new ManageLabelsAdapter(this, requireContext());
        binding.labels.setAdapter(adapter);
        viewModel.getFullBoardById(viewModel.getCurrentAccount().getId(), boardId).observe(this, (fullBoard) -> {
            if (fullBoard == null) {
                throw new IllegalStateException("FullBoard should not be null");
            }
            this.adapter.update(fullBoard.getLabels());
        });

        binding.fab.setOnClickListener((v) -> {
            binding.fab.setEnabled(false);
            final Label label = new Label();
            label.setBoardId(boardId);
            label.setTitle(binding.addLabelTitle.getText().toString());
            label.setColor(colors[new Random().nextInt(colors.length)]);

            viewModel.createLabel(viewModel.getCurrentAccount().getId(), label, boardId, new IResponseCallback<>() {
                @Override
                public void onResponse(Label response) {
                    requireActivity().runOnUiThread(() -> {
                        binding.fab.setEnabled(true);
                        binding.addLabelTitle.setText(null);
                    });
                    toastFromThread(getString(R.string.tag_successfully_added, label.getTitle()));
                }

                @Override
                public void onError(Throwable throwable) {
                    requireActivity().runOnUiThread(() -> binding.fab.setEnabled(true));
                    if (throwable instanceof SQLiteConstraintException) {
                        toastFromThread(getString(R.string.tag_already_exists, label.getTitle()));
                    } else {
                        toastFromThread(throwable.getLocalizedMessage());
                        IResponseCallback.super.onError(throwable);
                    }
                }
            });
        });
        binding.addLabelTitle.setOnEditorActionListener((v, actionId, event) -> binding.fab.performClick());
        return dialogBuilder
                .setTitle(R.string.manage_tags)
                .setView(binding.getRoot())
                .setPositiveButton(R.string.simple_close, null)
                .create();
    }

    @Override
    public void applyBrand(int mainColor) {
        applyBrandToFAB(mainColor, binding.fab);
        applyBrandToEditTextInputLayout(mainColor, binding.addLabelTitleWrapper);
    }

    public static DialogFragment newInstance(long boardLocalId) {
        final DialogFragment dialog = new ManageLabelsDialogFragment();

        final Bundle args = new Bundle();
        args.putLong(KEY_BOARD_ID, boardLocalId);
        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public void requestDelete(@NonNull Label label) {
        viewModel.countCardsWithLabel(label.getLocalId(), (count) -> requireActivity().runOnUiThread(() -> {
            if (count > 0) {
                new DeleteAlertDialogBuilder(requireContext())
                        .setTitle(getString(R.string.delete_something, label.getTitle()))
                        .setMessage(getResources().getQuantityString(R.plurals.do_you_want_to_delete_the_label, count, count))
                        .setPositiveButton(R.string.simple_delete, (dialog, which) -> deleteLabel(label))
                        .setNeutralButton(android.R.string.cancel, null)
                        .show();
            } else {
                deleteLabel(label);
            }
        }));
    }

    private void deleteLabel(@NonNull Label label) {
        viewModel.deleteLabel(label, new IResponseCallback<>() {
            @Override
            public void onResponse(Void response) {
                DeckLog.info("Successfully deleted label", label.getTitle());
            }

            @Override
            public void onError(Throwable throwable) {
                if (!SyncManager.ignoreExceptionOnVoidError(throwable)) {
                    IResponseCallback.super.onError(throwable);
                    toastFromThread(throwable.getLocalizedMessage());
                }
            }
        });
    }

    @Override
    public void requestEdit(@NonNull Label label) {
        EditLabelDialogFragment.newInstance(label).show(getChildFragmentManager(), EditLabelDialogFragment.class.getCanonicalName());
    }

    @Override
    public void onLabelUpdated(@NonNull Label label) {
        viewModel.updateLabel(label, new IResponseCallback<>() {
            @Override
            public void onResponse(Label label) {
                DeckLog.info("Successfully update label", label.getTitle());
            }

            @Override
            public void onError(Throwable error) {
                if (error instanceof SQLiteConstraintException) {
                    toastFromThread(getString(R.string.tag_already_exists, label.getTitle()));
                } else {
                    IResponseCallback.super.onError(error);
                    toastFromThread(error.getLocalizedMessage());
                }
            }
        });
    }

    /**
     * Ensures that the {@param message} gets toasted on the {@link UiThread} to avoid <a href="https://github.com/stefan-niedermann/nextcloud-deck/issues/917">crashes</a>.
     */
    @AnyThread
    private void toastFromThread(@Nullable String message) {
        requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show());
    }
}