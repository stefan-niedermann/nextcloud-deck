package it.niedermann.nextcloud.deck.ui.board;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

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
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;

public class SelectBoardDialogFragment extends DialogFragment {

    private static final String KEY_ACCOUNT_ID = "account_id";

    private OnBoardSelectedListener onBoardSelectedListener;

    private Board board = null;

    @BindView(R.id.boards)
    RecyclerView boards;

    /**
     * Use newInstance()-Method
     */
    public SelectBoardDialogFragment() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnBoardSelectedListener) {
            this.onBoardSelectedListener = (OnBoardSelectedListener) context;
        } else {
            throw new ClassCastException("Caller must implement " + OnBoardSelectedListener.class.getCanonicalName());
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Activity activity = Objects.requireNonNull(getActivity());
        View view = activity.getLayoutInflater().inflate(R.layout.dialog_board_select, null);

        long accountId = Objects.requireNonNull(getArguments()).getLong(KEY_ACCOUNT_ID);
        if(accountId == 0L) {
            throw new IllegalArgumentException("You must provide an accountId");
        }

        ButterKnife.bind(this, view);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity, Application.getAppTheme(getContext()) ? R.style.DialogDarkTheme : R.style.ThemeOverlay_AppCompat_Dialog_Alert);
        dialogBuilder.setTitle(R.string.simple_select);
        dialogBuilder.setPositiveButton(R.string.simple_select, (dialog, which) -> onBoardSelectedListener.onBoardSelected(board));
        SyncManager syncManager = new SyncManager(activity);
        syncManager.getBoards(accountId).observe(this, (List<Board> boardsList) -> {
            boards.setAdapter(new BoardAdapter(boardsList));
        });

        return dialogBuilder
                .setView(view)
                .setNegativeButton(R.string.simple_cancel, null)
                .create();
    }

    public static SelectBoardDialogFragment newInstance(@NonNull Long accountId) {
        SelectBoardDialogFragment dialog = new SelectBoardDialogFragment();

        Bundle args = new Bundle();
        args.putLong(KEY_ACCOUNT_ID, accountId);
        dialog.setArguments(args);

        return dialog;
    }

    public interface OnBoardSelectedListener {
        void onBoardSelected(Board board);
    }

}