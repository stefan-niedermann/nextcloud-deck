package it.niedermann.nextcloud.deck.ui.board.accesscontrol;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.List;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.DialogBoardShareBinding;
import it.niedermann.nextcloud.deck.model.AccessControl;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.WrappedLiveData;
import it.niedermann.nextcloud.deck.ui.branding.BrandedActivity;
import it.niedermann.nextcloud.deck.ui.branding.BrandedAlertDialogBuilder;
import it.niedermann.nextcloud.deck.ui.branding.BrandedDialogFragment;
import it.niedermann.nextcloud.deck.ui.card.UserAutoCompleteAdapter;

import static it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.LiveDataHelper.observeOnce;
import static it.niedermann.nextcloud.deck.ui.board.accesscontrol.AccessControlAdapter.HEADER_ITEM_LOCAL_ID;

public class AccessControlDialogFragment extends BrandedDialogFragment implements AccessControlChangedListener, OnItemClickListener {

    private DialogBoardShareBinding binding;

    private static final String KEY_ACCOUNT_ID = "account_id";
    private static final String KEY_BOARD_ID = "board_id";

    private long accountId;
    private long boardId;
    private SyncManager syncManager;
    private UserAutoCompleteAdapter userAutoCompleteAdapter;
    private AccessControlAdapter adapter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        final Bundle args = getArguments();

        if (args == null || !args.containsKey(KEY_BOARD_ID) || !args.containsKey(KEY_ACCOUNT_ID)) {
            throw new IllegalArgumentException(KEY_ACCOUNT_ID + " and " + KEY_BOARD_ID + " must be provided as arguments");
        }

        this.boardId = args.getLong(KEY_BOARD_ID);
        this.accountId = args.getLong(KEY_ACCOUNT_ID);

        if (this.boardId == 0L || this.accountId == 0L) {
            throw new IllegalArgumentException(KEY_ACCOUNT_ID + " and " + KEY_BOARD_ID + " must be valid localIds and not be 0");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final AlertDialog.Builder dialogBuilder = new BrandedAlertDialogBuilder(requireContext());

        binding = DialogBoardShareBinding.inflate(requireActivity().getLayoutInflater());
        adapter = new AccessControlAdapter(this, requireContext());
        binding.peopleList.setAdapter(adapter);

        syncManager = new SyncManager(requireActivity());
        syncManager.getFullBoardById(accountId, boardId).observe(this, (FullBoard fullBoard) -> {
            syncManager.getAccessControlByLocalBoardId(accountId, boardId).observe(this, (List<AccessControl> accessControlList) -> {
                final AccessControl ownerControl = new AccessControl();
                ownerControl.setLocalId(HEADER_ITEM_LOCAL_ID);
                ownerControl.setUser(fullBoard.getOwner());
                accessControlList.add(0, ownerControl);
                adapter.update(accessControlList);
                userAutoCompleteAdapter = new UserAutoCompleteAdapter(requireActivity(), accountId, boardId);
                binding.people.setAdapter(userAutoCompleteAdapter);
                binding.people.setOnItemClickListener(this);
            });
        });
        return dialogBuilder
                .setView(binding.getRoot())
                .setPositiveButton(R.string.simple_close, null)
                .create();
    }

    public static DialogFragment newInstance(@NonNull Long accountId, @NonNull Long boardId) {
        final DialogFragment dialog = new AccessControlDialogFragment();

        final Bundle args = new Bundle();
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
        final WrappedLiveData<Void> wrappedDeleteLiveData = syncManager.deleteAccessControl(ac);
        observeOnce(wrappedDeleteLiveData, this, (ignored) -> {
            if (wrappedDeleteLiveData.hasError()) {
                Toast.makeText(requireContext(), getString(R.string.error_revoking_ac, ac.getUser().getDisplayname()), Toast.LENGTH_LONG).show();
                DeckLog.logError(wrappedDeleteLiveData.getError());
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
        syncManager.createAccessControl(accountId, ac);
        binding.people.setText("");
        userAutoCompleteAdapter.exclude(user);
    }

    @Override
    public void applyBrand(int mainColor, int textColor) {
        BrandedActivity.applyBrandToEditText(mainColor, textColor, binding.people);
        this.adapter.applyBrand(mainColor, textColor);
    }
}