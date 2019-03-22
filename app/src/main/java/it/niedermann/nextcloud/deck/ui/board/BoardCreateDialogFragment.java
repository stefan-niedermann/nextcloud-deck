package it.niedermann.nextcloud.deck.ui.board;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.ui.MainActivity;

public class BoardCreateDialogFragment extends DialogFragment {

    @BindView(R.id.input)
    EditText input;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_board_create, null);
        ButterKnife.bind(this, view);
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.create_board)
                .setView(view)
                .setNegativeButton(R.string.simple_cancel, (dialog, which) -> {
                    // Do something else
                })
                .setPositiveButton(R.string.simple_create, (dialog, which) -> {
                    ((MainActivity) getActivity()).onCreateBoard(input.getText().toString());
                })
                .create();
    }
}