package it.niedermann.nextcloud.deck.ui.sharetarget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.DialogShareProgressBinding;
import it.niedermann.nextcloud.deck.ui.branding.BrandedActivity;
import it.niedermann.nextcloud.deck.ui.branding.BrandedDialogFragment;
import it.niedermann.nextcloud.deck.util.ExceptionUtil;

import static android.graphics.PorterDuff.Mode;

public class ShareProgressDialogFragment extends BrandedDialogFragment {

    private DialogShareProgressBinding binding;
    private ShareProgressViewModel viewModel;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewModel = new ViewModelProvider(requireActivity()).get(ShareProgressViewModel.class);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        binding = DialogShareProgressBinding.inflate(requireActivity().getLayoutInflater());

        return new AlertDialog.Builder(requireContext())
                .setView(binding.getRoot())
                .create();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel.getMax().observe(requireActivity(), (nextMax) -> binding.progress.setMax(nextMax));
        viewModel.getProgress().observe(requireActivity(), (progress) -> {
            binding.progress.setProgress(progress);
            binding.progressText.setText(getString(R.string.progress_count, progress, viewModel.getMaxValue()));
        });

        viewModel.getExceptions().observe(requireActivity(), (exceptions) -> {
            if (exceptions.size() > 0) {
                final StringBuilder debugInfos = new StringBuilder();
                for (Throwable exception : exceptions) {
                    debugInfos.append(ExceptionUtil.getDebugInfos(requireContext(), exception, null));
                }
                binding.stacktrace.setText(debugInfos);
                binding.stacktraceContainer.setVisibility(View.VISIBLE);
            } else {
                binding.stacktraceContainer.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        requireActivity().finish();
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        requireActivity().finish();
    }

    public static ShareProgressDialogFragment newInstance() {
        return new ShareProgressDialogFragment();
    }

    @Override
    public void applyBrand(int mainColor, int textColor) {
        binding.progress.getProgressDrawable().setColorFilter(
                BrandedActivity.getSecondaryForegroundColorDependingOnTheme(requireContext(), mainColor), Mode.SRC_IN);
    }
}
