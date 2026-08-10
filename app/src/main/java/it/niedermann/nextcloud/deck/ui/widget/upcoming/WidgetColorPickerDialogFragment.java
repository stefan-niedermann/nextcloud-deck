package it.niedermann.nextcloud.deck.ui.widget.upcoming;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.DialogWidgetColorPickerBinding;

/**
 * Reusable color picker dialog for the widget appearance configuration screen.
 * Reports the chosen color (or "use default") back to the caller via the Fragment Result API.
 */
public class WidgetColorPickerDialogFragment extends DialogFragment {

    public static final String BUNDLE_KEY_COLOR = "color";
    public static final String BUNDLE_KEY_USE_DEFAULT = "use_default";

    private static final String ARG_REQUEST_KEY = "request_key";
    private static final String ARG_INITIAL_COLOR = "initial_color";

    private DialogWidgetColorPickerBinding binding;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = DialogWidgetColorPickerBinding.inflate(requireActivity().getLayoutInflater());

        final var args = requireArguments();
        final String requestKey = args.getString(ARG_REQUEST_KEY);
        final int initialColor = args.getInt(ARG_INITIAL_COLOR);
        binding.colorChooser.selectColor(initialColor);

        return new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.choose_color)
                .setView(binding.getRoot())
                .setPositiveButton(R.string.simple_save, (dialog, which) -> {
                    final var result = new Bundle();
                    result.putInt(BUNDLE_KEY_COLOR, binding.colorChooser.getSelectedColor());
                    getParentFragmentManager().setFragmentResult(requestKey, result);
                })
                .setNegativeButton(R.string.widget_appearance_use_default, (dialog, which) -> {
                    final var result = new Bundle();
                    result.putBoolean(BUNDLE_KEY_USE_DEFAULT, true);
                    getParentFragmentManager().setFragmentResult(requestKey, result);
                })
                .setNeutralButton(android.R.string.cancel, null)
                .create();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public static WidgetColorPickerDialogFragment newInstance(@NonNull String requestKey, @ColorInt int initialColor) {
        final var fragment = new WidgetColorPickerDialogFragment();
        final var args = new Bundle();
        args.putString(ARG_REQUEST_KEY, requestKey);
        args.putInt(ARG_INITIAL_COLOR, initialColor);
        fragment.setArguments(args);
        return fragment;
    }
}
