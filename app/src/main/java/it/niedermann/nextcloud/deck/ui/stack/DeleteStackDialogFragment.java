package it.niedermann.nextcloud.deck.ui.stack;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.ui.branding.BrandedDeleteAlertDialogBuilder;

public class DeleteStackDialogFragment extends DialogFragment {

    private static final String KEY_STACK_ID = "stack_id";

    private DeleteStackListener deleteStackListener;
    private Long stackId;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof DeleteStackListener) {
            this.deleteStackListener = (DeleteStackListener) context;
        } else {
            throw new ClassCastException("Caller must implement " + DeleteStackListener.class.getCanonicalName());
        }

        if (getArguments() == null || !getArguments().containsKey(KEY_STACK_ID)) {
            throw new IllegalArgumentException("Please provide at least stack id as an argument");
        } else {
            this.stackId = getArguments().getLong(KEY_STACK_ID);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new BrandedDeleteAlertDialogBuilder(requireContext())
                .setTitle(R.string.delete_list)
                .setMessage(R.string.do_you_want_to_delete_the_current_list)
                .setPositiveButton(R.string.simple_delete, (dialog, whichButton) -> deleteStackListener.onStackDeleted(stackId))
                .setNeutralButton(android.R.string.cancel, null);
        return builder.create();
    }

    public static DialogFragment newInstance(long stackId) {
        DeleteStackDialogFragment dialog = new DeleteStackDialogFragment();

        Bundle args = new Bundle();
        args.putLong(KEY_STACK_ID, stackId);
        dialog.setArguments(args);

        return dialog;
    }
}
