package it.niedermann.nextcloud.deck.ui.sharetarget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.Collection;

import it.niedermann.nextcloud.deck.BuildConfig;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.DialogShareProgressBinding;
import it.niedermann.nextcloud.deck.exceptions.UploadAttachmentFailedException;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.ui.branding.BrandedDialogFragment;
import it.niedermann.nextcloud.deck.ui.card.EditActivity;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionDialogFragment;
import it.niedermann.nextcloud.exception.ExceptionUtil;

import static android.graphics.PorterDuff.Mode;
import static it.niedermann.nextcloud.deck.ui.branding.BrandingUtil.getSecondaryForegroundColorDependingOnTheme;

public class ShareProgressDialogFragment extends BrandedDialogFragment {

    private DialogShareProgressBinding binding;
    private ShareProgressViewModel viewModel;

    private static final String BUNDLE_KEY_ACCOUNT = "account";
    private static final String BUNDLE_KEY_BOARD_LOCAL_ID = "boardLocalId";
    private static final String BUNDLE_KEY_CARD_LOCAL_ID = "cardLocalId";

    private Account account;
    private long boardLocalId;
    private long cardLocalId;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewModel = new ViewModelProvider(requireActivity()).get(ShareProgressViewModel.class);

        final Bundle args = requireArguments();

        if (!args.containsKey(BUNDLE_KEY_ACCOUNT) || !args.containsKey(BUNDLE_KEY_BOARD_LOCAL_ID) || !args.containsKey(BUNDLE_KEY_CARD_LOCAL_ID)) {
            throw new IllegalArgumentException("Provide at least " + BUNDLE_KEY_ACCOUNT + " and " + BUNDLE_KEY_BOARD_LOCAL_ID + " and " + BUNDLE_KEY_CARD_LOCAL_ID + " of the card that should be edited.");
        }

        account = (Account) args.getSerializable(BUNDLE_KEY_ACCOUNT);
        if (account == null) {
            throw new IllegalArgumentException(BUNDLE_KEY_ACCOUNT + " must not be null.");
        }

        cardLocalId = args.getLong(BUNDLE_KEY_CARD_LOCAL_ID);
        if (cardLocalId <= 0L) {
            throw new IllegalArgumentException(BUNDLE_KEY_CARD_LOCAL_ID + " must be a positive long but was " + cardLocalId);
        }

        boardLocalId = args.getLong(BUNDLE_KEY_BOARD_LOCAL_ID);
        if (boardLocalId <= 0L) {
            throw new IllegalArgumentException(BUNDLE_KEY_BOARD_LOCAL_ID + " must be a positive integer but was " + boardLocalId);
        }
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
                binding.proceedButton.setEnabled(true);
                binding.proceedButton.setOnClickListener((v) -> requireActivity().startActivity(EditActivity.createEditCardIntent(requireContext(), account, boardLocalId, cardLocalId)));
                final Collection<Throwable> exceptions = viewModel.getExceptions().getValue();
                if (exceptions == null || exceptions.size() == 0) {
                    requireActivity().finish();
                    binding.proceedButton.callOnClick();
                }
            } else {
                binding.proceedButton.setEnabled(false);
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
                binding.errorCounter.setVisibility(View.VISIBLE);
                binding.errorReportButton.setVisibility(View.VISIBLE);
            } else {
                binding.errorCounter.setVisibility(View.GONE);
                binding.errorReportButton.setVisibility(View.GONE);
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

    public static DialogFragment newInstance(@NonNull Account account, long boardLocalId, long cardLocalId) {
        final DialogFragment fragment = new ShareProgressDialogFragment();
        final Bundle args = new Bundle();
        args.putSerializable(BUNDLE_KEY_ACCOUNT, account);
        args.putSerializable(BUNDLE_KEY_BOARD_LOCAL_ID, boardLocalId);
        args.putSerializable(BUNDLE_KEY_CARD_LOCAL_ID, cardLocalId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void applyBrand(int mainColor) {
        binding.progress.getProgressDrawable().setColorFilter(
                getSecondaryForegroundColorDependingOnTheme(requireContext(), mainColor), Mode.SRC_IN);
        binding.errorReportButton.setTextColor(getSecondaryForegroundColorDependingOnTheme(requireContext(), mainColor));
        binding.proceedButton.setTextColor(getSecondaryForegroundColorDependingOnTheme(requireContext(), mainColor));
    }
}
