package it.niedermann.nextcloud.deck.ui.board;

import android.app.Activity;
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
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.niedermann.nextcloud.deck.Application;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.AccessControl;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.card.UserAutoCompleteAdapter;
import it.niedermann.nextcloud.deck.ui.widget.DelayedAutoCompleteTextView;

public class AccessControlDialogFragment extends DialogFragment implements
        AccessControlAdapter.AccessControlChangedListener,
        AdapterView.OnItemClickListener {

    private static final String KEY_ACCOUNT_ID = "account_id";
    private static final String KEY_BOARD_ID = "board_id";

    private long accountId;
    private long boardId;
    private SyncManager syncManager;
    private UserAutoCompleteAdapter userAutoCompleteAdapter;
    private View view;

    @BindView(R.id.peopleList)
    RecyclerView peopleList;
    @BindView(R.id.people)
    DelayedAutoCompleteTextView people;

    /**
     * Use newInstance()-Method
     */
    public AccessControlDialogFragment() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Activity activity = Objects.requireNonNull(getActivity());
        View view = activity.getLayoutInflater().inflate(R.layout.dialog_board_share, null);
        ButterKnife.bind(this, view);
        boardId = Objects.requireNonNull(getArguments()).getLong(KEY_BOARD_ID);
        accountId = Objects.requireNonNull(getArguments()).getLong(KEY_ACCOUNT_ID);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity, Application.getAppTheme(getContext()) ? R.style.DialogDarkTheme : R.style.ThemeOverlay_AppCompat_Dialog_Alert);

        if (boardId == 0L || accountId == 0L) {
            throw new IllegalArgumentException("accountId and boardId must be provided");
        } else {
            syncManager = new SyncManager(activity);
            syncManager.getAccessControlByLocalBoardId(accountId, boardId).observe(this, (List<AccessControl> accessControlList) -> {
                RecyclerView.Adapter adapter = new AccessControlAdapter(accessControlList, this, getContext());
                peopleList.setAdapter(adapter);
                userAutoCompleteAdapter = new UserAutoCompleteAdapter(this, activity, accountId, boardId);
                people.setAdapter(userAutoCompleteAdapter);
                people.setOnItemClickListener(this);
            });
        }

        this.view = view;

        return dialogBuilder
                .setView(view)
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
        ac.setPermissionEdit(true);
        ac.setBoardId(boardId);
        ac.setType(0L); // https://github.com/nextcloud/deck/blob/master/docs/API.md#post-boardsboardidacl---add-new-acl-rule
        ac.setUserId(userAutoCompleteAdapter.getItem(position).getLocalId());
        ac.setUser(userAutoCompleteAdapter.getItem(position));
        syncManager.createAccessControl(accountId, ac);
        people.setText("");
    }
}