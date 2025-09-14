package it.niedermann.nextcloud.deck.feature_import;

import static android.app.Activity.RESULT_CANCELED;
import static android.os.Build.VERSION;
import static android.os.Build.VERSION_CODES;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.load.model.Headers;
import com.nextcloud.android.sso.AccountImporter;
import com.nextcloud.android.sso.exceptions.AccountImportCancelledException;
import com.nextcloud.android.sso.exceptions.AndroidGetAccountsPermissionNotGranted;
import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;
import com.nextcloud.android.sso.exceptions.NextcloudFilesAppNotInstalledException;
import com.nextcloud.android.sso.model.SingleSignOnAccount;
import com.nextcloud.android.sso.ui.UiExceptionManager;

import java.util.List;

import it.niedermann.nextcloud.deck.shared.model.Account;
import it.niedermann.nextcloud.deck.shared.model.Capabilities;

public class ImportAccountFragment extends Fragment {

    private ImportAccountViewModel importAccountViewModel;
    private ViewDataBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Thread.currentThread().setUncaughtExceptionHandler(new it.niedermann.nextcloud.deck.feature_shared.exception.ExceptionHandler(requireActivity()));
        importAccountViewModel = new ViewModelProvider(requireActivity()).get(ImportAccountViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_import_account, container, false);

        if (VERSION.SDK_INT < VERSION_CODES.S) {
            binding.image.setClipToOutline(true);
        }

        resetAvatar();

        importAccountViewModel.hasAccounts().observeOnce(this, hasAccounts -> binding.welcomeText.setText(hasAccounts
                ? getString(R.string.welcome_text_further_accounts)
                : getString(R.string.welcome_text, getString(R.string.app_name))));

