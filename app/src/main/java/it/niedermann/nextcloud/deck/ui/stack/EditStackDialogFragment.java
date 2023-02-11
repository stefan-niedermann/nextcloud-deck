package it.niedermann.nextcloud.deck.ui.stack;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Objects;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.DialogStackCreateBinding;
import it.niedermann.nextcloud.deck.ui.theme.ThemedDialogFragment;
import it.niedermann.nextcloud.deck.ui.theme.ViewThemeUtils;

public class EditStackDialogFragment extends ThemedDialogFragment implements DialogInterface.OnClickListener {
    private static final String KEY_STACK_ID = "stack_id";
    private static final String KEY_OLD_TITLE = "old_title";
    private EditStackListener editStackListener;

    private DialogStackCreateBinding binding;

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

        final var builder = new MaterialAlertDialogBuilder(requireActivity())
                .setView(binding.getRoot())
                .setNeutralButton(android.R.string.cancel, null);

        final var args = getArguments();

        if (args == null) {
            builder.setTitle(R.string.add_list)
                    .setPositiveButton(R.string.simple_add, null);
        } else {
            binding.input.setText(args.getString(KEY_OLD_TITLE));
            builder.setTitle(R.string.rename_list)
                    .setPositiveButton(R.string.simple_rename, null);
        }
        final var dialog = builder.create();

        dialog.setOnShowListener(d -> {
            final boolean inputIsValid = inputIsValid(binding.input.getText());
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(inputIsValid);
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(v -> onClick(dialog, DialogInterface.BUTTON_POSITIVE));
        });

        binding.input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Nothing to do
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final boolean inputIsValid = inputIsValid(binding.input.getText());
                if (inputIsValid) {
                    binding.inputWrapper.setError(null);
                }
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(inputIsValid);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Nothing to do
            }
        });

        binding.input.setOnEditorActionListener((textView, actionId, event) -> {
            //noinspection SwitchStatementWithTooFewBranches
            switch (actionId) {
                case EditorInfo.IME_ACTION_DONE:
                    onClick(dialog, DialogInterface.BUTTON_POSITIVE);
                    return true;
            }
            return false;
        });


        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding.input.requestFocus();
        Objects.requireNonNull(requireDialog().getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.binding = null;
    }

    public static DialogFragment newInstance() {
        return new EditStackDialogFragment();
    }

    public static DialogFragment newInstance(long stackId, @Nullable String oldTitle) {
        final var dialog = new EditStackDialogFragment();

        final var args = new Bundle();
        args.putLong(KEY_STACK_ID, stackId);
        args.putString(KEY_OLD_TITLE, oldTitle);

        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void applyTheme(int color) {
        final var utils = ViewThemeUtils.of(color, requireContext());

        utils.material.colorTextInputLayout(binding.inputWrapper);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        final var args = getArguments();
        final var createMode = args == null;

        //noinspection SwitchStatementWithTooFewBranches
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                final var currentUserInput = binding.input.getText();
                if (inputIsValid(currentUserInput)) {
                    if (createMode) {
                        editStackListener.onCreateStack(binding.input.getText().toString());
                    } else {
                        editStackListener.onUpdateStack(args.getLong(KEY_STACK_ID), binding.input.getText().toString());
                    }
                    dismiss();
                } else {
                    binding.inputWrapper.setError(getString(R.string.title_is_mandatory));
                    binding.input.requestFocus();
                }
                break;
            default:
                throw new IllegalStateException("Unexpected button: " + which);
        }
    }

    private static boolean inputIsValid(@Nullable CharSequence input) {
        return input != null && !input.toString().trim().isEmpty();
    }
}
