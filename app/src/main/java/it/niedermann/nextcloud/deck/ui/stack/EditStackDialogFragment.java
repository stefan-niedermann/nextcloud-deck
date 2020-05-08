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

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.DialogStackCreateBinding;
import it.niedermann.nextcloud.deck.ui.branding.BrandedActivity;
import it.niedermann.nextcloud.deck.ui.branding.BrandedAlertDialogBuilder;
import it.niedermann.nextcloud.deck.ui.branding.BrandedDialogFragment;

import static it.niedermann.nextcloud.deck.Application.NO_STACK_ID;

public class EditStackDialogFragment extends BrandedDialogFragment {
    private static final String KEY_STACK_ID = "stack_id";
    private static final String KEY_OLD_TITLE = "old_title";
    private long stackId = NO_STACK_ID;
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

        AlertDialog.Builder builder = new BrandedAlertDialogBuilder(requireActivity())
                .setView(binding.getRoot())
                .setNeutralButton(android.R.string.cancel, null);
        if (getArguments() == null) {
            throw new IllegalArgumentException("Please add at least stack id to the arguments");
        }
        stackId = getArguments().getLong(KEY_STACK_ID);
        if (stackId == NO_STACK_ID) {
            builder.setTitle(R.string.add_list)
                    .setPositiveButton(R.string.simple_add, (dialog, which) -> editStackListener.onCreateStack(binding.input.getText().toString()));
        } else {
            binding.input.setText(getArguments().getString(KEY_OLD_TITLE));
            builder.setTitle(R.string.rename_list)
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

    public static DialogFragment newInstance(long stackId) {
        return newInstance(stackId, null);
    }

    public static DialogFragment newInstance(long stackId, @Nullable String oldTitle) {
        final DialogFragment dialog = new EditStackDialogFragment();

        final Bundle args = new Bundle();
        args.putLong(KEY_STACK_ID, stackId);
        if (oldTitle != null) {
            args.putString(KEY_OLD_TITLE, oldTitle);
        }
        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public void applyBrand(int mainColor, int textColor) {
        BrandedActivity.applyBrandToEditText(mainColor, textColor, binding.input);
    }
}
