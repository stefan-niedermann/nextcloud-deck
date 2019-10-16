package it.niedermann.nextcloud.deck.ui.board;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.niedermann.nextcloud.deck.Application;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.AccessControl;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;

public class ShareBoardDialogFragment extends DialogFragment {

    private static final String KEY_ACCOUNT_ID = "account_id";
    private static final String KEY_BOARD_ID = "board_id";
    private static final Long NO_BOARD_ID = -1L;

    @BindView(R.id.peopleList)
    LinearLayout peopleList;

    /**
     * Use newInstance()-Method
     */
    private ShareBoardDialogFragment() {}

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Activity activity = Objects.requireNonNull(getActivity());
        View view = activity.getLayoutInflater().inflate(R.layout.dialog_board_share, null);
        ButterKnife.bind(this, view);
        Long boardId = Objects.requireNonNull(getArguments()).getLong(KEY_BOARD_ID);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity, Application.getAppTheme(getContext()) ? R.style.DialogDarkTheme : R.style.ThemeOverlay_AppCompat_Dialog_Alert);

        if (NO_BOARD_ID.equals(boardId)) {
            throw new IllegalArgumentException("boardId does not exist");
        } else {
            SyncManager syncManager = new SyncManager(activity);
            final long accountId = Objects.requireNonNull(getArguments()).getLong(KEY_ACCOUNT_ID);
            syncManager.getFullBoardById(accountId, boardId).observe(ShareBoardDialogFragment.this, (FullBoard fb) -> {
                if (fb.board != null) {
                    for(AccessControl ac : fb.getParticipants()) {
                        View v = getLayoutInflater().inflate(R.layout.fragment_board_share_user, null);
                        if(ac.getUser() != null)
                        ((TextView) v.findViewById(R.id.username)).setText(ac.getUser().getUid());
                        List<String> permissions = new ArrayList<>();
                        if(ac.isPermissionEdit()) {
                            permissions.add(getString(R.string.edit));
                        }
                        if(ac.isPermissionManage()) {
                            permissions.add(getString(R.string.simple_manage));
                        }
                        if(ac.isPermissionShare()) {
                            permissions.add(getString(R.string.simple_share));
                        }
                        ((TextView) v.findViewById(R.id.permission_list)).setText(TextUtils.join(", ", permissions));
                        peopleList.addView(v);
                    }
                }
            });
        }

        return dialogBuilder
                .setView(view)
                .setPositiveButton(R.string.simple_close, (dialog, which) -> {
                })
                .create();
    }

    public static ShareBoardDialogFragment newInstance(@NonNull Long accountId, @NonNull Long boardId) {
        ShareBoardDialogFragment dialog = new ShareBoardDialogFragment();

        Bundle args = new Bundle();
        args.putLong(KEY_ACCOUNT_ID, accountId);
        args.putLong(KEY_BOARD_ID, boardId);
        dialog.setArguments(args);

        return dialog;
    }

}