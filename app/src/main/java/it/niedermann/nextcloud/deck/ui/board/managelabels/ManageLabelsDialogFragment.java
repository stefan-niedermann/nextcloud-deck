package it.niedermann.nextcloud.deck.ui.board.managelabels;

import android.app.Dialog;
import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.Random;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.DialogBoardManageLabelsBinding;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.WrappedLiveData;
import it.niedermann.nextcloud.deck.ui.MainViewModel;
import it.niedermann.nextcloud.deck.ui.branding.BrandedAlertDialogBuilder;
import it.niedermann.nextcloud.deck.ui.branding.BrandedDeleteAlertDialogBuilder;
import it.niedermann.nextcloud.deck.ui.branding.BrandedDialogFragment;

import static it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.LiveDataHelper.observeOnce;
import static it.niedermann.nextcloud.deck.ui.branding.BrandingUtil.applyBrandToEditText;
import static it.niedermann.nextcloud.deck.ui.branding.BrandingUtil.applyBrandToFAB;

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
        final AlertDialog.Builder dialogBuilder = new BrandedAlertDialogBuilder(requireContext());
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

            WrappedLiveData<Label> createLiveData = viewModel.createLabel(viewModel.getCurrentAccount().getId(), label, boardId);
            observeOnce(createLiveData, this, (createdLabel) -> {
                if (createLiveData.hasError()) {
                    final Throwable error = createLiveData.getError();
                    assert error != null;
                    if (error instanceof SQLiteConstraintException) {
                        Toast.makeText(requireContext(), getString(R.string.tag_already_exists, label.getTitle()), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(requireContext(), error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        DeckLog.logError(error);
                    }
                } else {
                    binding.addLabelTitle.setText(null);
                    Toast.makeText(requireContext(), getString(R.string.tag_successfully_added, label.getTitle()), Toast.LENGTH_LONG).show();
                }
                binding.fab.setEnabled(true);
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
        applyBrandToEditText(mainColor, binding.addLabelTitle);
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
        observeOnce(viewModel.countCardsWithLabel(label.getLocalId()), this, (count) -> {
            if (count > 0) {
                new BrandedDeleteAlertDialogBuilder(requireContext())
                        .setTitle(getString(R.string.delete_something, label.getTitle()))
                        .setMessage(getResources().getQuantityString(R.plurals.do_you_want_to_delete_the_label, count, count))
                        .setPositiveButton(R.string.simple_delete, (dialog, which) -> deleteLabel(label))
                        .setNeutralButton(android.R.string.cancel, null)
                        .show();
            } else {
                deleteLabel(label);
            }
        });
    }

    private void deleteLabel(@NonNull Label label) {
        final WrappedLiveData<Void> deleteLiveData = viewModel.deleteLabel(label);
        observeOnce(deleteLiveData, this, (v) -> {
            if (deleteLiveData.hasError() && !SyncManager.ignoreExceptionOnVoidError(deleteLiveData.getError())) {
                final Throwable error = deleteLiveData.getError();
                assert error != null;
                Toast.makeText(requireContext(), error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                DeckLog.logError(deleteLiveData.getError());
            }
        });
    }

    @Override
    public void requestEdit(@NonNull Label label) {
        EditLabelDialogFragment.newInstance(label).show(getChildFragmentManager(), EditLabelDialogFragment.class.getCanonicalName());
    }

    @Override
    public void onLabelUpdated(@NonNull Label label) {
        WrappedLiveData<Label> updateLiveData = viewModel.updateLabel(label);
        observeOnce(updateLiveData, this, (updatedLabel) -> {
            if (updateLiveData.hasError()) {
                final Throwable error = updateLiveData.getError();
                assert error != null;
                if (error instanceof SQLiteConstraintException) {
                    Toast.makeText(requireContext(), getString(R.string.tag_already_exists, label.getTitle()), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(requireContext(), error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    DeckLog.logError(updateLiveData.getError());
                }
            }
        });
    }
}