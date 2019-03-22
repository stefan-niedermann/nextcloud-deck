package it.niedermann.nextcloud.deck.ui.board;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.ui.MainActivity;
import it.niedermann.nextcloud.deck.util.ViewUtil;

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

        initColorChooser();

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

    private void initColorChooser() {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, getResources().getDimensionPixelSize(R.dimen.standard_half_padding), 0);

        String[] colors = getResources().getStringArray(R.array.board_default_colors);

        for (int i = 0; i < colors.length; i++) {
            final String color = colors[i];
            ImageView image = new ImageView(getContext());
            image.setOnClickListener((imageView) -> {
                image.setImageDrawable(ViewUtil.getTintedImageView(getContext(), R.drawable.circle_alpha_check_36dp, color));
                selectedColor = color;
            });
            image.setImageDrawable(ViewUtil.getTintedImageView(getContext(), R.drawable.circle_grey600_36dp, color));
            if (i < colors.length - 1) {
                image.setLayoutParams(lp);
            }
            colorPicker.addView(image);
        }
    }
}