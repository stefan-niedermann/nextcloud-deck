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
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.ui.MainActivity;

public class EditStackDialogFragment extends DialogFragment {
    private static final String KEY_STACK_ID = "board_id";
    private static final Long NO_STACK_ID = -1L;

    @BindView(R.id.input)
    EditText input;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if(getArguments() != null) {
            long stackId = getArguments().getLong(KEY_STACK_ID);
        }
        View view = Objects.requireNonNull(getActivity()).getLayoutInflater().inflate(R.layout.dialog_stack_create, null);
        ButterKnife.bind(this, view);
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.add_column)
                .setView(view)
                .setNegativeButton(R.string.simple_cancel, (dialog, which) -> {
                    // Do something else
                })
                .setPositiveButton(R.string.simple_add, (dialog, which) -> {
                    ((MainActivity) getActivity()).onCreateStack(input.getText().toString());
                })
                .create();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        input.requestFocus();
        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
