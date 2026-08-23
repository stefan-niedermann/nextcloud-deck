package it.niedermann.nextcloud.deck.ui.card.details;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.DialogColorPickerBinding;
import it.niedermann.nextcloud.deck.ui.card.EditCardViewModel;

public class PickColorDialogFragment extends DialogFragment {

    private static final String KEY_INITIAL_COLOR = "initial_color";

    private DialogColorPickerBinding binding;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = DialogColorPickerBinding.inflate(requireActivity().getLayoutInflater());
        final var viewModel = new ViewModelProvider(requireActivity()).get(EditCardViewModel.class);

        final var args = getArguments();
        if (args != null && args.containsKey(KEY_INITIAL_COLOR)) {
            binding.colorChooser.selectColor(args.getInt(KEY_INITIAL_COLOR));
        }

        return new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.choose_color)
                .setView(binding.getRoot())
                .setPositiveButton(R.string.simple_save, (dialog, which) -> viewModel.setCardColor(binding.colorChooser.getSelectedColor()))
                .setNegativeButton(R.string.label_clear_color, (dialog, which) -> viewModel.setCardColor(null))
                .setNeutralButton(android.R.string.cancel, null)
                .create();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public static PickColorDialogFragment newInstance(@Nullable @ColorInt Integer initialColor) {
        final var fragment = new PickColorDialogFragment();
        if (initialColor != null) {
            final var args = new Bundle();
            args.putInt(KEY_INITIAL_COLOR, initialColor);
            fragment.setArguments(args);
        }
        return fragment;
    }
}
