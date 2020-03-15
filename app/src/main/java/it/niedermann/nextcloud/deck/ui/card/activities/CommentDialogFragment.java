package it.niedermann.nextcloud.deck.ui.card.activities;

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
import it.niedermann.nextcloud.deck.databinding.DialogAddCommentBinding;

public class CommentDialogFragment extends DialogFragment {
    private AddCommentListener addCommentListener;

    private DialogAddCommentBinding binding;

    /**
     * Use newInstance()-Method
     */
    public CommentDialogFragment() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AddCommentListener) {
            this.addCommentListener = (AddCommentListener) context;
        } else {
            throw new ClassCastException("Caller must implement " + AddCommentListener.class.getCanonicalName());
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        binding = DialogAddCommentBinding.inflate(requireActivity().getLayoutInflater());

        return new AlertDialog.Builder(requireActivity(), Application.getAppTheme(getContext()) ? R.style.DialogDarkTheme : R.style.ThemeOverlay_AppCompat_Dialog_Alert)
                .setView(binding.getRoot())
                .setTitle(R.string.simple_comment)
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                    // Do something else
                })
                .setPositiveButton(R.string.simple_add, (dialog, which) -> addCommentListener.onCommentAdded(binding.input.getText().toString()))
                .create();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding.input.requestFocus();
        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public static CommentDialogFragment newInstance() {
        return new CommentDialogFragment();
    }

    public interface AddCommentListener {
        void onCommentAdded(String comment);
    }
}