        binding.addButton.setOnClickListener((v) -> {
            binding.progressText.setText("");
            binding.addButton.setEnabled(false);
            binding.updateDeckButton.setVisibility(View.GONE);
            try {
                AccountImporter.pickNewAccount(this);
            } catch (NextcloudFilesAppNotInstalledException e) {
                UiExceptionManager.showDialogForException(this, e);
                DeckLog.warn("=============================================================");
                DeckLog.warn("Nextcloud app is not installed. Cannot choose account");
                DeckLog.logError(e);
                binding.addButton.setEnabled(true);
            } catch (AndroidGetAccountsPermissionNotGranted e) {
                binding.addButton.setEnabled(true);
                AccountImporter.requestAndroidAccountPermissionsAndPickAccount(this);
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        requireActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AccountImporter.REQUEST_AUTH_TOKEN_SSO && resultCode == RESULT_CANCELED) {
            binding.addButton.setEnabled(true);
        } else {
            try {
                AccountImporter.onActivityResult(requestCode, resultCode, data, ImportAccountFragment.this, new AccountImporter.IAccountAccessGranted() {
                    @Override
                    public void accountAccessGranted(SingleSignOnAccount account) {
                        final var accountToCreate = new Account(account.name, account.userId, account.url);

                        runOnUiThread(() -> {
                            binding.progressCircular.setVisibility(View.VISIBLE);
                            binding.progressCircular.setIndeterminate(true);
                            binding.progressText.setText(R.string.progress_import_indeterminate);
                            setAvatar(accountToCreate);
                        });

                        importAccountViewModel.createAccount(accountToCreate, new IResponseCallback<>() {
                            @Override
                            public void onResponse(Account createdAccount, Headers headers) {
                                try {
                                    final var syncManager = new SyncRepository(ImportAccountFragment.this, createdAccount);

                                    syncManager.refreshCapabilities(new ResponseCallback<>(createdAccount) {
                                        @Override
                                        public void onResponse(Capabilities response, Headers headers) {
                                            if (!response.isMaintenanceEnabled()) {
                                                if (response.getDeckVersion().isSupported()) {
                                                    final var callback = new IResponseCallback<>() {
                                                        @Override
                                                        public void onResponse(Object response, Headers headers) {
                                                            var progress$ = syncManager.synchronize(new ResponseCallback<>(account) {
                                                                @Override
                                                                public void onResponse(Boolean response, Headers headers) {
                                                                    restoreWifiPref();
                                                                    SyncWorker.update(getApplicationContext());
                                                                    importAccountViewModel.saveCurrentAccount(account);
                                                                    setResult(RESULT_OK);
                                                                    finish();
                                                                }

                                                                @Override
                                                                public void onError(Throwable throwable) {
                                                                    super.onError(throwable);
                                                                    setStatusText(throwable.getMessage());
                                                                    runOnUiThread(() -> it.niedermann.nextcloud.deck.feature_shared.exception.exception.ExceptionDialogFragment.newInstance(throwable, createdAccount).show(getSupportFragmentManager(), it.niedermann.nextcloud.deck.feature_shared.exception.exception.ExceptionDialogFragment.class.getSimpleName()));
                                                                    rollbackAccountCreation(createdAccount.getId());
                                                                }
                                                            });

                                                            runOnUiThread(() -> progress$.observe(ImportAccountFragment.this, (progress) -> {
                                                                DeckLog.log("New progress value", progress.first, progress.second);
                                                                if (progress.first > 0) {
                                                                    binding.progressCircular.setIndeterminate(false);
                                                                }
                                                                if (progress.first < progress.second) {
                                                                    binding.progressText.setText(getString(R.string.progress_import, progress.first + 1, progress.second));
                                                                }
                                                                binding.progressCircular.setProgress(progress.first);
                                                                binding.progressCircular.setMax(progress.second);
                                                            }));
                                                        }
                                                    };

                                                    if (response.getDeckVersion().firstCallHasDifferentResponseStructure()) {
                                                        syncManager.fetchBoardsFromServer(new ResponseCallback<>(account) {
                                                            @Override
                                                            public void onResponse(List<FullBoard> response, Headers headers) {
                                                                callback.onResponse(createdAccount, headers);
                                                            }

                                                            @SuppressLint("MissingSuperCall")
                                                            @Override
                                                            public void onError(Throwable throwable) {
                                                                // We proceed with the import anyway. It's just important that one request has been done.
                                                                callback.onResponse(createdAccount, headers);
                                                            }
                                                        });
                                                    } else {
                                                        callback.onResponse(createdAccount, headers);
                                                    }
                                                } else {
                                                    setStatusText(getString(R.string.deck_outdated_please_update, response.getDeckVersion().getOriginalVersion()));
                                                    runOnUiThread(() -> {
                                                        binding.updateDeckButton.setOnClickListener((v) -> startActivity(new Intent(Intent.ACTION_VIEW)
                                                                .setData(Uri.parse(createdAccount.getUrl() + urlFragmentUpdateDeck))));
                                                        binding.updateDeckButton.setVisibility(View.VISIBLE);
                                                    });
                                                    rollbackAccountCreation(createdAccount.getId());
                                                }
                                            } else {
                                                setStatusText(R.string.maintenance_mode);
                                                rollbackAccountCreation(createdAccount.getId());
                                            }
                                        }

                                        @Override
                                        public void onError(Throwable throwable) {
                                            super.onError(throwable);
                                            if (throwable instanceof OfflineException o && OfflineException.Reason.OFFLINE.equals(o.getReason())) {
                                                setStatusText(R.string.you_have_to_be_connected_to_the_internet_in_order_to_add_an_account);
                                            } else {
                                                setStatusText(throwable.getMessage());
                                                runOnUiThread(() -> it.niedermann.nextcloud.deck.feature_shared.exception.exception.ExceptionDialogFragment.newInstance(throwable, createdAccount).show(getSupportFragmentManager(), it.niedermann.nextcloud.deck.feature_shared.exception.exception.ExceptionDialogFragment.class.getSimpleName()));
                                            }
                                            rollbackAccountCreation(createdAccount.getId());
                                        }
                                    });
                                } catch (NextcloudFilesAppAccountNotFoundException e) {
                                    setStatusText(e.getMessage());
                                    runOnUiThread(() -> it.niedermann.nextcloud.deck.feature_shared.exception.exception.ExceptionDialogFragment.newInstance(e, createdAccount).show(getSupportFragmentManager(), it.niedermann.nextcloud.deck.feature_shared.exception.exception.ExceptionDialogFragment.class.getSimpleName()));
                                    rollbackAccountCreation(createdAccount.getId());
                                }
                            }

                            @Override
                            public void onError(Throwable error) {
                                IResponseCallback.super.onError(error);
                                if (error instanceof SQLiteConstraintException) {
                                    DeckLog.warn("Account already added");
                                    runOnUiThread(() -> setStatusText(getString(R.string.account_already_added, accountToCreate.getName())));
                                } else {
                                    runOnUiThread(() -> {
                                        setStatusText(error.getMessage());
                                        it.niedermann.nextcloud.deck.feature_shared.exception.exception.ExceptionDialogFragment.newInstance(error, accountToCreate).show(getSupportFragmentManager(), it.niedermann.nextcloud.deck.feature_shared.exception.exception.ExceptionDialogFragment.class.getSimpleName());
                                    });
                                }
                                runOnUiThread(() -> binding.addButton.setEnabled(true));
                                restoreWifiPref();
                                resetAvatar();
                            }
                        });
                    }
                });
            } catch (AccountImportCancelledException e) {
                runOnUiThread(() -> binding.addButton.setEnabled(true));
                restoreWifiPref();
                DeckLog.info("Account import has been canceled.");
            }
        }
    }

    @Override
    public void onStop() {
        requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        this.binding = null;
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AccountImporter.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}