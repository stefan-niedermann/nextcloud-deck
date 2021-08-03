package it.niedermann.nextcloud.deck.ui.stack;

import static it.niedermann.nextcloud.deck.ui.branding.BrandingUtil.applyBrandToEditTextInputLayout;

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

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.DialogStackCreateBinding;
import it.niedermann.nextcloud.deck.ui.branding.BrandedDialogFragment;

public class EditStackDialogFragment extends BrandedDialogFragment {
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

        final var builder = new AlertDialog.Builder(requireActivity())
                .setView(binding.getRoot())
                .setNeutralButton(android.R.string.cancel, null);
        final var args = getArguments();
        if (args == null) {
            builder.setTitle(R.string.add_list)
                    .setPositiveButton(R.string.simple_add, (dialog, which) -> editStackListener.onCreateStack(binding.input.getText().toString()));
        } else {
            binding.input.setText(args.getString(KEY_OLD_TITLE));
            builder.setTitle(R.string.rename_list)
                    .setPositiveButton(R.string.simple_rename, (dialog, which) -> editStackListener.onUpdateStack(args.getLong(KEY_STACK_ID), binding.input.getText().toString()));
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
    public void applyBrand(int mainColor) {
        applyBrandToEditTextInputLayout(mainColor, binding.inputWrapper);
    }
}
