package it.niedermann.nextcloud.deck.ui.board.accesscontrol;

import static it.niedermann.nextcloud.deck.ui.board.accesscontrol.AccessControlAdapter.HEADER_ITEM_LOCAL_ID;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.nextcloud.android.sso.api.EmptyResponse;
import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;

import java.util.List;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.DialogBoardShareBinding;
import it.niedermann.nextcloud.deck.model.AccessControl;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.remote.api.IResponseCallback;
import it.niedermann.nextcloud.deck.repository.SyncRepository;
import it.niedermann.nextcloud.deck.ui.card.UserAutoCompleteAdapter;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionDialogFragment;
import it.niedermann.nextcloud.deck.ui.theme.ThemeUtils;
import it.niedermann.nextcloud.deck.ui.theme.ThemedSnackbar;
import it.niedermann.nextcloud.deck.ui.viewmodel.SyncViewModel;
import okhttp3.Headers;

public class AccessControlDialogFragment extends DialogFragment implements AccessControlChangedListener, OnItemClickListener {

    private AccessControlViewModel accessControlViewModel;
    private DialogBoardShareBinding binding;

    private static final String KEY_ACCOUNT = "account";
    private static final String KEY_BOARD_ID = "board_id";

    private Account account;
    private long boardId;
    private UserAutoCompleteAdapter userAutoCompleteAdapter;
    private AccessControlAdapter adapter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        final Bundle args = getArguments();

        if (args == null || !args.containsKey(KEY_ACCOUNT) || !args.containsKey(KEY_BOARD_ID)) {
            throw new IllegalArgumentException(KEY_ACCOUNT + " and " + KEY_BOARD_ID + " must be provided as arguments");
        }

        this.account = (Account) args.getSerializable(KEY_ACCOUNT);

        if (this.account == null) {
            throw new IllegalArgumentException(KEY_ACCOUNT + " must not be null");
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

        accessControlViewModel = new SyncViewModel.Provider(requireActivity(), requireActivity().getApplication(), account).get(AccessControlViewModel.class);
        final var dialogBuilder = new MaterialAlertDialogBuilder(requireContext());

        binding = DialogBoardShareBinding.inflate(requireActivity().getLayoutInflater());

        adapter = new AccessControlAdapter(account, this, requireContext());
        binding.peopleList.setAdapter(adapter);

        accessControlViewModel.getFullBoardById(account.getId(), boardId).observe(this, (FullBoard fullBoard) -> {
            if (fullBoard != null) {
                accessControlViewModel.getAccessControlByLocalBoardId(fullBoard.getAccountId(), fullBoard.getLocalId()).observe(this, (List<AccessControl> accessControlList) -> {
                    final AccessControl ownerControl = new AccessControl();
                    ownerControl.setLocalId(HEADER_ITEM_LOCAL_ID);
                    ownerControl.setUser(fullBoard.getOwner());
                    accessControlList.add(0, ownerControl);
                    adapter.update(accessControlList, fullBoard.getBoard().isPermissionManage());
                    try {
                        userAutoCompleteAdapter = new UserAutoCompleteAdapter(requireActivity(), account, boardId);
                    } catch (NextcloudFilesAppAccountNotFoundException e) {
                        ExceptionDialogFragment.newInstance(e, account).show(getChildFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
                        // TODO Handle error
                    }
                    binding.people.setAdapter(userAutoCompleteAdapter);
                    binding.people.setOnItemClickListener(this);
                });
                applyTheme(fullBoard.getBoard().getColor());
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
    public void onDestroy() {
        super.onDestroy();
        this.binding = null;
    }

    @Override
    public void updateAccessControl(AccessControl accessControl) {
        accessControlViewModel.updateAccessControl(accessControl, new IResponseCallback<>() {
            @Override
            public void onResponse(AccessControl response, Headers headers) {
                DeckLog.info("Successfully updated", AccessControl.class.getSimpleName(), "for user", accessControl.getUser().getDisplayname());
            }

            @Override
            public void onError(Throwable throwable) {
                IResponseCallback.super.onError(throwable);
                ExceptionDialogFragment.newInstance(throwable, account).show(getChildFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
            }
        });
    }

    @Override
    public void deleteAccessControl(AccessControl ac) {
        accessControlViewModel.deleteAccessControl(ac, new IResponseCallback<>() {
            @Override
            public void onResponse(EmptyResponse response, Headers headers) {
                DeckLog.info("Successfully deleted access control for user", ac.getUser().getDisplayname());
            }

            @Override
            public void onError(Throwable throwable) {
                if (SyncRepository.isNoOnVoidError(throwable)) {
                    IResponseCallback.super.onError(throwable);

                    accessControlViewModel.getCurrentBoardColor(ac.getAccountId(), ac.getBoardId())
                            .thenAcceptAsync(color -> ThemedSnackbar.make(requireView(), getString(R.string.error_revoking_ac, ac.getUser().getDisplayname()), Snackbar.LENGTH_LONG, color)
                                    .setAction(R.string.simple_more, v -> ExceptionDialogFragment.newInstance(throwable, account).show(getChildFragmentManager(), ExceptionDialogFragment.class.getSimpleName()))
                                    .show(), ContextCompat.getMainExecutor(requireContext()));
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
        ac.setType(user.getType()); // https://github.com/nextcloud/deck/blob/master/docs/API.md#post-boardsboardidacl---add-new-acl-rule
        ac.setUserId(user.getLocalId());
        ac.setUser(user);
        accessControlViewModel.createAccessControl(account, ac, new IResponseCallback<>() {
            @Override
            public void onResponse(AccessControl response, Headers headers) {
                DeckLog.info("Successfully created", AccessControl.class.getSimpleName(), "for user", user.getDisplayname());
            }

            @Override
            public void onError(Throwable throwable) {
                IResponseCallback.super.onError(throwable);
                ExceptionDialogFragment.newInstance(throwable, account).show(getChildFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
            }
        });
        binding.people.setText("");
        userAutoCompleteAdapter.exclude(user);
    }

    public void applyTheme(@ColorInt int color) {
        final var utils = ThemeUtils.of(color, requireContext());

        utils.material.colorTextInputLayout(binding.peopleWrapper);

        adapter.applyTheme(color);
    }

    public static DialogFragment newInstance(@NonNull Account account, long boardLocalId) {
        final DialogFragment dialog = new AccessControlDialogFragment();

        final Bundle args = new Bundle();
        args.putSerializable(KEY_ACCOUNT, account);
        args.putLong(KEY_BOARD_ID, boardLocalId);
        dialog.setArguments(args);

        return dialog;
    }
}