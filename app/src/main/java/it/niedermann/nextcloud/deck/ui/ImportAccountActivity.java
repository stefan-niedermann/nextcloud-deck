package it.niedermann.nextcloud.deck.ui;

import static android.os.Build.VERSION;
import static android.os.Build.VERSION_CODES;
import static com.nextcloud.android.sso.AccountImporter.REQUEST_AUTH_TOKEN_SSO;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteConstraintException;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.nextcloud.android.sso.AccountImporter;
import com.nextcloud.android.sso.exceptions.AccountImportCancelledException;
import com.nextcloud.android.sso.exceptions.AndroidGetAccountsPermissionNotGranted;
import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;
import com.nextcloud.android.sso.exceptions.NextcloudFilesAppNotInstalledException;
import com.nextcloud.android.sso.model.SingleSignOnAccount;
import com.nextcloud.android.sso.ui.UiExceptionManager;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ActivityImportAccountBinding;
import it.niedermann.nextcloud.deck.exceptions.OfflineException;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.model.ocs.Capabilities;
import it.niedermann.nextcloud.deck.remote.SyncWorker;
import it.niedermann.nextcloud.deck.remote.api.IResponseCallback;
import it.niedermann.nextcloud.deck.remote.api.ResponseCallback;
import it.niedermann.nextcloud.deck.repository.SyncRepository;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionDialogFragment;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionHandler;
import okhttp3.Headers;

public class ImportAccountActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    private String prefKeyWifiOnly;
    private boolean originalWifiOnlyValue = false;
    private String urlFragmentUpdateDeck;

    private ImportAccountViewModel importAccountViewModel;
    private ActivityImportAccountBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.currentThread().setUncaughtExceptionHandler(new ExceptionHandler(this));

        importAccountViewModel = new ViewModelProvider(this).get(ImportAccountViewModel.class);
        binding = ActivityImportAccountBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        if (VERSION.SDK_INT < VERSION_CODES.S) {
            binding.image.setClipToOutline(true);
        }

        resetAvatar();

        prefKeyWifiOnly = getString(R.string.pref_key_wifi_only);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        urlFragmentUpdateDeck = getString(R.string.url_fragment_update_deck);

        originalWifiOnlyValue = sharedPreferences.getBoolean(prefKeyWifiOnly, false);

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
    }

    @Override
    protected void onResume() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onResume();
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onSupportNavigateUp();
        setResult(RESULT_CANCELED);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_AUTH_TOKEN_SSO && resultCode == RESULT_CANCELED) {
            binding.addButton.setEnabled(true);
        } else {
            disableWifiPref().thenAcceptAsync(v -> {
                try {
                    AccountImporter.onActivityResult(requestCode, resultCode, data, ImportAccountActivity.this, new AccountImporter.IAccountAccessGranted() {
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
                                        final var syncManager = new SyncRepository(ImportAccountActivity.this, createdAccount);

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
                                                                        runOnUiThread(() -> ExceptionDialogFragment.newInstance(throwable, createdAccount).show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName()));
                                                                        rollbackAccountCreation(createdAccount.getId());
                                                                    }
                                                                });

                                                                runOnUiThread(() -> progress$.observe(ImportAccountActivity.this, (progress) -> {
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
                                                    runOnUiThread(() -> ExceptionDialogFragment.newInstance(throwable, createdAccount).show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName()));
                                                }
                                                rollbackAccountCreation(createdAccount.getId());
                                            }
                                        });
                                    } catch (NextcloudFilesAppAccountNotFoundException e) {
                                        setStatusText(e.getMessage());
                                        runOnUiThread(() -> ExceptionDialogFragment.newInstance(e, createdAccount).show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName()));
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
                                            ExceptionDialogFragment.newInstance(error, accountToCreate).show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
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
            }, ContextCompat.getMainExecutor(this));
        }
    }

    @Override
    protected void onStop() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.binding = null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AccountImporter.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    private void rollbackAccountCreation(final long accountId) {
        DeckLog.log("Rolling back account creation for " + accountId);
        importAccountViewModel.deleteAccount(accountId);
        runOnUiThread(() -> binding.addButton.setEnabled(true));
        restoreWifiPref();
        resetAvatar();
    }

    private void resetAvatar() {
        runOnUiThread(() ->
                Glide
                        .with(binding.image.getContext())
                        .load(R.mipmap.ic_launcher)
                        .into(binding.image)
        );
    }

    private void setAvatar(@NonNull Account account) {
        runOnUiThread(() ->
                Glide
                        .with(binding.image.getContext())
                        .load(account.getAvatarUrl(binding.image.getWidth()))
                        .placeholder(R.mipmap.ic_launcher)
                        .error(R.mipmap.ic_launcher)
                        .into(binding.image)
        );
    }

    private void setStatusText(@StringRes int statusText) {
        setStatusText(getString(statusText));
    }

    private void setStatusText(String statusText) {
        runOnUiThread(() -> {
            binding.updateDeckButton.setVisibility(View.GONE);
            binding.progressCircular.setVisibility(View.GONE);
            binding.progressText.setText(statusText);
        });
    }

    @SuppressLint("ApplySharedPref")
    private CompletableFuture<Void> disableWifiPref() {
        return CompletableFuture.runAsync(() -> {
            DeckLog.info("--- Temporarily disable sync on wifi only setting");
            sharedPreferences.edit().putBoolean(prefKeyWifiOnly, false).commit();
        });
    }

    private void restoreWifiPref() {
        DeckLog.info("--- Restoring sync on wifi only setting");
        sharedPreferences
                .edit()
                .putBoolean(prefKeyWifiOnly, originalWifiOnlyValue)
                .apply();
    }

    public static Intent createIntent(@NonNull Context context) {
        return new Intent(context, ImportAccountActivity.class);
    }
}