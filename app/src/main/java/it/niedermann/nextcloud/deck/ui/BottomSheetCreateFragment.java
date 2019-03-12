package it.niedermann.nextcloud.deck.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import butterknife.OnClick;
import it.niedermann.nextcloud.deck.R;


public class BottomSheetCreateFragment extends BottomSheetDialogFragment {

    public BottomSheetCreateFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_create, container, false);
    }

    @OnClick(R.id.create_card)
    void onCreateCardClicked() {
        Snackbar.make(this.getView(), "Creating cards is not yet supported.", Snackbar.LENGTH_SHORT);
    }

    @OnClick(R.id.create_stack)
    void onCreateStackClicked() {
        Snackbar.make(this.getView(), "Creating stacks is not yet supported.", Snackbar.LENGTH_SHORT);
    }

    @OnClick(R.id.create_board)
    void onCreateBoardClicked() {
        Snackbar.make(this.getView(), "Creating boards is not yet supported.", Snackbar.LENGTH_SHORT);
    }
}