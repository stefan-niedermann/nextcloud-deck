package it.niedermann.nextcloud.deck.ui.widget;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.DialogWidgetColorPickerBinding;

/**
 * Generic color picker dialog used by widget configuration screens. Unlike
 * {@link it.niedermann.nextcloud.deck.ui.card.details.PickColorDialogFragment}, this dialog is
 * not tied to a specific {@link androidx.lifecycle.ViewModel} — the result is reported back via
 * the Fragment Result API so it can be reused for several independent color settings on the same
 * screen, distinguished by {@link #resultKeyFor(String)}.
 */
public class WidgetColorPickerDialogFragment extends DialogFragment {

    private static final String ARG_RESULT_KEY = "resultKey";
    private static final String ARG_INITIAL_COLOR = "initialColor";
    private static final String ARG_ALLOW_TRANSPARENT = "allowTransparent";

    public static final String EXTRA_COLOR = "color";
    public static final String EXTRA_USE_DEFAULT = "useDefault";

    private DialogWidgetColorPickerBinding binding;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = DialogWidgetColorPickerBinding.inflate(requireActivity().getLayoutInflater());

        final var args = requireArguments();
        final String resultKey = args.getString(ARG_RESULT_KEY);
        final boolean allowTransparent = args.getBoolean(ARG_ALLOW_TRANSPARENT);
        final boolean hasInitialColor = args.containsKey(ARG_INITIAL_COLOR);
        @ColorInt final int initialColor = hasInitialColor ? args.getInt(ARG_INITIAL_COLOR) : Color.WHITE;

        if (allowTransparent) {
            binding.transparentCheckbox.setVisibility(View.VISIBLE);
            binding.transparentCheckbox.setOnCheckedChangeListener((buttonView, isChecked) ->
                    binding.colorChooser.setVisibility(isChecked ? View.GONE : View.VISIBLE));
            final boolean isTransparent = hasInitialColor && Color.alpha(initialColor) == 0;
            binding.transparentCheckbox.setChecked(isTransparent);
            binding.colorChooser.setVisibility(isTransparent ? View.GONE : View.VISIBLE);
        }

        binding.colorChooser.selectColor(hasInitialColor ? initialColor : Color.WHITE);

        return new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.choose_color)
                .setView(binding.getRoot())
                .setPositiveButton(R.string.simple_save, (dialog, which) -> {
                    final boolean transparent = allowTransparent && binding.transparentCheckbox.isChecked();
                    final int color = transparent ? Color.TRANSPARENT : binding.colorChooser.getSelectedColor();
                    final var result = new Bundle();
                    result.putInt(EXTRA_COLOR, color);
                    getParentFragmentManager().setFragmentResult(resultKeyFor(resultKey), result);
                })
                .setNeutralButton(R.string.widget_color_use_default, (dialog, which) -> {
                    final var result = new Bundle();
                    result.putBoolean(EXTRA_USE_DEFAULT, true);
                    getParentFragmentManager().setFragmentResult(resultKeyFor(resultKey), result);
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @NonNull
    public static String resultKeyFor(@NonNull String key) {
        return "widget_color_picker_result:" + key;
    }

    public static WidgetColorPickerDialogFragment newInstance(@NonNull String resultKey, @Nullable @ColorInt Integer initialColor, boolean allowTransparent) {
        final var fragment = new WidgetColorPickerDialogFragment();
        final var args = new Bundle();
        args.putString(ARG_RESULT_KEY, resultKey);
        args.putBoolean(ARG_ALLOW_TRANSPARENT, allowTransparent);
        if (initialColor != null) {
            args.putInt(ARG_INITIAL_COLOR, initialColor);
        }
        fragment.setArguments(args);
        return fragment;
    }
}
