package it.niedermann.nextcloud.deck.ui.board.accesscontrol;

import static it.niedermann.nextcloud.deck.ui.board.accesscontrol.AccessControlAdapter.HEADER_ITEM_LOCAL_ID;
import static it.niedermann.nextcloud.deck.ui.branding.BrandingUtil.applyBrandToEditTextInputLayout;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.databinding.DialogBoardShareBinding;
import it.niedermann.nextcloud.deck.model.AccessControl;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.MainViewModel;
import it.niedermann.nextcloud.deck.ui.branding.BrandedSnackbar;
import it.niedermann.nextcloud.deck.ui.card.UserAutoCompleteAdapter;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionDialogFragment;

public class AccessControlDialogFragment extends DialogFragment implements AccessControlChangedListener, OnItemClickListener {

    private MainViewModel viewModel;
    private DialogBoardShareBinding binding;

    private static final String KEY_BOARD_ID = "board_id";

    private long boardId;
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
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext());

        binding = DialogBoardShareBinding.inflate(requireActivity().getLayoutInflater());
        adapter = new AccessControlAdapter(viewModel.getCurrentAccount(), this, requireContext());
        binding.peopleList.setAdapter(adapter);

        viewModel.getFullBoardById(viewModel.getCurrentAccount().getId(), boardId).observe(this, (FullBoard fullBoard) -> {
            if (fullBoard != null) {
                viewModel.getAccessControlByLocalBoardId(viewModel.getCurrentAccount().getId(), boardId).observe(this, (List<AccessControl> accessControlList) -> {
                    final AccessControl ownerControl = new AccessControl();
                    ownerControl.setLocalId(HEADER_ITEM_LOCAL_ID);
                    ownerControl.setUser(fullBoard.getOwner());
                    accessControlList.add(0, ownerControl);
                    adapter.update(accessControlList, fullBoard.getBoard().isPermissionManage());
                    userAutoCompleteAdapter = new UserAutoCompleteAdapter(requireActivity(), viewModel.getCurrentAccount(), boardId);
                    binding.people.setAdapter(userAutoCompleteAdapter);
                    binding.people.setOnItemClickListener(this);
                });
                applyBrand(fullBoard.getBoard().getColor());
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
        viewModel.updateAccessControl(accessControl, new IResponseCallback<>() {
            @Override
            public void onResponse(AccessControl response) {
                DeckLog.info("Successfully updated", AccessControl.class.getSimpleName(), "for user", accessControl.getUser().getDisplayname());
            }

            @Override
            public void onError(Throwable throwable) {
                IResponseCallback.super.onError(throwable);
                requireActivity().runOnUiThread(() -> ExceptionDialogFragment.newInstance(throwable, viewModel.getCurrentAccount()).show(getChildFragmentManager(), ExceptionDialogFragment.class.getSimpleName()));
            }
        });
    }

    @Override
    public void deleteAccessControl(AccessControl ac) {
        viewModel.deleteAccessControl(ac, new IResponseCallback<>() {
            @Override
            public void onResponse(Void response) {
                DeckLog.info("Successfully deleted access control for user", ac.getUser().getDisplayname());
            }

            @Override
            public void onError(Throwable throwable) {
                if (!SyncManager.ignoreExceptionOnVoidError(throwable)) {
                    IResponseCallback.super.onError(throwable);
                    requireActivity().runOnUiThread(() -> BrandedSnackbar.make(requireView(), getString(R.string.error_revoking_ac, ac.getUser().getDisplayname()), Snackbar.LENGTH_LONG)
                            .setAction(R.string.simple_more, v -> ExceptionDialogFragment.newInstance(throwable, viewModel.getCurrentAccount()).show(getChildFragmentManager(), ExceptionDialogFragment.class.getSimpleName()))
                            .show());
                }
            }
        });
        adapter.remove(ac);
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
        viewModel.createAccessControl(viewModel.getCurrentAccount().getId(), ac, new IResponseCallback<>() {
            @Override
            public void onResponse(AccessControl response) {
                DeckLog.info("Successfully created", AccessControl.class.getSimpleName(), "for user", user.getDisplayname());
            }

            @Override
            public void onError(Throwable throwable) {
                IResponseCallback.super.onError(throwable);
                requireActivity().runOnUiThread(() -> ExceptionDialogFragment.newInstance(throwable, viewModel.getCurrentAccount()).show(getChildFragmentManager(), ExceptionDialogFragment.class.getSimpleName()));
            }
        });
        binding.people.setText("");
        userAutoCompleteAdapter.exclude(user);
    }

    public void applyBrand(@ColorInt int mainColor) {
        applyBrandToEditTextInputLayout(mainColor, binding.peopleWrapper);
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