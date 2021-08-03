package it.niedermann.nextcloud.deck.ui.card.comments;

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
import it.niedermann.nextcloud.deck.databinding.DialogAddCommentBinding;
import it.niedermann.nextcloud.deck.ui.branding.BrandedDialogFragment;

public class CardCommentsEditDialogFragment extends BrandedDialogFragment {
    private static final String BUNDLE_KEY_COMMENT_ID = "commentId";
    private static final String BUNDLE_KEY_COMMENT_MESSAGE = "commentMessage";
    private CommentEditedListener addCommentListener;

    private DialogAddCommentBinding binding;
    private Bundle args;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        args = getArguments();
        if (args == null || !args.containsKey(BUNDLE_KEY_COMMENT_ID)) {
            throw new IllegalArgumentException("Please provide at least local comment id");
        }
        if (getParentFragment() instanceof CommentEditedListener) {
            this.addCommentListener = (CommentEditedListener) getParentFragment();
        } else if (context instanceof CommentEditedListener) {
            this.addCommentListener = (CommentEditedListener) context;
        } else {
            throw new ClassCastException("Context or parent fragment must implement " + CommentEditedListener.class.getCanonicalName());
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        binding = DialogAddCommentBinding.inflate(requireActivity().getLayoutInflater());

        return new AlertDialog.Builder(requireActivity())
                .setView(binding.getRoot())
                .setTitle(R.string.simple_comment)
                .setNeutralButton(android.R.string.cancel, null)
                .setPositiveButton(R.string.simple_update, (dialog, which) -> addCommentListener.onCommentEdited(requireArguments().getLong(BUNDLE_KEY_COMMENT_ID), binding.input.getText().toString()))
                .create();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (args.containsKey(BUNDLE_KEY_COMMENT_MESSAGE)) {
            binding.input.setText(args.getString(BUNDLE_KEY_COMMENT_MESSAGE));
        }
        binding.input.requestFocus();
        Objects.requireNonNull(requireDialog().getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public static DialogFragment newInstance(@NonNull Long commentLocalId, String message) {
        final var fragment = new CardCommentsEditDialogFragment();
        final var args = new Bundle();
        args.putLong(BUNDLE_KEY_COMMENT_ID, commentLocalId);
        args.putString(BUNDLE_KEY_COMMENT_MESSAGE, message);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void applyBrand(int mainColor) {
        applyBrandToEditTextInputLayout(mainColor, binding.inputWrapper);
    }
}

