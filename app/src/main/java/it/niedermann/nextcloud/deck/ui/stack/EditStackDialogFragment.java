package it.niedermann.nextcloud.deck.ui.stack;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.Objects;

import it.niedermann.nextcloud.deck.Application;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.DialogStackCreateBinding;

public class EditStackDialogFragment extends DialogFragment {
    public static final Long NO_STACK_ID = -1L;
    private static final String KEY_STACK_ID = "board_id";
    private static final String KEY_OLD_TITLE = "old_title";
    private long stackId = NO_STACK_ID;
    private EditStackListener editStackListener;

    private DialogStackCreateBinding binding;

    /**
     * Use newInstance()-Method
     */
    public EditStackDialogFragment() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof EditStackListener) {
            this.editStackListener = (EditStackListener) context;
        } else {
            throw new ClassCastException("Caller must implement " + EditStackListener.class.getCanonicalName());
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        binding = DialogStackCreateBinding.inflate(requireActivity().getLayoutInflater());

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity(), Application.getAppTheme(getContext()) ? R.style.DialogDarkTheme : R.style.ThemeOverlay_AppCompat_Dialog_Alert)
                .setView(binding.getRoot())
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                    // Do something else
                });
        if (getArguments() == null) {
            throw new IllegalArgumentException("Please add at least stack id to the arguments");
        }
        stackId = getArguments().getLong(KEY_STACK_ID);
        if (stackId == NO_STACK_ID) {
            builder.setTitle(R.string.add_column)
                    .setPositiveButton(R.string.simple_add, (dialog, which) -> editStackListener.onCreateStack(binding.input.getText().toString()));
        } else {
            binding.input.setText(getArguments().getString(KEY_OLD_TITLE));
            builder.setTitle(R.string.rename_column)
                    .setPositiveButton(R.string.simple_rename, (dialog, which) -> editStackListener.onUpdateStack(stackId, binding.input.getText().toString()));
        }
        return builder.create();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding.input.requestFocus();
        Objects.requireNonNull(requireDialog().getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public static EditStackDialogFragment newInstance(long stackId) {
        return newInstance(stackId, null);
    }

    public static EditStackDialogFragment newInstance(long stackId, String oldTitle) {
        EditStackDialogFragment dialog = new EditStackDialogFragment();

        Bundle args = new Bundle();
        args.putLong(KEY_STACK_ID, stackId);
        args.putString(KEY_OLD_TITLE, oldTitle);
        dialog.setArguments(args);

        return dialog;
    }

    public interface EditStackListener {
        void onCreateStack(String title);

        void onUpdateStack(long stackId, String title);
    }
}
