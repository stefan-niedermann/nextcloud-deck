package it.niedermann.nextcloud.deck.ui.card.attachments.previewdialog;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static it.niedermann.nextcloud.deck.DeckApplication.isDarkTheme;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.RequestBuilder;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.DialogPreviewBinding;

public class PreviewDialog extends DialogFragment {

    private DialogPreviewBinding binding;
    private PreviewDialogViewModel viewModel;
    private LiveData<RequestBuilder<?>> imageBuilder$;
    private LiveData<String> title$;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity()).get(PreviewDialogViewModel.class);
        binding = DialogPreviewBinding.inflate(LayoutInflater.from(requireContext()));

        final var context = requireContext();

        this.imageBuilder$ = this.viewModel.getImageBuilder();
        this.imageBuilder$.observe(requireActivity(), builder -> {
            if (builder == null) {
                binding.avatar.setVisibility(GONE);
            } else {
                final var circularProgressDrawable = new CircularProgressDrawable(context);
                circularProgressDrawable.setStrokeWidth(5f);
                circularProgressDrawable.setCenterRadius(30f);
                circularProgressDrawable.setColorSchemeColors(isDarkTheme(context) ? Color.LTGRAY : Color.DKGRAY);
                circularProgressDrawable.start();
                binding.avatar.setVisibility(VISIBLE);
                binding.avatar.post(() -> builder
                        .placeholder(circularProgressDrawable)
                        .into(binding.avatar));
            }
        });
        this.title$ = this.viewModel.getTitle();
        this.title$.observe(requireActivity(), title -> {
            if (TextUtils.isEmpty(title)) {
                binding.title.setVisibility(GONE);
            } else {
                binding.title.setVisibility(VISIBLE);
                binding.title.setText(title);
            }
        });

        return new AlertDialog.Builder(requireContext())
                .setPositiveButton(R.string.simple_attach, (d, w) -> {
                    viewModel.setResult(true);
                    dismiss();
                })
                .setNeutralButton(R.string.simple_close, (d, w) -> {
                    viewModel.setResult(false);
                    dismiss();
                })
                .setView(binding.getRoot())
                .create();
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        viewModel.setResult(false);
        super.onCancel(dialog);
    }

    @Override
    public void onDestroy() {
        this.imageBuilder$.removeObservers(requireActivity());
        this.title$.removeObservers(requireActivity());
        super.onDestroy();
        this.binding = null;
    }

    public static DialogFragment newInstance() {
        return new PreviewDialog();
    }
}
