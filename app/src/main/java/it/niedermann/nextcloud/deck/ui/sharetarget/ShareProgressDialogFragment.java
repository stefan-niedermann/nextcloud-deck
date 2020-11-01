package it.niedermann.nextcloud.deck.ui.sharetarget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import it.niedermann.nextcloud.deck.BuildConfig;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.DialogShareProgressBinding;
import it.niedermann.nextcloud.deck.exceptions.UploadAttachmentFailedException;
import it.niedermann.nextcloud.deck.ui.branding.BrandedDialogFragment;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionDialogFragment;
import it.niedermann.nextcloud.exception.ExceptionUtil;

import static android.graphics.PorterDuff.Mode;
import static it.niedermann.nextcloud.deck.ui.branding.BrandingUtil.getSecondaryForegroundColorDependingOnTheme;

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
            final Integer currentMaxValue = viewModel.getMaxValue();
            if (currentMaxValue != null && progress >= currentMaxValue) {
                if (!viewModel.hasExceptions() && !viewModel.hasAlreadyDuplicateAttachments()) {
                    Toast.makeText(requireContext(), getString(R.string.share_success, String.valueOf(currentMaxValue), viewModel.targetCardTitle), Toast.LENGTH_LONG).show();
                    dismiss();
                }
            }
        });

        viewModel.getExceptions().observe(requireActivity(), (exceptions) -> {
            final int exceptionsCount = exceptions.size();
            if (exceptionsCount > 0) {
                binding.errorCounter.setText(getResources().getQuantityString(R.plurals.progress_error_count, exceptionsCount, exceptionsCount));
                binding.errorReportButton.setOnClickListener((v) -> {
                    final StringBuilder debugInfos = new StringBuilder(exceptionsCount + " attachments failed to upload:");
                    for (Throwable t : exceptions) {
                        debugInfos.append(ExceptionUtil.INSTANCE.getDebugInfos(requireContext(), t, BuildConfig.FLAVOR));
                    }
                    ExceptionDialogFragment.newInstance(new UploadAttachmentFailedException(debugInfos.toString()), null)
                            .show(getChildFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
                });
                binding.errors.setVisibility(View.VISIBLE);
            } else {
                binding.errors.setVisibility(View.GONE);
            }
        });

        viewModel.getDuplicateAttachments().observe(requireActivity(), (duplicates) -> {
            final int duplicatesCount = duplicates.size();
            if (duplicatesCount > 0) {
                final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
                binding.duplicates.removeAllViews();
                for (String duplicate : duplicates) {
                    TextView duplicateEntry = new TextView(requireContext());
                    duplicateEntry.setLayoutParams(params);
                    duplicateEntry.setText(duplicate);
                    binding.duplicates.addView(duplicateEntry);
                }
                binding.duplicatesContainer.setVisibility(View.VISIBLE);
            } else {
                binding.duplicatesContainer.setVisibility(View.GONE);
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
    public void applyBrand(int mainColor) {
        binding.progress.getProgressDrawable().setColorFilter(
                getSecondaryForegroundColorDependingOnTheme(requireContext(), mainColor), Mode.SRC_IN);
        binding.errorReportButton.setTextColor(getSecondaryForegroundColorDependingOnTheme(requireContext(), mainColor));
    }
}
