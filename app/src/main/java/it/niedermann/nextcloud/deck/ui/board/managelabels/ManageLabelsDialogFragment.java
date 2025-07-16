package it.niedermann.nextcloud.deck.ui.board.managelabels;

import android.app.Dialog;
import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Lifecycle;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Random;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.DialogBoardManageLabelsBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.repository.SyncRepository;
import it.niedermann.nextcloud.deck.ui.theme.DeleteAlertDialogBuilder;
import it.niedermann.nextcloud.deck.ui.theme.ThemeUtils;
import it.niedermann.nextcloud.deck.ui.theme.ThemedDialogFragment;
import it.niedermann.nextcloud.deck.ui.viewmodel.SyncViewModel;

public class ManageLabelsDialogFragment extends ThemedDialogFragment implements ManageLabelListener, EditLabelListener {

    private LabelsViewModel labelsViewModel;
    private DialogBoardManageLabelsBinding binding;
    private ManageLabelsAdapter adapter;
    private String[] colors;

    private static final String KEY_ACCOUNT = "account";
    private static final String KEY_BOARD_ID = "board_id";

    private Account account;
    private long boardId;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        final Bundle args = getArguments();

        if (args == null || !args.containsKey(KEY_ACCOUNT) || !args.containsKey(KEY_BOARD_ID)) {
            throw new IllegalArgumentException(KEY_ACCOUNT + " and " + KEY_BOARD_ID + " must be provided as arguments");
        }

        this.account = (Account) args.getSerializable(KEY_ACCOUNT);

        if (this.account == null) {
            throw new IllegalStateException(KEY_ACCOUNT + " must not be null");
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

        labelsViewModel = new SyncViewModel.Provider(requireActivity(), requireActivity().getApplication(), account).get(LabelsViewModel.class);
        final var dialogBuilder = new MaterialAlertDialogBuilder(requireContext());
        binding = DialogBoardManageLabelsBinding.inflate(requireActivity().getLayoutInflater());
        colors = getResources().getStringArray(R.array.board_default_colors);
        adapter = new ManageLabelsAdapter(this, requireContext());
        binding.labels.setAdapter(adapter);
        labelsViewModel.getLabelsByBoardId(boardId).observe(this, this.adapter::update);

        binding.fab.setOnClickListener((v) -> {
            binding.fab.setEnabled(false);
            final Label label = new Label();
            label.setBoardId(boardId);
            label.setTitle(binding.addLabelTitle.getText().toString());
            label.setColor(colors[new Random().nextInt(colors.length)]);

            labelsViewModel.createLabel(label, boardId)
                    .whenCompleteAsync((createdLabel, exception) -> {
                        if (exception == null) {
                            if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.CREATED)) {
                                binding.fab.setEnabled(true);
                                binding.addLabelTitle.setText(null);
                            }
                            Toast.makeText(requireContext(), getString(R.string.tag_successfully_added, label.getTitle()), Toast.LENGTH_LONG).show();
                        } else {
                            if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.CREATED)) {
                                binding.fab.setEnabled(true);
                            }

                            if (exception instanceof SQLiteConstraintException) {
                                Toast.makeText(requireContext(), getString(R.string.tag_already_exists, label.getTitle()), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(requireContext(), exception.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }, ContextCompat.getMainExecutor(requireContext()));
        });
        binding.addLabelTitle.setOnEditorActionListener((v, actionId, event) -> binding.fab.performClick());
        return dialogBuilder
                .setTitle(R.string.manage_tags)
                .setView(binding.getRoot())
                .setPositiveButton(R.string.simple_close, null)
                .create();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.binding = null;
    }

    @Override
    public void applyTheme(int color) {
        final var utils = ThemeUtils.of(color, requireContext());

        utils.material.themeFAB(binding.fab);
        utils.material.colorTextInputLayout(binding.addLabelTitleWrapper);
    }

    public static DialogFragment newInstance(@NonNull Account account, long boardLocalId) {
        final DialogFragment dialog = new ManageLabelsDialogFragment();

        final Bundle args = new Bundle();
        args.putSerializable(KEY_ACCOUNT, account);
        args.putLong(KEY_BOARD_ID, boardLocalId);
        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public void requestDelete(@NonNull Label label) {
        labelsViewModel.countCardsWithLabel(label.getLocalId())
                .whenCompleteAsync((count, exception) -> {
                    if (exception == null) {

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

                    } else {
                        Toast.makeText(requireContext(), exception.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                }, ContextCompat.getMainExecutor(requireContext()));
    }

    private void deleteLabel(@NonNull Label label) {
        labelsViewModel.deleteLabel(label).whenCompleteAsync((v, exception) -> {

            if (exception == null)
                DeckLog.info("Successfully deleted label", label.getTitle());

            else if (SyncRepository.isNoOnVoidError(exception))
                Toast.makeText(requireContext(), exception.getLocalizedMessage(), Toast.LENGTH_LONG).show();

        });
    }

    @Override
    public void requestEdit(@NonNull Label label) {
        EditLabelDialogFragment.newInstance(label).show(getChildFragmentManager(), EditLabelDialogFragment.class.getCanonicalName());
    }

    @Override
    public void onLabelUpdated(@NonNull Label label) {
        labelsViewModel.updateLabel(label).whenCompleteAsync((updatedLabel, exception) -> {

            if (exception == null)
                DeckLog.info("Successfully update label", updatedLabel.getTitle());

            else if (exception instanceof SQLiteConstraintException)
                Toast.makeText(requireContext(), getString(R.string.tag_already_exists, updatedLabel.getTitle()), Toast.LENGTH_LONG).show();

            else
                Toast.makeText(requireContext(), exception.getLocalizedMessage(), Toast.LENGTH_LONG).show();

        });
    }
}