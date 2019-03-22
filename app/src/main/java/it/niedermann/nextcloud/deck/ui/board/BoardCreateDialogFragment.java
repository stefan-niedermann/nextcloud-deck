package it.niedermann.nextcloud.deck.ui.board;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.DialogFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.ui.MainActivity;

public class BoardCreateDialogFragment extends DialogFragment {

    private String selectedColor;

    @BindView(R.id.input)
    EditText input;
    @BindView(R.id.colorPicker)
    LinearLayout colorPicker;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_board_create, null);

        ButterKnife.bind(this, view);
        ImageView image;
        Drawable drawable, wrapped;

        for (String color : getResources().getStringArray(R.array.board_default_colors)) {
            image = new ImageView(getContext());
            drawable = getResources().getDrawable(R.drawable.circle_grey600_24dp);
            wrapped = DrawableCompat.wrap(drawable).mutate();
            DrawableCompat.setTint(wrapped, Color.parseColor(color));
            image.setImageDrawable(wrapped);
            image.setOnClickListener((imageView) -> {
                selectedColor = color;
            });
            colorPicker.addView(image);
        }

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.create_board)
                .setView(view)
                .setNegativeButton(R.string.simple_cancel, (dialog, which) -> {
                    // Do something else
                })
                .setPositiveButton(R.string.simple_create, (dialog, which) -> {
                    ((MainActivity) getActivity()).onCreateBoard(input.getText().toString(), selectedColor);
                })
                .create();
    }
}