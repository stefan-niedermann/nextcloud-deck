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

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, getResources().getDimensionPixelSize(R.dimen.standard_half_padding), 0);

        String[] colors = getResources().getStringArray(R.array.board_default_colors);

        for (int i = 0; i < colors.length; i++) {
            final String color = colors[i];
            image = new ImageView(getContext());
            drawable = getResources().getDrawable(R.drawable.circle_grey600_24dp);
            wrapped = DrawableCompat.wrap(drawable).mutate();
            DrawableCompat.setTint(wrapped, Color.parseColor(color));
            image.setImageDrawable(drawable);
            image.setOnClickListener((imageView) -> {
                selectedColor = color;
            });
            if(i < colors.length - 1) {
                image.setLayoutParams(lp);
            }
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