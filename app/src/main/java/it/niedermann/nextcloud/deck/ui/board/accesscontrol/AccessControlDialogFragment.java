package it.niedermann.nextcloud.deck.ui.board.accesscontrol;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.niedermann.nextcloud.deck.Application;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.DialogBoardShareBinding;
import it.niedermann.nextcloud.deck.model.AccessControl;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.card.UserAutoCompleteAdapter;

public class AccessControlDialogFragment extends DialogFragment implements AccessControlChangedListener, AdapterView.OnItemClickListener {

    private DialogBoardShareBinding binding;

    private static final String KEY_ACCOUNT_ID = "account_id";
    private static final String KEY_BOARD_ID = "board_id";

    private long accountId;
    private long boardId;
    private SyncManager syncManager;
    private UserAutoCompleteAdapter userAutoCompleteAdapter;

    /**
     * Use newInstance()-Method
     */
    public AccessControlDialogFragment() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DialogBoardShareBinding.inflate(requireActivity().getLayoutInflater());
        boardId = requireArguments().getLong(KEY_BOARD_ID);
        accountId = requireArguments().getLong(KEY_ACCOUNT_ID);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext(), Application.getAppTheme(getContext()) ? R.style.DialogDarkTheme : R.style.ThemeOverlay_AppCompat_Dialog_Alert);

        if (boardId == 0L || accountId == 0L) {
            throw new IllegalArgumentException("accountId and boardId must be provided");
        } else {
            syncManager = new SyncManager(requireActivity());
            syncManager.getFullBoardById(accountId, boardId).observe(this, (FullBoard fullBoard) -> {
                syncManager.getAccessControlByLocalBoardId(accountId, boardId).observe(this, (List<AccessControl> accessControlList) -> {
                    AccessControl ownerControl = new AccessControl();
                    ownerControl.setUser(fullBoard.getOwner());
                    accessControlList.add(0, ownerControl);
                    RecyclerView.Adapter adapter = new AccessControlAdapter(accessControlList, this, getContext());
                    binding.peopleList.setAdapter(adapter);
                    userAutoCompleteAdapter = new UserAutoCompleteAdapter(requireActivity(), accountId, boardId);
                    binding.people.setAdapter(userAutoCompleteAdapter);
                    binding.people.setOnItemClickListener(this);
                });
            });
        }

        return dialogBuilder
                .setView(binding.getRoot())
                .setPositiveButton(R.string.simple_close, null)
                .create();
    }

    public static AccessControlDialogFragment newInstance(@NonNull Long accountId, @NonNull Long boardId) {
        AccessControlDialogFragment dialog = new AccessControlDialogFragment();

        Bundle args = new Bundle();
        args.putLong(KEY_ACCOUNT_ID, accountId);
        args.putLong(KEY_BOARD_ID, boardId);
        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public void updateAccessControl(AccessControl accessControl) {
        syncManager.updateAccessControl(accessControl);
    }

    @Override
    public void deleteAccessControl(AccessControl ac) {
        // TODO implement in syncManager!
        Toast.makeText(getContext(), "Deleting user permissions is not yet supported.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AccessControl ac = new AccessControl();
        User user = userAutoCompleteAdapter.getItem(position);
        ac.setPermissionEdit(true);
        ac.setBoardId(boardId);
        ac.setType(0L); // https://github.com/nextcloud/deck/blob/master/docs/API.md#post-boardsboardidacl---add-new-acl-rule
        ac.setUserId(user.getLocalId());
        ac.setUser(user);
        syncManager.createAccessControl(accountId, ac);
        binding.people.setText("");
        userAutoCompleteAdapter.exclude(user);
    }
}