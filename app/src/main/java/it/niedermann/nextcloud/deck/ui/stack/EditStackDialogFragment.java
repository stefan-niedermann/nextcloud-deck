package it.niedermann.nextcloud.deck.ui.stack;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.niedermann.nextcloud.deck.Application;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.ui.MainActivity;

public class EditStackDialogFragment extends DialogFragment {
    public static final Long NO_STACK_ID = -1L;
    private static final String KEY_STACK_ID = "board_id";
    private long stackId = NO_STACK_ID;

    @BindView(R.id.input)
    EditText input;

    /**
     * Use newInstance()-Method
     */
    private EditStackDialogFragment() {}

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = Objects.requireNonNull(getActivity()).getLayoutInflater().inflate(R.layout.dialog_stack_create, null);
        ButterKnife.bind(this, view);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), Application.getAppTheme(getContext()) ? R.style.DialogDarkTheme : R.style.ThemeOverlay_AppCompat_Dialog_Alert)
                .setTitle(R.string.add_column)
                .setView(view)
                .setNegativeButton(R.string.simple_cancel, (dialog, which) -> {
                    // Do something else
                });
        if (getArguments() != null) {
            stackId = getArguments().getLong(KEY_STACK_ID);
            if (stackId == NO_STACK_ID) {
                builder.setPositiveButton(R.string.simple_add, (dialog, which) -> {
                    ((MainActivity) getActivity()).onCreateStack(input.getText().toString());
                });
            } else {
                builder.setPositiveButton(R.string.simple_rename, (dialog, which) -> {
                    ((MainActivity) getActivity()).onUpdateStack(stackId, input.getText().toString());
                });
            }
        } else {
            builder.setPositiveButton(R.string.simple_add, (dialog, which) -> {
                ((MainActivity) getActivity()).onCreateStack(input.getText().toString());
            });
        }
        return builder.create();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        input.requestFocus();
        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public static EditStackDialogFragment newInstance(long stackId) {
        EditStackDialogFragment dialog = new EditStackDialogFragment();

        Bundle args = new Bundle();
        args.putLong(KEY_STACK_ID, stackId);
        dialog.setArguments(args);

        return dialog;
    }
}
