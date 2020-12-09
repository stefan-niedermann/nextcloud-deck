package it.niedermann.nextcloud.deck.ui.board.managelabels;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.DialogTextColorInputBinding;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.ui.branding.BrandedAlertDialogBuilder;
import it.niedermann.nextcloud.deck.ui.branding.BrandedDialogFragment;

import static it.niedermann.nextcloud.deck.ui.branding.BrandingUtil.applyBrandToEditText;

public class EditLabelDialogFragment extends BrandedDialogFragment {

    private DialogTextColorInputBinding binding;

    private static final String KEY_LABEL = "label";

    private EditLabelListener listener;

    private Label label = null;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof EditLabelListener) {
            this.listener = (EditLabelListener) getParentFragment();
        } else {
            throw new ClassCastException("ParentFragment must implement " + EditLabelListener.class.getCanonicalName());
        }

        final Bundle args = getArguments();

        if (args == null) {
            throw new IllegalArgumentException("Provide at least " + KEY_LABEL);
        }

        final Label label = (Label) args.getSerializable(KEY_LABEL);
        if (label == null) {
            throw new IllegalArgumentException(KEY_LABEL + " must not be null");
        }
        this.label = new Label(label);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DialogTextColorInputBinding.inflate(requireActivity().getLayoutInflater());

        AlertDialog.Builder dialogBuilder = new BrandedAlertDialogBuilder(requireContext());

        dialogBuilder.setTitle(getString(R.string.edit_tag, label.getTitle()));
        dialogBuilder.setPositiveButton(R.string.simple_save, (dialog, which) -> {
            this.label.setColor(binding.colorChooser.getSelectedColor());
            this.label.setTitle(binding.input.getText().toString());
            listener.onLabelUpdated(this.label);
        });
        String title = this.label.getTitle();
        binding.input.setText(title);
        binding.input.setSelection(title.length());
        binding.colorChooser.selectColor(this.label.getColor());

        return dialogBuilder
                .setView(binding.getRoot())
                .setNeutralButton(android.R.string.cancel, null)
                .create();
    }

    public static DialogFragment newInstance(@NonNull Label label) {
        final DialogFragment dialog = new EditLabelDialogFragment();

        final Bundle args = new Bundle();
        args.putSerializable(KEY_LABEL, label);
        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public void applyBrand(int mainColor) {
        applyBrandToEditText(mainColor, binding.input);
    }
}