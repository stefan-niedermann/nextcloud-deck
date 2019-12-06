package it.niedermann.nextcloud.deck.ui.card;

import android.app.Dialog;
import android.content.Context;
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

public class CommentDialogFragment extends DialogFragment {
    private AddCommentListener addCommentListener;

    @BindView(R.id.input)
    EditText input;

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
        View view = Objects.requireNonNull(getActivity()).getLayoutInflater().inflate(R.layout.dialog_add_comment, null);
        ButterKnife.bind(this, view);

        return new AlertDialog.Builder(getActivity(), Application.getAppTheme(getContext()) ? R.style.DialogDarkTheme : R.style.ThemeOverlay_AppCompat_Dialog_Alert)
                .setView(view)
                .setTitle(R.string.simple_comment)
                .setNegativeButton(R.string.simple_cancel, (dialog, which) -> {
                    // Do something else
                })
                .setPositiveButton(R.string.simple_add, (dialog, which) -> addCommentListener.onCommentAdded(input.getText().toString()))
                .create();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        input.requestFocus();
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
