package it.niedermann.nextcloud.deck.ui.board.accesscontrol;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.DialogBoardShareBinding;
import it.niedermann.nextcloud.deck.model.AccessControl;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.WrappedLiveData;
import it.niedermann.nextcloud.deck.ui.MainViewModel;
import it.niedermann.nextcloud.deck.ui.branding.BrandedAlertDialogBuilder;
import it.niedermann.nextcloud.deck.ui.branding.BrandedDialogFragment;
import it.niedermann.nextcloud.deck.ui.branding.BrandedSnackbar;
import it.niedermann.nextcloud.deck.ui.card.UserAutoCompleteAdapter;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionDialogFragment;

import static it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.LiveDataHelper.observeOnce;
import static it.niedermann.nextcloud.deck.ui.board.accesscontrol.AccessControlAdapter.HEADER_ITEM_LOCAL_ID;
import static it.niedermann.nextcloud.deck.ui.branding.BrandingUtil.applyBrandToEditText;

public class AccessControlDialogFragment extends BrandedDialogFragment implements AccessControlChangedListener, OnItemClickListener {

    private MainViewModel viewModel;
    private DialogBoardShareBinding binding;

    private static final String KEY_BOARD_ID = "board_id";

    private long boardId;
    private SyncManager syncManager;
    private UserAutoCompleteAdapter userAutoCompleteAdapter;
    private AccessControlAdapter adapter;

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

        binding = DialogBoardShareBinding.inflate(requireActivity().getLayoutInflater());
        adapter = new AccessControlAdapter(viewModel.getCurrentAccount(), this, requireContext());
        binding.peopleList.setAdapter(adapter);

        syncManager = new SyncManager(requireActivity());
        syncManager.getFullBoardById(viewModel.getCurrentAccount().getId(), boardId).observe(this, (FullBoard fullBoard) -> {
            if (fullBoard != null) {
                syncManager.getAccessControlByLocalBoardId(viewModel.getCurrentAccount().getId(), boardId).observe(this, (List<AccessControl> accessControlList) -> {
                    final AccessControl ownerControl = new AccessControl();
                    ownerControl.setLocalId(HEADER_ITEM_LOCAL_ID);
                    ownerControl.setUser(fullBoard.getOwner());
                    accessControlList.add(0, ownerControl);
                    adapter.update(accessControlList, fullBoard.getBoard().isPermissionManage());
                    userAutoCompleteAdapter = new UserAutoCompleteAdapter(requireActivity(), viewModel.getCurrentAccount(), boardId);
                    binding.people.setAdapter(userAutoCompleteAdapter);
                    binding.people.setOnItemClickListener(this);
                });
            } else {
                // Happens when someone revokes his own access → board gets deleted locally → LiveData fires, but no board
                // see https://github.com/stefan-niedermann/nextcloud-deck/issues/410
                dismiss();
            }
        });
        return dialogBuilder
                .setTitle(R.string.share_board)
                .setView(binding.getRoot())
                .setPositiveButton(R.string.simple_close, null)
                .create();
    }

    @Override
    public void updateAccessControl(AccessControl accessControl) {
        WrappedLiveData<AccessControl> updateLiveData = syncManager.updateAccessControl(accessControl);
        observeOnce(updateLiveData, requireActivity(), (next) -> {
            if (updateLiveData.hasError()) {
                ExceptionDialogFragment.newInstance(updateLiveData.getError(), viewModel.getCurrentAccount()).show(getChildFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
            }
        });
    }

    @Override
    public void deleteAccessControl(AccessControl ac) {
        final WrappedLiveData<Void> wrappedDeleteLiveData = syncManager.deleteAccessControl(ac);
        adapter.remove(ac);
        observeOnce(wrappedDeleteLiveData, this, (ignored) -> {
            if (wrappedDeleteLiveData.hasError() && !SyncManager.ignoreExceptionOnVoidError(wrappedDeleteLiveData.getError())) {
                DeckLog.logError(wrappedDeleteLiveData.getError());
                BrandedSnackbar.make(requireView(), getString(R.string.error_revoking_ac, ac.getUser().getDisplayname()), Snackbar.LENGTH_LONG)
                        .setAction(R.string.simple_more, v -> ExceptionDialogFragment.newInstance(wrappedDeleteLiveData.getError(), viewModel.getCurrentAccount()).show(getChildFragmentManager(), ExceptionDialogFragment.class.getSimpleName()))
                        .show();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final AccessControl ac = new AccessControl();
        final User user = userAutoCompleteAdapter.getItem(position);
        ac.setPermissionEdit(true);
        ac.setBoardId(boardId);
        ac.setType(0L); // https://github.com/nextcloud/deck/blob/master/docs/API.md#post-boardsboardidacl---add-new-acl-rule
        ac.setUserId(user.getLocalId());
        ac.setUser(user);
        final WrappedLiveData<AccessControl> createLiveData = syncManager.createAccessControl(viewModel.getCurrentAccount().getId(), ac);
        observeOnce(createLiveData, this, (next) -> {
            if (createLiveData.hasError()) {
                ExceptionDialogFragment.newInstance(createLiveData.getError(), viewModel.getCurrentAccount()).show(getChildFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
            }
        });
        binding.people.setText("");
        userAutoCompleteAdapter.exclude(user);
    }

    @Override
    public void applyBrand(int mainColor) {
        applyBrandToEditText(mainColor, binding.people);
        this.adapter.applyBrand(mainColor);
    }

    public static DialogFragment newInstance(long boardLocalId) {
        final DialogFragment dialog = new AccessControlDialogFragment();

        final Bundle args = new Bundle();
        args.putLong(KEY_BOARD_ID, boardLocalId);
        dialog.setArguments(args);

        return dialog;
    }
}