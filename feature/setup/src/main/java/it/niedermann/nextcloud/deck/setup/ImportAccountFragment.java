package it.niedermann.nextcloud.deck.setup;

import static android.app.Activity.RESULT_CANCELED;
import static android.os.Build.VERSION;
import static android.os.Build.VERSION_CODES;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.nextcloud.android.sso.AccountImporter;
import com.nextcloud.android.sso.exceptions.AccountImportCancelledException;
import com.nextcloud.android.sso.exceptions.AndroidGetAccountsPermissionNotGranted;
import com.nextcloud.android.sso.exceptions.NextcloudFilesAppNotInstalledException;
import com.nextcloud.android.sso.ui.UiExceptionManager;

import java.util.Optional;
import java.util.logging.Logger;

import it.niedermann.nextcloud.deck.feature_shared.util.AvatarUtil;
import it.niedermann.nextcloud.deck.feature_shared.util.LiveDataWrapper;
import it.niedermann.nextcloud.deck.setup.databinding.FragmentImportAccountBinding;

public class ImportAccountFragment extends Fragment {

    private static final Logger logger = Logger.getLogger(ImportAccountFragment.class.getName());

    private ImportAccountViewModel vm;
    private FragmentImportAccountBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Thread.currentThread().setUncaughtExceptionHandler(new it.niedermann.nextcloud.deck.feature_shared.exception.ExceptionHandler(requireActivity()));
        vm = new ViewModelProvider(requireActivity()).get(ImportAccountViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_import_account, container, false);
        binding.setVariable(BR.vm, vm);
        binding.setVariable(BR.fragment, this);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        return binding.getRoot();

    }

    public void requestImport(@NonNull View source) {

        logger.finest("Disable add button");
        binding.addButton.setEnabled(false);

        try {
            logger.finest("Invoking SSO account picker");
            AccountImporter.pickNewAccount(this);

        } catch (NextcloudFilesAppNotInstalledException e) {
            logger.severe(e.getMessage());
            UiExceptionManager.showDialogForException(requireContext(), e);

        } catch (AndroidGetAccountsPermissionNotGranted e) {
            logger.severe(e.getMessage());
            AccountImporter.requestAndroidAccountPermissionsAndPickAccount(requireActivity());

        }

    }

    /**
     * @noinspection deprecation
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AccountImporter.onRequestPermissionsResult(requestCode, permissions, grantResults, this);

    }

    /**
     * @noinspection deprecation
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AccountImporter.REQUEST_AUTH_TOKEN_SSO && resultCode == RESULT_CANCELED) {
            logger.finest("Enable add button");
            binding.addButton.setEnabled(true);

        } else {

            try {
                AccountImporter.onActivityResult(requestCode, resultCode, data, ImportAccountFragment.this, vm::importAccount);

                final var isImportingSubscription = vm.isImporting();
                isImportingSubscription.observe(this, isImporting -> {
                    if (!isImporting) {
                        binding.addButton.setEnabled(true);
                        isImportingSubscription.removeObservers(getViewLifecycleOwner());
                    }
                });

            } catch (AccountImportCancelledException e) {
                logger.finest("Enable add button");
                binding.addButton.setEnabled(true);
            }
        }
    }

    @Override
    public void onDestroy() {
        binding = null;
        vm = null;
        super.onDestroy();
    }

    @BindingAdapter("avatar")
    public static void avatar(ImageView view, @NonNull ImportStateWrapper importState) {
        final var logo = it.niedermann.nextcloud.deck.feature_shared.R.drawable.logo;
        if (importState.hasNoValue()) {
            Glide.with(view)
                    .load(logo)
                    .into(view);
        } else {
            final var value = importState.getValue();
            assert value != null;
            Glide.with(view)
                    .load(AvatarUtil.getInstance().getAvatarUrl(value.getAccountName(), value.getUrl(), value.getUserName(), view.getWidth()))
                    .placeholder(logo)
                    .error(logo)
                    .into(view);
        }
    }

    @BindingAdapter("clipToOutline")
    public static void clipToOutline(View view, Boolean clip) {
        if (VERSION.SDK_INT < VERSION_CODES.S) {
            view.setClipToOutline(clip);
        }
    }

    @BindingAdapter("progress")
    public static void progress(ProgressBar progressBar, @Nullable ImportStateWrapper importStateWrapper) {

        Optional.ofNullable(importStateWrapper)
                .filter(LiveDataWrapper::hasNoError)
                .map(LiveDataWrapper::getValue)
                .ifPresentOrElse(value -> {

                    final var indeterminate = value.getBoardsDone() == 0 &&
                                              value.getBoardsWip() == 0 &&
                                              value.getBoardsTotal() == 0;

                    progressBar.setIndeterminate(indeterminate);
                    progressBar.setMax(value.getBoardsTotal());
                    progressBar.setProgress(value.getBoardsDone());
                    progressBar.setSecondaryProgress(value.getBoardsDone() + value.getBoardsWip());
                    progressBar.setVisibility(View.VISIBLE);

                }, () -> progressBar.setVisibility(View.GONE));
    }
}