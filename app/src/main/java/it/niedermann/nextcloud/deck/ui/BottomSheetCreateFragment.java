package it.niedermann.nextcloud.deck.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import it.niedermann.nextcloud.deck.R;


public class BottomSheetCreateFragment extends BottomSheetDialogFragment {
    private Unbinder unbinder;

    public BottomSheetCreateFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_create, container, false);
        unbinder = ButterKnife.bind(this, view);

        return view;
    }

    @OnClick(R.id.create_card)
    void onCreateCardClicked() {
        this.dismiss();
        Snackbar.make(this.getActivity().findViewById(R.id.drawer_layout), "Creating cards is not yet supported.", Snackbar.LENGTH_LONG).show();
    }

    @OnClick(R.id.create_stack)
    void onCreateStackClicked() {
        this.dismiss();
        Snackbar.make(this.getActivity().findViewById(R.id.drawer_layout), "Creating stacks is not yet supported.", Snackbar.LENGTH_LONG).show();
    }

    @OnClick(R.id.create_board)
    void onCreateBoardClicked() {
        this.dismiss();
        Snackbar.make(this.getActivity().findViewById(R.id.drawer_layout), "Creating boards is not yet supported.", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}