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
    private static final String KEY_NUMBER_CARDS = "number_cards";

    private DeleteStackListener deleteStackListener;
    private long stackId;
    private int numberCards;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof DeleteStackListener) {
            this.deleteStackListener = (DeleteStackListener) context;
        } else {
            throw new ClassCastException("Caller must implement " + DeleteStackListener.class.getCanonicalName());
        }

        final Bundle args = getArguments();

        if (args == null || !args.containsKey(KEY_STACK_ID) || !args.containsKey(KEY_NUMBER_CARDS)) {
            throw new IllegalArgumentException("Please provide at least " + KEY_STACK_ID + " and " + KEY_NUMBER_CARDS + " as arguments");
        } else {
            this.stackId = args.getLong(KEY_STACK_ID);
            this.numberCards = args.getInt(KEY_NUMBER_CARDS);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new BrandedDeleteAlertDialogBuilder(requireContext())
                .setTitle(R.string.delete_list)
                .setMessage(getResources().getQuantityString(R.plurals.do_you_want_to_delete_the_current_list, numberCards, numberCards))
                .setPositiveButton(R.string.simple_delete, (dialog, whichButton) -> deleteStackListener.onStackDeleted(stackId))
                .setNeutralButton(android.R.string.cancel, null);
        return builder.create();
    }

    public static DialogFragment newInstance(long stackId, int numberCards) {
        DeleteStackDialogFragment dialog = new DeleteStackDialogFragment();

        Bundle args = new Bundle();
        args.putLong(KEY_STACK_ID, stackId);
        args.putInt(KEY_NUMBER_CARDS, numberCards);
        dialog.setArguments(args);

        return dialog;
    }
}
